#!/usr/bin/env bash
#
# seed-idc-demo.sh — Seed the IDC demo tenant with a focused, demo-ready dataset
#
# Creates: 12 departments, 5 demo users, 1 published job, 10 applications
#          at various pipeline stages, 1 talent pool, 1 agency
#
# Prerequisites:
#   - psql, aws, jq, curl, python3 installed
#   - AWS CLI configured with credentials
#   - Database connection details
#   - The idc-demo tenant already exists in the database
#
# Usage:
#   export DB_HOST="..." DB_NAME="..." DB_USER="..." DB_PASSWORD="..."
#   export COGNITO_USER_POOL_ID="af-south-1_XXXXXXX"
#   export COGNITO_CLIENT_ID="xxxxxxxxxxxxxxxxxxxxxxxxxx"
#   export ADMIN_PASSWORD="Demo@2026"
#   ./scripts/seed-idc-demo.sh
#
set -euo pipefail

# ============================================================
# Section 0: Prerequisites & Config
# ============================================================

# Check required tools
for cmd in aws jq curl python3; do
  command -v "$cmd" >/dev/null 2>&1 || { echo "ERROR: $cmd is required but not found" >&2; exit 1; }
done
# psql only needed when SQL clearing is enabled
if [ -z "${SKIP_SQL_CLEAR:-}" ] && [ -n "${DB_HOST:-}" ]; then
  command -v psql >/dev/null 2>&1 || { echo "ERROR: psql is required for SQL clearing (set SKIP_SQL_CLEAR=1 to skip)" >&2; exit 1; }
fi

# Database (optional — only needed for SQL clearing step)
DB_HOST="${DB_HOST:-}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-}"
DB_USER="${DB_USER:-}"
DB_PASSWORD="${DB_PASSWORD:-}"
SKIP_SQL_CLEAR="${SKIP_SQL_CLEAR:-}"

# Cognito
COGNITO_USER_POOL_ID="${COGNITO_USER_POOL_ID:?Set COGNITO_USER_POOL_ID}"
COGNITO_CLIENT_ID="${COGNITO_CLIENT_ID:?Set COGNITO_CLIENT_ID}"
AWS_REGION="${AWS_REGION:-af-south-1}"

# API
API_BASE_URL="${API_BASE_URL:-https://api.shumelahire.co.za}"

# Tenant & Auth
ADMIN_EMAIL="${ADMIN_EMAIL:-admin@idc-demo.co.za}"
ADMIN_PASSWORD="${ADMIN_PASSWORD:?Set ADMIN_PASSWORD}"
DEMO_PASSWORD="${DEMO_PASSWORD:-Demo@2026}"
TENANT_ID="${TENANT_ID:-idc-demo}"

# Colours
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log()  { echo -e "${CYAN}[$(date +%H:%M:%S)]${NC} $*" >&2; }
ok()   { echo -e "${GREEN}  OK${NC} $*" >&2; }
warn() { echo -e "${YELLOW}  WARN${NC} $*" >&2; }
fail() { echo -e "${RED}  FAIL${NC} $*" >&2; exit 1; }

urlencode() {
  python3 -c "import urllib.parse, sys; print(urllib.parse.quote(sys.argv[1]))" "$1"
}

echo ""
echo -e "${CYAN}============================================================${NC}"
echo -e "${CYAN}  ShumelaHire — IDC Demo Seed Script${NC}"
echo -e "${CYAN}============================================================${NC}"
echo ""

# ============================================================
# Section 1: Clear Data via SQL
# ============================================================
if [ -n "$SKIP_SQL_CLEAR" ] || [ -z "$DB_HOST" ]; then
  warn "Skipping SQL data clearing (SKIP_SQL_CLEAR set or DB_HOST not provided)"
else
log "Clearing existing tenant data via SQL..."

PGPASSWORD="$DB_PASSWORD" psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -q <<SQL
-- Helper function: delete from table if it exists, scoped to tenant
CREATE OR REPLACE FUNCTION _safe_tenant_delete(tbl text, tid text) RETURNS void AS \$\$
BEGIN
  EXECUTE format('DELETE FROM %I WHERE tenant_id = %L', tbl, tid);
EXCEPTION WHEN OTHERS THEN
  RAISE NOTICE 'Skipped %: %', tbl, SQLERRM;
END;
\$\$ LANGUAGE plpgsql;

-- Delete in FK-safe order (leaves first, roots last)
SELECT _safe_tenant_delete(t, '$TENANT_ID') FROM unnest(ARRAY[
  -- Performance
  'sap_payroll_transmissions', 'review_evidence', 'review_goal_scores', 'goal_kpis',
  'performance_reviews', 'performance_goals', 'performance_contracts',
  'performance_cycles', 'performance_templates',
  -- Recruitment leaves
  'background_checks', 'shortlist_scores', 'screening_answers', 'screening_questions',
  'pipeline_transitions', 'tg_salary_recommendations', 'recruitment_metrics',
  -- Offers
  'offers',
  -- Agency submissions
  'agency_submissions',
  -- Talent pool entries
  'talent_pool_entries',
  -- Interviews
  'interviews',
  -- Documents
  'documents',
  -- Employees
  'custom_field_values', 'custom_fields', 'employment_events',
  'employee_documents', 'employees',
  -- Applications & Applicants
  'applications', 'applicants',
  -- Jobs
  'tg_job_board_postings', 'job_ad_history', 'job_ads', 'job_postings',
  -- Talent pools, agencies, requisitions, departments
  'talent_pools', 'agency_profiles', 'requisitions', 'departments',
  -- System
  'messages', 'notifications', 'audit_logs',
  'workflow_actions', 'workflow_steps', 'workflow_instances',
  'user_preferences', 'linkedin_org_connections'
]) AS t;

-- Users: preserve the admin row
DELETE FROM users WHERE tenant_id = '$TENANT_ID' AND email != '$ADMIN_EMAIL';

-- Cleanup
DROP FUNCTION _safe_tenant_delete(text, text);
SQL

ok "Tenant data cleared (admin user preserved)"
fi

# ============================================================
# Section 2: Cognito User Cleanup
# ============================================================
log "Cleaning up Cognito users with @idc-demo.co.za domain..."

