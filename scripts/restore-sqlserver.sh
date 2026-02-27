#!/usr/bin/env bash
# restore-sqlserver.sh — Restore ShumelaHire SQL Server database from backup
# Usage: ./scripts/restore-sqlserver.sh <backup_file> [container_name]
#
# Environment variables:
#   MSSQL_SA_PASSWORD  — SA password (required)
#   MSSQL_HOST         — SQL Server host (default: localhost)
#   MSSQL_PORT         — SQL Server port (default: 1433)
#   MSSQL_DATABASE     — Database name (default: shumelahire)

set -euo pipefail

BACKUP_FILE="${1:?Usage: $0 <backup_file> [container_name]}"
CONTAINER_NAME="${2:-shumelahire-sqlserver}"
DATABASE="${MSSQL_DATABASE:-shumelahire}"

# Validate SA password is set
if [ -z "${MSSQL_SA_PASSWORD:-}" ]; then
    echo "ERROR: MSSQL_SA_PASSWORD environment variable is required"
    exit 1
fi

# Validate backup file exists
if [ ! -f "${BACKUP_FILE}" ]; then
    echo "ERROR: Backup file not found: ${BACKUP_FILE}"
    exit 1
fi

echo "=== ShumelaHire SQL Server Restore ==="
echo "Database: ${DATABASE}"
echo "Backup:   ${BACKUP_FILE}"
echo "Time:     $(date -u +%Y-%m-%dT%H:%M:%SZ)"
echo ""
echo "WARNING: This will overwrite the existing '${DATABASE}' database!"
read -p "Continue? (y/N): " CONFIRM
if [ "${CONFIRM}" != "y" ] && [ "${CONFIRM}" != "Y" ]; then
    echo "Restore cancelled."
    exit 0
fi
echo "======================================="

# Check if running in Docker
if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "Running restore via Docker container..."

    # Copy backup file to container
    docker cp "${BACKUP_FILE}" "${CONTAINER_NAME}:/backup/restore.bak"

    # Set database to single-user mode, restore, then back to multi-user
    docker exec "${CONTAINER_NAME}" /opt/mssql-tools18/bin/sqlcmd \
        -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -C \
        -Q "
        IF EXISTS (SELECT name FROM sys.databases WHERE name = '${DATABASE}')
        BEGIN
            ALTER DATABASE [${DATABASE}] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
        END;
        RESTORE DATABASE [${DATABASE}] FROM DISK = N'/backup/restore.bak' WITH REPLACE, STATS = 10;
        ALTER DATABASE [${DATABASE}] SET MULTI_USER;
        "

    # Cleanup
    docker exec "${CONTAINER_NAME}" rm -f /backup/restore.bak
else
    echo "Running restore via direct SQL Server connection..."

    MSSQL_HOST="${MSSQL_HOST:-localhost}"
    MSSQL_PORT="${MSSQL_PORT:-1433}"

    sqlcmd -S "${MSSQL_HOST},${MSSQL_PORT}" -U sa -P "${MSSQL_SA_PASSWORD}" -C \
        -Q "
        IF EXISTS (SELECT name FROM sys.databases WHERE name = '${DATABASE}')
        BEGIN
            ALTER DATABASE [${DATABASE}] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
        END;
        RESTORE DATABASE [${DATABASE}] FROM DISK = N'${BACKUP_FILE}' WITH REPLACE, STATS = 10;
        ALTER DATABASE [${DATABASE}] SET MULTI_USER;
        "
fi

echo "=== Restore Complete ==="
echo "Database '${DATABASE}' has been restored from ${BACKUP_FILE}"
