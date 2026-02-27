#!/usr/bin/env bash
# init-sqlserver.sh — Initialize SQL Server database for ShumelaHire
# Creates the database if it doesn't exist and sets up the application login.
# Usage: ./scripts/init-sqlserver.sh [container_name]
#
# Environment variables:
#   MSSQL_SA_PASSWORD  — SA password (required)
#   MSSQL_DATABASE     — Database name (default: shumelahire)
#   APP_DB_USER        — Application DB user (default: shumelahire_app)
#   APP_DB_PASSWORD    — Application DB password (default: MSSQL_SA_PASSWORD)

set -euo pipefail

CONTAINER_NAME="${1:-shumelahire-sqlserver}"
DATABASE="${MSSQL_DATABASE:-shumelahire}"
APP_USER="${APP_DB_USER:-shumelahire_app}"
APP_PASSWORD="${APP_DB_PASSWORD:-${MSSQL_SA_PASSWORD}}"

# Validate SA password is set
if [ -z "${MSSQL_SA_PASSWORD:-}" ]; then
    echo "ERROR: MSSQL_SA_PASSWORD environment variable is required"
    exit 1
fi

echo "=== ShumelaHire SQL Server Initialization ==="
echo "Container: ${CONTAINER_NAME}"
echo "Database:  ${DATABASE}"
echo "App User:  ${APP_USER}"
echo "=============================================="

# Wait for SQL Server to be ready
echo "Waiting for SQL Server to be ready..."
for i in $(seq 1 30); do
    if docker exec "${CONTAINER_NAME}" /opt/mssql-tools18/bin/sqlcmd \
        -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -C \
        -Q "SELECT 1" -b > /dev/null 2>&1; then
        echo "SQL Server is ready."
        break
    fi
    if [ "$i" -eq 30 ]; then
        echo "ERROR: SQL Server did not become ready within 30 attempts"
        exit 1
    fi
    echo "  Attempt $i/30 — waiting..."
    sleep 2
done

# Create database and application user
echo "Creating database and application user..."
docker exec "${CONTAINER_NAME}" /opt/mssql-tools18/bin/sqlcmd \
    -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -C \
    -Q "
    -- Create database if not exists
    IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = '${DATABASE}')
    BEGIN
        CREATE DATABASE [${DATABASE}];
        PRINT 'Database ${DATABASE} created.';
    END
    ELSE
        PRINT 'Database ${DATABASE} already exists.';

    -- Create login if not exists
    IF NOT EXISTS (SELECT name FROM sys.server_principals WHERE name = '${APP_USER}')
    BEGIN
        CREATE LOGIN [${APP_USER}] WITH PASSWORD = '${APP_PASSWORD}';
        PRINT 'Login ${APP_USER} created.';
    END
    ELSE
        PRINT 'Login ${APP_USER} already exists.';
    "

# Create user in database and grant permissions
docker exec "${CONTAINER_NAME}" /opt/mssql-tools18/bin/sqlcmd \
    -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -C \
    -d "${DATABASE}" \
    -Q "
    -- Create user if not exists
    IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = '${APP_USER}')
    BEGIN
        CREATE USER [${APP_USER}] FOR LOGIN [${APP_USER}];
        PRINT 'User ${APP_USER} created in ${DATABASE}.';
    END
    ELSE
        PRINT 'User ${APP_USER} already exists in ${DATABASE}.';

    -- Grant permissions
    ALTER ROLE db_datareader ADD MEMBER [${APP_USER}];
    ALTER ROLE db_datawriter ADD MEMBER [${APP_USER}];
    GRANT CREATE TABLE TO [${APP_USER}];
    GRANT ALTER ON SCHEMA::dbo TO [${APP_USER}];
    GRANT REFERENCES ON SCHEMA::dbo TO [${APP_USER}];
    PRINT 'Permissions granted to ${APP_USER}.';
    "

echo "=== Initialization Complete ==="
