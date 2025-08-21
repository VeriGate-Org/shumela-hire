#!/bin/bash
# Script to convert DashboardShell to PageWrapper in React pages

PAGES=(
    "offers/page.tsx"
    "pipeline/page.tsx" 
    "job-postings/page.tsx"
    "applications/page.tsx"
    "interviews/page.tsx"
    "reports/page.tsx"
)

BASE_PATH="/Users/arthurmanena/Documents/source/e-recruitment-dashboard/src/app"

for page in "${PAGES[@]}"; do
    file="$BASE_PATH/$page"
    echo "Processing $file..."
    
    # Replace DashboardShell import with PageWrapper import
    sed -i '' 's|import DashboardShell from.*DashboardShell.*|import PageWrapper from '\''@/components/PageWrapper'\'';|g' "$file"
    
    # You'll still need to manually update the JSX usage patterns
    # because they vary too much between files
done

echo "Import replacements complete. JSX usage patterns need manual updates."