COGNITO_USERS=$(aws cognito-idp list-users \
  --user-pool-id "$COGNITO_USER_POOL_ID" \
  --region "$AWS_REGION" \
  --output json 2>/dev/null) || { warn "Could not list Cognito users"; COGNITO_USERS='{"Users":[]}'; }

# Extract usernames of @idc-demo.co.za users (excluding admin)
USERS_TO_DELETE=$(echo "$COGNITO_USERS" | jq -r '
  .Users[] |
  (.Attributes // []) as $attrs |
  ($attrs | map(select(.Name == "email")) | .[0].Value // .Username) as $email |
  select($email | test("@idc-demo\\.co\\.za$")) |
  select($email != "'"$ADMIN_EMAIL"'") |
  .Username
' 2>/dev/null || echo "")

DELETED_COUNT=0
if [ -n "$USERS_TO_DELETE" ]; then
  while IFS= read -r username; do
    aws cognito-idp admin-delete-user \
      --user-pool-id "$COGNITO_USER_POOL_ID" \
      --username "$username" \
      --region "$AWS_REGION" 2>/dev/null && {
        DELETED_COUNT=$((DELETED_COUNT + 1))
        ok "Deleted Cognito user: $username"
      } || warn "Could not delete Cognito user: $username"
  done <<< "$USERS_TO_DELETE"
fi
ok "Cognito cleanup complete ($DELETED_COUNT users removed)"

# ============================================================
# Section 3: Create 5 Cognito Users
# ============================================================
log "Creating 5 demo Cognito users..."

create_cognito_user() {
  local email="$1" group="$2" first="$3" last="$4"

  # Create user (suppress welcome email)
  aws cognito-idp admin-create-user \
    --user-pool-id "$COGNITO_USER_POOL_ID" \
    --username "$email" \
    --user-attributes \
      Name=email,Value="$email" \
      Name=email_verified,Value=true \
      Name=given_name,Value="$first" \
      Name=family_name,Value="$last" \
      Name=custom:tenant_id,Value="$TENANT_ID" \
    --temporary-password "$DEMO_PASSWORD" \
    --message-action SUPPRESS \
    --region "$AWS_REGION" >/dev/null 2>&1 || { warn "User may already exist: $email"; }

  # Set permanent password
  aws cognito-idp admin-set-user-password \
    --user-pool-id "$COGNITO_USER_POOL_ID" \
    --username "$email" \
    --password "$DEMO_PASSWORD" \
    --permanent \
    --region "$AWS_REGION" >/dev/null 2>&1 || warn "Could not set password for $email"

  # Add to group
  aws cognito-idp admin-add-user-to-group \
    --user-pool-id "$COGNITO_USER_POOL_ID" \
    --username "$email" \
    --group-name "$group" \
    --region "$AWS_REGION" >/dev/null 2>&1 || warn "Could not add $email to group $group"

  ok "Cognito user: $email ($group)"
}

create_cognito_user "hr.manager@idc-demo.co.za"     "HR_MANAGER"     "Nomfundo" "Mashaba"
create_cognito_user "hiring.manager@idc-demo.co.za"  "HIRING_MANAGER" "James"    "Petersen"
create_cognito_user "recruiter@idc-demo.co.za"       "RECRUITER"      "Zanele"   "Khumalo"
create_cognito_user "interviewer@idc-demo.co.za"     "INTERVIEWER"    "David"    "Mokoena"
create_cognito_user "employee@idc-demo.co.za"        "EMPLOYEE"       "Priya"    "Govender"

# ============================================================
# Section 4: Authenticate with Cognito
# ============================================================
log "Authenticating as ${ADMIN_EMAIL}..."

# Use --cli-input-json to avoid shell escaping issues with special chars in passwords
AUTH_INPUT=$(jq -n \
  --arg clientId "$COGNITO_CLIENT_ID" \
  --arg username "$ADMIN_EMAIL" \
  --arg password "$ADMIN_PASSWORD" \
  '{ClientId:$clientId, AuthFlow:"USER_PASSWORD_AUTH", AuthParameters:{USERNAME:$username, PASSWORD:$password}}')

AUTH_RESULT=$(aws cognito-idp initiate-auth \
  --cli-input-json "$AUTH_INPUT" \
  --region "$AWS_REGION" \
  --output json 2>&1) || fail "Cognito auth failed: $AUTH_RESULT"

ID_TOKEN=$(echo "$AUTH_RESULT" | jq -r '.AuthenticationResult.IdToken')
[ "$ID_TOKEN" = "null" ] && fail "No ID token returned"
ok "Authenticated (token expires in $(echo "$AUTH_RESULT" | jq -r '.AuthenticationResult.ExpiresIn')s)"

# API helpers
api() {
  local method="$1"
  local path="$2"
  shift 2
  local url="${API_BASE_URL}${path}"
  local headers=(-H "Authorization: Bearer $ID_TOKEN" -H "Content-Type: application/json")
  [ -n "$TENANT_ID" ] && headers+=(-H "X-Tenant-Id: $TENANT_ID")

  local response
  response=$(curl -s -w "\n%{http_code}" "${headers[@]}" -X "$method" "$url" "$@")
  local http_code
  http_code=$(echo "$response" | tail -1)
  local body
  body=$(echo "$response" | sed '$d')

  if [[ "$http_code" -ge 200 && "$http_code" -lt 300 ]]; then
    echo "$body"
  else
    echo "HTTP $http_code: $body" >&2
    return 1
  fi
}

api_post() { api POST "$@"; }
api_get()  { api GET "$@"; }
api_put()  { api PUT "$@"; }

# Verify connectivity
log "Verifying API connectivity..."
ME=$(api_get "/api/auth/me") || fail "Cannot reach API at $API_BASE_URL"
ok "Connected as $(echo "$ME" | jq -r '.email')"

# ============================================================
# Section 5: Provision Users in DB
# ============================================================
log "Provisioning demo users in database (JIT via /api/auth/me)..."

DEMO_EMAILS=(
  "hr.manager@idc-demo.co.za"
  "hiring.manager@idc-demo.co.za"
  "recruiter@idc-demo.co.za"
  "interviewer@idc-demo.co.za"
  "employee@idc-demo.co.za"
)

for demo_email in "${DEMO_EMAILS[@]}"; do
  # Authenticate as demo user (use --cli-input-json for safe password handling)
  DEMO_AUTH_INPUT=$(jq -n \
    --arg clientId "$COGNITO_CLIENT_ID" \
    --arg username "$demo_email" \
    --arg password "$DEMO_PASSWORD" \
    '{ClientId:$clientId, AuthFlow:"USER_PASSWORD_AUTH", AuthParameters:{USERNAME:$username, PASSWORD:$password}}')

  DEMO_AUTH=$(aws cognito-idp initiate-auth \
    --cli-input-json "$DEMO_AUTH_INPUT" \
    --region "$AWS_REGION" \
    --output json 2>&1) || { warn "Auth failed for $demo_email"; continue; }

  DEMO_ID_TOKEN=$(echo "$DEMO_AUTH" | jq -r '.AuthenticationResult.IdToken')

  # Call /api/auth/me to trigger CognitoUserProvisioningFilter
  DEMO_ME=$(curl -s \
    -H "Authorization: Bearer $DEMO_ID_TOKEN" \
    -H "Content-Type: application/json" \
    -H "X-Tenant-Id: $TENANT_ID" \
    "${API_BASE_URL}/api/auth/me" 2>/dev/null) || { warn "Failed to provision $demo_email"; continue; }

  ok "Provisioned $demo_email"
done

# Fetch all user IDs for later use (paginated response: {content:[...], totalElements:N})
USERS_RAW=$(api_get "/api/admin/users?page=0&size=100") || warn "Could not fetch users list"
# Handle both paginated {content:[...]} and plain array [...] responses
USERS=$(echo "$USERS_RAW" | jq 'if type == "object" then .content else . end' 2>/dev/null)
USER_COUNT=$(echo "$USERS" | jq 'length')
ok "Found $USER_COUNT users in tenant"

get_user_id_by_email() {
  echo "$USERS" | jq -r --arg email "$1" '.[] | select(.email == $email) | .id' | head -1
}

get_user_id_by_role() {
  echo "$USERS" | jq -r --arg role "$1" '.[] | select(.roleName == $role) | .id' | head -1
}

# Get admin user's DB ID (from users list, not JWT)
ADMIN_USER_ID=$(get_user_id_by_role "Administrator")
[ -z "$ADMIN_USER_ID" ] && ADMIN_USER_ID="1"
ok "Admin DB user ID=$ADMIN_USER_ID"

INTERVIEWER_USER_ID=$(get_user_id_by_role "Interviewer")
[ -z "$INTERVIEWER_USER_ID" ] && INTERVIEWER_USER_ID="$ADMIN_USER_ID"

# ============================================================
# Section 6: Seed 12 IDC Departments
# ============================================================
log "Creating 12 IDC departments..."

create_department() {
  local name="$1" desc="$2"
  local body
  body=$(jq -n --arg name "$name" --arg description "$desc" '{name:$name, description:$description}')
  local result
  result=$(api_post "/api/departments" -d "$body") || { warn "Failed to create department: $name"; return 0; }
  ok "Department: $name"
}

create_department "Agro-Processing & Agriculture" \
  "Food, beverage, forestry, and aquaculture"
create_department "Automotive & Transport Equipment" \
  "Automotive, rail, and aerospace manufacturing"
create_department "Chemicals, Medical & Industrial Mineral Products" \
  "Chemicals, pharmaceuticals, and mineral beneficiation"
create_department "Infrastructure" \
  "Water, sanitation, telecommunications, and logistics"
create_department "Machinery, Equipment & Electronics" \
  "Capital equipment and electronics manufacturing"
create_department "Media & Audio-Visual" \
  "Film and media value chain"
create_department "Mining & Metals" \
  "Mining operations and metals processing"
create_department "Textiles & Wood Products" \
  "Clothing, leather, and home decor manufacturing"
create_department "Tourism & Services" \
  "Accommodation, business hotels, and healthcare"
create_department "Small Business Finance & Regions (SBF)" \
  "Regional offices servicing smaller businesses (up to R15m-R20m)"
create_department "Partnership Programmes Department" \
  "Tailored funding products"
create_department "Human Capital Division" \
  "Talent and staff development"

# ============================================================
# Section 7: Create 1 Published Job — Senior Investment Analyst
# ============================================================
log "Creating job posting: Senior Investment Analyst..."

JOB_BODY=$(jq -n '{
  title: "Senior Investment Analyst",
  department: "Mining & Metals",
  location: "Sandton, Gauteng",
  employmentType: "FULL_TIME",
  experienceLevel: "SENIOR",
  description: "The IDC is seeking a Senior Investment Analyst to evaluate and recommend investment opportunities in the Mining & Metals sector. The analyst will conduct rigorous financial and economic analysis of proposed projects, assess risk profiles, and prepare investment memoranda for the Board Investment Committee. This role requires a deep understanding of the mining value chain and South African industrial policy.",
  requirements: "Minimum 6 years experience in investment analysis, corporate finance, or development finance. Strong financial modelling skills. Knowledge of South African mining sector and industrial policy frameworks. CFA designation preferred.",
  responsibilities: "Evaluate investment proposals and conduct detailed financial due diligence for mining and metals projects. Build and maintain complex financial models for project appraisal. Prepare investment memoranda and present to the Investment Committee. Monitor portfolio performance and provide post-investment support. Assess socio-economic impact of proposed investments including job creation and transformation targets.",
  qualifications: "BCom Honours in Finance, Economics, or Accounting. CFA Level II or above preferred. CA(SA) considered.",
  benefits: "Medical aid contribution. Retirement fund (15% employer contribution). Performance bonus. Study assistance programme. Parking.",
  salaryMin: 650000,
  salaryMax: 850000,
  salaryCurrency: "ZAR",
  positionsAvailable: 1,
  remoteWorkAllowed: false,
  applicationDeadline: "2026-04-30T23:59:59",
  featured: true
}')

JOB_RESULT=$(api_post "/api/job-postings?createdBy=$ADMIN_USER_ID" -d "$JOB_BODY") || fail "Failed to create job posting"
JOB_ID=$(echo "$JOB_RESULT" | jq -r '.id')
ok "Job posting #$JOB_ID: Senior Investment Analyst"

# Publish workflow: submit → approve → publish
api_post "/api/job-postings/$JOB_ID/submit-for-approval?submittedBy=$ADMIN_USER_ID" >/dev/null 2>&1 || true
api_post "/api/job-postings/$JOB_ID/approve?approvedBy=$ADMIN_USER_ID" >/dev/null 2>&1 || true
api_post "/api/job-postings/$JOB_ID/publish?publishedBy=$ADMIN_USER_ID" >/dev/null 2>&1 || true
ok "Job #$JOB_ID published"

# ============================================================
# Section 8: Create 10 Applicants & Applications
# ============================================================
log "Creating 10 applicants..."

create_applicant() {
  local name="$1" surname="$2" email="$3" phone="$4"
  local education="$5" experience="$6" skills="$7"

  local body
  body=$(jq -n \
    --arg name "$name" \
    --arg surname "$surname" \
    --arg email "$email" \
    --arg phone "$phone" \
    --arg education "$education" \
    --arg experience "$experience" \
    --arg skills "$skills" \
    '{name:$name, surname:$surname, email:$email, phone:$phone, education:$education, experience:$experience, skills:$skills}')

  local result
  result=$(api_post "/api/applicants" -d "$body" 2>/dev/null)
  if [ $? -eq 0 ]; then
    local id
    id=$(echo "$result" | jq -r '.id')
    ok "Applicant #$id: $name $surname (created)"
    echo "$id"
  else
    # Duplicate email — fetch existing applicant ID
    local enc_email
    enc_email=$(urlencode "$email")
    local search_result
    search_result=$(api_get "/api/applicants?search=$enc_email&page=0&size=1" 2>/dev/null) || { warn "Failed to find applicant: $email"; return 1; }
    local id
    id=$(echo "$search_result" | jq -r '.content[0].id // empty' 2>/dev/null)
    if [ -n "$id" ]; then
      ok "Applicant #$id: $name $surname (existing)"
      echo "$id"
    else
      warn "Could not find or create applicant: $name $surname ($email)"
      return 1
    fi
  fi
}

# Applicant 1: Thabo Mokoena
APPL_1=$(create_applicant "Thabo" "Mokoena" "thabo.mokoena@gmail.com" "+27 82 345 6789" \
  '[{"institution":"University of the Witwatersrand","degree":"BCom Honours Finance","year":"2018"},{"institution":"CFA Institute","degree":"CFA Level III","year":"2021"}]' \
  '[{"company":"Standard Bank","role":"Investment Analyst","years":"2018-2022"},{"company":"Nedbank Capital","role":"Senior Analyst","years":"2022-present"}]' \
  '["Financial modelling","Valuation","Project finance","Excel","Bloomberg Terminal","Due diligence"]')

# Applicant 2: Naledi Dlamini
APPL_2=$(create_applicant "Naledi" "Dlamini" "naledi.dlamini@outlook.com" "+27 83 456 7890" \
  '[{"institution":"University of Cape Town","degree":"BCom Honours Economics","year":"2019"},{"institution":"CFA Institute","degree":"CFA Level II","year":"2022"}]' \
  '[{"company":"RMB","role":"Graduate Analyst","years":"2019-2021"},{"company":"Ashburton Investments","role":"Investment Analyst","years":"2021-present"}]' \
  '["Investment analysis","Equity research","Financial modelling","Sector analysis","Bloomberg"]')

# Applicant 3: Pieter van der Merwe
APPL_3=$(create_applicant "Pieter" "van der Merwe" "pieter.vdm@gmail.com" "+27 71 567 8901" \
  '[{"institution":"Stellenbosch University","degree":"MCom Finance","year":"2017"},{"institution":"CFA Institute","degree":"CFA Level III","year":"2020"}]' \
  '[{"company":"Absa Capital","role":"Analyst","years":"2017-2020"},{"company":"DBSA","role":"Senior Investment Analyst","years":"2020-present"}]' \
  '["Development finance","Credit analysis","Financial modelling","Mining sector","Stress testing"]')

# Applicant 4: Ayanda Nkosi
APPL_4=$(create_applicant "Ayanda" "Nkosi" "ayanda.nkosi@yahoo.com" "+27 84 678 9012" \
  '[{"institution":"University of Pretoria","degree":"BCom Finance","year":"2020"},{"institution":"CFA Institute","degree":"CFA Level I","year":"2023"}]' \
  '[{"company":"Investec","role":"Graduate Analyst","years":"2020-2022"},{"company":"Old Mutual","role":"Investment Analyst","years":"2022-present"}]' \
  '["Equity valuation","Financial analysis","Excel","Python","Portfolio management"]')

# Applicant 5: Fatima Patel
APPL_5=$(create_applicant "Fatima" "Patel" "fatima.patel@gmail.com" "+27 72 789 0123" \
  '[{"institution":"University of Johannesburg","degree":"BCom Accounting","year":"2018"},{"institution":"SAICA","degree":"CA(SA)","year":"2021"}]' \
  '[{"company":"Deloitte","role":"Audit Trainee","years":"2018-2021"},{"company":"Transnet","role":"Financial Analyst","years":"2021-present"}]' \
  '["IFRS","Financial modelling","Due diligence","SAP","Mining sector analysis","PFMA"]')

# Applicant 6: Sipho Mthembu
APPL_6=$(create_applicant "Sipho" "Mthembu" "sipho.mthembu@hotmail.com" "+27 83 890 1234" \
  '[{"institution":"University of KwaZulu-Natal","degree":"BCom Honours Economics","year":"2017"},{"institution":"CFA Institute","degree":"CFA Level II","year":"2020"}]' \
  '[{"company":"RMB","role":"Graduate Analyst","years":"2017-2019"},{"company":"Ashburton Investments","role":"Investment Analyst","years":"2019-present"}]' \
  '["Investment analysis","Equity research","Financial modelling","Sector analysis","Presentations","Bloomberg"]')

# Applicant 7: Lauren Williams
APPL_7=$(create_applicant "Lauren" "Williams" "lauren.williams@gmail.com" "+27 82 901 2345" \
  '[{"institution":"Stellenbosch University","degree":"BCom Honours Investment Management","year":"2019"}]' \
  '[{"company":"Allan Gray","role":"Analyst","years":"2019-2022"},{"company":"Coronation Fund Managers","role":"Senior Analyst","years":"2022-present"}]' \
  '["Portfolio management","Equity valuation","Financial modelling","Research","Presentations"]')

# Applicant 8: Bongani Zwane
APPL_8=$(create_applicant "Bongani" "Zwane" "bongani.zwane@gmail.com" "+27 71 012 3456" \
  '[{"institution":"University of the Witwatersrand","degree":"MCom Finance","year":"2015"},{"institution":"CFA Institute","degree":"CFA Charter","year":"2019"}]' \
  '[{"company":"Standard Bank","role":"Analyst","years":"2015-2018"},{"company":"DBSA","role":"Senior Investment Analyst","years":"2018-present"}]' \
  '["Development finance","Project finance","Financial modelling","Mining sector","Due diligence","Stakeholder management"]')

# Applicant 9: Nomsa Mahlangu
APPL_9=$(create_applicant "Nomsa" "Mahlangu" "nomsa.mahlangu@gmail.com" "+27 71 567 8901" \
  '[{"institution":"University of Johannesburg","degree":"BCom Economics","year":"2020"},{"institution":"University of Johannesburg","degree":"BCom Honours Investment Management","year":"2021"}]' \
  '[{"company":"Allan Gray","role":"Graduate Analyst","years":"2021-2023"},{"company":"Coronation Fund Managers","role":"Analyst","years":"2023-present"}]' \
  '["Financial analysis","Portfolio management","Excel","Bloomberg","Research","Equity valuation"]')

# Applicant 10: Michael Botha
APPL_10=$(create_applicant "Michael" "Botha" "michael.botha@gmail.com" "+27 72 234 5678" \
  '[{"institution":"Stellenbosch University","degree":"BCom Actuarial Science","year":"2019"}]' \
  '[{"company":"Old Mutual","role":"Actuarial Analyst","years":"2019-2022"},{"company":"Sanlam","role":"Risk Analyst","years":"2022-present"}]' \
  '["Risk modelling","Statistical analysis","Python","R","SQL","Actuarial valuations"]')

# --- Create 10 applications ---
log "Creating 10 applications for Senior Investment Analyst..."

create_application() {
  local applicant_id="$1" cover_letter="$2"

  local body
  body=$(jq -n \
    --argjson applicantId "$applicant_id" \
    --argjson jobAdId "$JOB_ID" \
    --arg coverLetter "$cover_letter" \
    '{applicantId:$applicantId, jobAdId:$jobAdId, jobTitle:"Senior Investment Analyst", department:"Mining & Metals", coverLetter:$coverLetter, applicationSource:"EXTERNAL"}')

  local result
  result=$(api_post "/api/applications" -d "$body" 2>/dev/null)
  if [ $? -eq 0 ]; then
    local id
    id=$(echo "$result" | jq -r '.id')
    ok "Application #$id for applicant #$applicant_id (created)"
    echo "$id"
  else
    # Application may already exist — search for existing
    local search_result
    search_result=$(api_get "/api/applications?applicantId=$applicant_id&page=0&size=10" 2>/dev/null) || true
    local id
    id=$(echo "$search_result" | jq -r ".content[] | select(.jobAdId == $JOB_ID or .jobTitle == \"Senior Investment Analyst\") | .id" 2>/dev/null | head -1)
    if [ -z "$id" ]; then
      # Broader search — any application for this applicant
      id=$(echo "$search_result" | jq -r '.content[0].id // empty' 2>/dev/null)
    fi
    if [ -n "$id" ]; then
      ok "Application #$id for applicant #$applicant_id (existing)"
      echo "$id"
    else
      warn "Could not create or find application for applicant #$applicant_id"
      echo ""
      return 0
    fi
  fi
}

APP_1=$(create_application "$APPL_1" \
  "I am an experienced investment analyst at Nedbank Capital with a CFA charter and strong background in project finance. The IDC's developmental mandate in the mining sector aligns with my career aspirations in development finance.")

APP_2=$(create_application "$APPL_2" \
  "With my investment analysis experience at Ashburton Investments and CFA Level II progress, I am eager to apply my analytical skills to the IDC's mining and metals portfolio.")

APP_3=$(create_application "$APPL_3" \
  "As a Senior Investment Analyst at DBSA with deep development finance experience and a focus on mining sector investments, I bring directly relevant expertise to the IDC's Mining & Metals division.")

APP_4=$(create_application "$APPL_4" \
  "My investment analysis experience at Old Mutual and strong quantitative foundation equip me to contribute meaningfully to the IDC's mining sector investment appraisals.")

APP_5=$(create_application "$APPL_5" \
  "As a CA(SA) with financial analysis experience at Transnet and exposure to PFMA compliance, I would bring a rigorous analytical perspective to the IDC's mining investment team.")

APP_6=$(create_application "$APPL_6" \
  "With investment analysis experience at Ashburton and a strong economics background, I am well-positioned to evaluate mining and metals investment opportunities for the IDC.")

APP_7=$(create_application "$APPL_7" \
  "My portfolio management experience at Coronation Fund Managers and deep understanding of South African equities, particularly mining counters, make me a strong candidate for this role.")

APP_8=$(create_application "$APPL_8" \
  "Having worked as a Senior Investment Analyst at DBSA, I bring development finance experience and a strong understanding of the mining sector that directly aligns with this role.")

APP_9=$(create_application "$APPL_9" \
  "As an analyst at Coronation with a strong academic grounding in investment management, I would bring fresh analytical perspectives to the IDC's mining and metals investment team.")

APP_10=$(create_application "$APPL_10" \
  "My actuarial background and risk analysis experience at Sanlam have equipped me with a quantitative lens that would add value to the IDC's mining investment appraisal process.")

# --- Transition application statuses ---
log "Updating application statuses through valid transitions..."

update_status() {
  local app_id="$1" status="$2" notes="${3:-}"
  [ -z "$app_id" ] && return 0
  local url="/api/applications/$app_id/status?status=$status"
  [ -n "$notes" ] && url+="&notes=$(urlencode "$notes")"
  api_put "$url" >/dev/null 2>&1 || true
}

# 1. Thabo Mokoena — stays SUBMITTED (default)
[ -n "$APP_1" ] && ok "Application #$APP_1 (Thabo Mokoena) -> SUBMITTED"

# 2. Naledi Dlamini — stays SUBMITTED (default)
[ -n "$APP_2" ] && ok "Application #$APP_2 (Naledi Dlamini) -> SUBMITTED"

# 3. Pieter van der Merwe — SCREENING
[ -n "$APP_3" ] && {
  update_status "$APP_3" "SCREENING" "Strong DFI experience. Under detailed review."
  ok "Application #$APP_3 (Pieter van der Merwe) -> SCREENING"
}

# 4. Ayanda Nkosi — SCREENING
[ -n "$APP_4" ] && {
  update_status "$APP_4" "SCREENING" "Reviewing qualifications and CFA progress."
  ok "Application #$APP_4 (Ayanda Nkosi) -> SCREENING"
}

# 5. Fatima Patel — INTERVIEW_SCHEDULED
[ -n "$APP_5" ] && {
  update_status "$APP_5" "SCREENING" "CA(SA) with relevant experience."
  update_status "$APP_5" "INTERVIEW_SCHEDULED" "Panel interview scheduled."
  ok "Application #$APP_5 (Fatima Patel) -> INTERVIEW_SCHEDULED"
}

# 6. Sipho Mthembu — INTERVIEW_SCHEDULED
[ -n "$APP_6" ] && {
  update_status "$APP_6" "SCREENING" "Solid investment background."
  update_status "$APP_6" "INTERVIEW_SCHEDULED" "Technical interview scheduled."
  ok "Application #$APP_6 (Sipho Mthembu) -> INTERVIEW_SCHEDULED"
}

# 7. Lauren Williams — INTERVIEW_COMPLETED
[ -n "$APP_7" ] && {
  update_status "$APP_7" "SCREENING" "Strong portfolio management background."
  update_status "$APP_7" "INTERVIEW_SCHEDULED" "First round interview scheduled."
  update_status "$APP_7" "INTERVIEW_COMPLETED" "Interview completed. Strong candidate."
  ok "Application #$APP_7 (Lauren Williams) -> INTERVIEW_COMPLETED"
}

# 8. Bongani Zwane — OFFER_PENDING
[ -n "$APP_8" ] && {
  update_status "$APP_8" "SCREENING" "Excellent DFI and mining sector experience."
  update_status "$APP_8" "INTERVIEW_SCHEDULED" "Panel interview scheduled."
  update_status "$APP_8" "INTERVIEW_COMPLETED" "Outstanding interview performance."
  update_status "$APP_8" "OFFER_PENDING" "Preparing offer. Top candidate."
  ok "Application #$APP_8 (Bongani Zwane) -> OFFER_PENDING"
}

# 9. Nomsa Mahlangu — SCREENING (shortlisted)
[ -n "$APP_9" ] && {
  update_status "$APP_9" "SCREENING" "Shortlisted"
  ok "Application #$APP_9 (Nomsa Mahlangu) -> SCREENING (Shortlisted)"
}

# 10. Michael Botha — REJECTED
[ -n "$APP_10" ] && {
  update_status "$APP_10" "SCREENING" "Reviewing actuarial background for relevance."
  update_status "$APP_10" "REJECTED" "Does not meet minimum investment analysis experience requirements for senior role."
  ok "Application #$APP_10 (Michael Botha) -> REJECTED"
}

# ============================================================
# Section 9: Schedule Interviews & Create Offer
# ============================================================
log "Scheduling interviews..."

schedule_interview() {
  local app_id="$1" scheduled_at="$2" type="$3" round="$4" location="$5"
  local enc_location
  enc_location=$(urlencode "$location")

  local result
  result=$(api_post "/api/interviews/schedule?applicationId=$app_id&scheduledAt=$scheduled_at&interviewerId=$INTERVIEWER_USER_ID&type=$type&round=$round&scheduledBy=$ADMIN_USER_ID&durationMinutes=60&location=$enc_location" 2>&1) || { warn "Failed to schedule interview for application #$app_id"; echo ""; return 0; }

  local int_id
  int_id=$(echo "$result" | jq -r '.id' 2>/dev/null || echo "")
  echo "$int_id"
}

# Fatima Patel — future date, PANEL, FIRST_ROUND
INT_FATIMA=""
if [ -n "$APP_5" ]; then
  INT_FATIMA=$(schedule_interview "$APP_5" "2026-03-10T10:00:00" "PANEL" "FIRST_ROUND" "IDC Boardroom A, 19 Fredman Drive, Sandton")
  ok "Interview scheduled: Fatima Patel (PANEL, 2026-03-10)"
fi

# Sipho Mthembu — future date, TECHNICAL, TECHNICAL
INT_SIPHO=""
if [ -n "$APP_6" ]; then
  INT_SIPHO=$(schedule_interview "$APP_6" "2026-03-11T14:00:00" "TECHNICAL" "TECHNICAL" "IDC IT Lab, 19 Fredman Drive, Sandton")
  ok "Interview scheduled: Sipho Mthembu (TECHNICAL, 2026-03-11)"
fi

# Lauren Williams — IN_PERSON, FIRST_ROUND → start → complete → feedback
INT_LAUREN=""
if [ -n "$APP_7" ]; then
  INT_LAUREN=$(schedule_interview "$APP_7" "2026-03-05T10:00:00" "IN_PERSON" "FIRST_ROUND" "IDC Boardroom B, 19 Fredman Drive, Sandton")
  [ -n "$INT_LAUREN" ] && ok "Interview scheduled: Lauren Williams (IN_PERSON, 2026-03-05)"
fi

if [ -n "$INT_LAUREN" ] && [ "$INT_LAUREN" != "null" ]; then
  api_post "/api/interviews/$INT_LAUREN/start?startedBy=$ADMIN_USER_ID" >/dev/null 2>&1 || true
  api_post "/api/interviews/$INT_LAUREN/complete?completedBy=$ADMIN_USER_ID" >/dev/null 2>&1 || true

  FEEDBACK_PARAMS="feedback=$(urlencode "Strong candidate with excellent portfolio management experience. Demonstrated deep knowledge of mining sector equities and valuation methodologies. Articulate and well-prepared.")"
  FEEDBACK_PARAMS+="&rating=4&communicationSkills=4&technicalSkills=4&culturalFit=4"
  FEEDBACK_PARAMS+="&overallImpression=$(urlencode "Strong candidate with relevant experience")"
  FEEDBACK_PARAMS+="&recommendation=CONSIDER&submittedBy=$ADMIN_USER_ID"
  api_post "/api/interviews/$INT_LAUREN/feedback?$FEEDBACK_PARAMS" >/dev/null 2>&1 || true
  ok "Interview #$INT_LAUREN completed with feedback"
fi

# Bongani Zwane — PANEL, FIRST_ROUND → start → complete → feedback
INT_BONGANI=""
if [ -n "$APP_8" ]; then
  INT_BONGANI=$(schedule_interview "$APP_8" "2026-03-04T10:00:00" "PANEL" "FIRST_ROUND" "IDC Boardroom A, 19 Fredman Drive, Sandton")
  [ -n "$INT_BONGANI" ] && ok "Interview scheduled: Bongani Zwane (PANEL, 2026-03-04)"
fi

if [ -n "$INT_BONGANI" ] && [ "$INT_BONGANI" != "null" ]; then
  api_post "/api/interviews/$INT_BONGANI/start?startedBy=$ADMIN_USER_ID" >/dev/null 2>&1 || true
  api_post "/api/interviews/$INT_BONGANI/complete?completedBy=$ADMIN_USER_ID" >/dev/null 2>&1 || true

  FEEDBACK_PARAMS="feedback=$(urlencode "Outstanding candidate. Deep development finance and mining sector expertise from DBSA. Excellent analytical skills, clear communicator, strong cultural fit. Unanimous panel recommendation to extend offer.")"
  FEEDBACK_PARAMS+="&rating=5&communicationSkills=5&technicalSkills=5&culturalFit=5"
  FEEDBACK_PARAMS+="&overallImpression=$(urlencode "Exceptional candidate - top choice")"
  FEEDBACK_PARAMS+="&recommendation=HIRE&submittedBy=$ADMIN_USER_ID"
  api_post "/api/interviews/$INT_BONGANI/feedback?$FEEDBACK_PARAMS" >/dev/null 2>&1 || true
  ok "Interview #$INT_BONGANI completed with feedback"
fi

# --- Create offer for Bongani Zwane ---
log "Creating offer for Bongani Zwane..."

OFFER_BODY=$(jq -n --argjson appId "$APP_8" '{
  application: {id: $appId},
  jobTitle: "Senior Investment Analyst",
  department: "Mining & Metals",
  offerType: "FULL_TIME_PERMANENT",
  baseSalary: 720000,
  currency: "ZAR",
  salaryFrequency: "ANNUALLY",
  bonusEligible: true,
  bonusTargetPercentage: 15,
  healthInsurance: true,
  retirementPlan: true,
  retirementContributionPercentage: 15,
  vacationDaysAnnual: 20,
  sickDaysAnnual: 15,
  probationaryPeriodDays: 90,
  noticePeriodDays: 30,
  startDate: "2026-04-01",
  offerExpiryDate: "2026-03-31T23:59:59",
  workLocation: "IDC Head Office, 19 Fredman Drive, Sandton",
  benefitsPackage: "Medical aid (Discovery Health), Retirement fund (15% employer contribution), Annual performance bonus (15% target), Study assistance programme, Parking",
  reportingManager: "Head: Mining & Metals"
}')

if [ -n "$APP_8" ]; then
  OFFER_RESULT=$(api_post "/api/offers/applications/$APP_8" -d "$OFFER_BODY" 2>&1) || warn "Failed to create offer"
  if echo "$OFFER_RESULT" | jq -e '.id' >/dev/null 2>&1; then
    OFFER_ID=$(echo "$OFFER_RESULT" | jq -r '.id')
    ok "Offer #$OFFER_ID created as DRAFT for Bongani Zwane (R720k)"
  else
    warn "Offer creation response: $OFFER_RESULT"
  fi
else
  warn "Skipping offer creation (no application ID for Bongani Zwane)"
fi

# ============================================================
# Section 10: Create Talent Pool with 5 Entries
# ============================================================
log "Creating talent pool..."

POOL_BODY=$(jq -n '{
  poolName: "Critical: Senior Investment Analysts",
  description: "High-potential candidates for current and future Senior Investment Analyst roles in the Mining & Metals division.",
  department: "Mining & Metals",
  experienceLevel: "SENIOR",
  isActive: true,
  autoAddEnabled: false
}')

POOL_RESULT=$(api_post "/api/talent-pools" -d "$POOL_BODY" 2>/dev/null)
if [ $? -eq 0 ] && echo "$POOL_RESULT" | jq -e '.id' >/dev/null 2>&1; then
  POOL_ID=$(echo "$POOL_RESULT" | jq -r '.id')
  ok "Talent pool #$POOL_ID: Critical: Senior Investment Analysts (created)"
else
  # Search for existing talent pool
  POOLS_SEARCH=$(api_get "/api/talent-pools" 2>/dev/null) || true
  POOL_ID=$(echo "$POOLS_SEARCH" | jq -r '.[] | select(.poolName | test("Senior Investment Analysts")) | .id' 2>/dev/null | head -1)
  if [ -n "$POOL_ID" ]; then
    ok "Talent pool #$POOL_ID: Critical: Senior Investment Analysts (existing)"
  else
    warn "Could not create or find talent pool. Skipping pool entries."
    POOL_ID=""
  fi
fi

# Add 5 entries
add_pool_entry() {
  local applicant_id="$1" rating="$2" notes="$3"

  local body
  body=$(jq -n \
    --argjson applicantId "$applicant_id" \
    --argjson rating "$rating" \
    --arg notes "$notes" \
    '{applicant:{id:$applicantId}, sourceType:"MANUAL", rating:$rating, isAvailable:true, notes:$notes}')

  api_post "/api/talent-pools/$POOL_ID/entries" -d "$body" >/dev/null 2>&1 || { warn "Failed to add applicant #$applicant_id to talent pool"; return 1; }
  ok "Pool entry: Applicant #$applicant_id (rating $rating)"
}

if [ -n "$POOL_ID" ]; then
  add_pool_entry "$APPL_1" 5 "CFA charter holder with strong project finance background. Top candidate."
  add_pool_entry "$APPL_2" 4 "Solid investment analysis experience. Strong potential."
  add_pool_entry "$APPL_3" 4 "DFI experience at DBSA. Directly relevant background."
  add_pool_entry "$APPL_5" 4 "CA(SA) with analytical rigour. Versatile candidate."
  add_pool_entry "$APPL_9" 3 "Early career but strong academic foundation. Future potential."
else
  warn "Skipping talent pool entries (no pool ID)"
fi

# ============================================================
# Section 11: Create Agency with Submission
# ============================================================
log "Creating recruitment agency..."

AGENCY_BODY=$(jq -n '{
  agencyName: "Kgotla Executive Search",
  contactPerson: "Lerato Kgotla",
  contactEmail: "lerato@kgotla-exec.co.za",
  contactPhone: "+27 11 234 5678",
  specializations: "Executive search, Financial services, Mining & Resources, Development finance",
  beeLevel: 1,
  feePercentage: 15
}')

AGENCY_RESULT=$(api_post "/api/agencies/register" -d "$AGENCY_BODY" 2>/dev/null)
if [ $? -eq 0 ] && echo "$AGENCY_RESULT" | jq -e '.id' >/dev/null 2>&1; then
  AGENCY_ID=$(echo "$AGENCY_RESULT" | jq -r '.id')
  ok "Agency #$AGENCY_ID: Kgotla Executive Search (created)"

  # Approve agency
  api_post "/api/agencies/$AGENCY_ID/approve" >/dev/null 2>&1 || warn "Failed to approve agency"
  ok "Agency #$AGENCY_ID approved"
else
  # Search for existing agency
  AGENCIES_SEARCH=$(api_get "/api/agencies" 2>/dev/null) || true
  AGENCY_ID=$(echo "$AGENCIES_SEARCH" | jq -r '.[] | select(.agencyName | test("Kgotla")) | .id' 2>/dev/null | head -1)
  if [ -n "$AGENCY_ID" ]; then
    ok "Agency #$AGENCY_ID: Kgotla Executive Search (existing)"
  else
    warn "Could not create or find agency. Skipping agency submission."
    AGENCY_ID=""
  fi
fi

# Submit a candidate
SUBMISSION_BODY=$(jq -n --argjson jobPostingId "$JOB_ID" '{
  jobPosting: {id: $jobPostingId},
  candidateName: "Thandi Moloi",
  candidateEmail: "thandi.moloi@gmail.com",
  candidatePhone: "+27 84 567 8901",
  coverNote: "Thandi is a highly experienced investment professional with 8 years in development finance, including 4 years at the Land Bank focusing on agricultural and mining sector investments. She holds a CFA charter and MCom in Finance from Wits. Currently available and seeking a senior role in the DFI space."
}')

if [ -n "$AGENCY_ID" ]; then
  SUBMISSION_RESULT=$(api_post "/api/agencies/$AGENCY_ID/submissions" -d "$SUBMISSION_BODY" 2>&1) || warn "Failed to submit agency candidate"
  if echo "$SUBMISSION_RESULT" | jq -e '.id' >/dev/null 2>&1; then
    SUBMISSION_ID=$(echo "$SUBMISSION_RESULT" | jq -r '.id')
    ok "Agency submission #$SUBMISSION_ID: Thandi Moloi"
  else
    warn "Agency submission response: $SUBMISSION_RESULT"
  fi
else
  warn "Skipping agency submission (no agency ID)"
fi

# ============================================================
# Section 12: Summary & Verification
# ============================================================
log "Verifying seeded data..."

# Count departments
DEPT_COUNT=$(api_get "/api/departments" 2>/dev/null | jq 'length' 2>/dev/null || echo "?")

# Count applications
APPS_RESP=$(api_get "/api/applications?page=0&size=1" 2>/dev/null || echo "")
APP_COUNT=$(echo "$APPS_RESP" | jq '.totalElements // .content | length' 2>/dev/null || echo "?")

# Count talent pools
POOLS_COUNT=$(api_get "/api/talent-pools" 2>/dev/null | jq 'length' 2>/dev/null || echo "?")

# Count agencies
AGENCIES_COUNT=$(api_get "/api/agencies" 2>/dev/null | jq 'length' 2>/dev/null || echo "?")

echo ""
echo -e "${GREEN}============================================================${NC}"
echo -e "${GREEN}  IDC Demo Data Seeding Complete${NC}"
echo -e "${GREEN}============================================================${NC}"
echo ""
echo "  Departments:    $DEPT_COUNT (expected: 12)"
echo "  Users:          $USER_COUNT (admin + 5 demo)"
echo "  Job Postings:   1 published (Senior Investment Analyst)"
echo "  Applications:   $APP_COUNT (expected: 10)"
echo "  Talent Pools:   $POOLS_COUNT (expected: 1, with 5 entries)"
echo "  Agencies:       $AGENCIES_COUNT (expected: 1, with 1 submission)"
echo ""
echo "  Pipeline Distribution:"
echo "    SUBMITTED:           2  (Thabo Mokoena, Naledi Dlamini)"
echo "    SCREENING:           3  (Pieter van der Merwe, Ayanda Nkosi, Nomsa Mahlangu)"
echo "    INTERVIEW_SCHEDULED: 2  (Fatima Patel, Sipho Mthembu)"
echo "    INTERVIEW_COMPLETED: 1  (Lauren Williams)"
echo "    OFFER_PENDING:       1  (Bongani Zwane)"
echo "    REJECTED:            1  (Michael Botha)"
echo ""
echo -e "${CYAN}  Login Credentials${NC}"
echo "  ──────────────────────────────────────────────────────────"
echo "  URL:  https://idc-demo.shumelahire.co.za"
echo ""
echo "  admin@idc-demo.co.za            $ADMIN_PASSWORD    ADMIN"
echo "  hr.manager@idc-demo.co.za       $DEMO_PASSWORD    HR_MANAGER"
echo "  hiring.manager@idc-demo.co.za   $DEMO_PASSWORD    HIRING_MANAGER"
echo "  recruiter@idc-demo.co.za        $DEMO_PASSWORD    RECRUITER"
echo "  interviewer@idc-demo.co.za      $DEMO_PASSWORD    INTERVIEWER"
echo "  employee@idc-demo.co.za         $DEMO_PASSWORD    EMPLOYEE"
echo ""
