#!/usr/bin/env bash
# backup-sqlserver.sh — Backup ShumelaHire SQL Server database
# Usage: ./scripts/backup-sqlserver.sh [container_name] [backup_dir]
#
# Environment variables:
#   MSSQL_SA_PASSWORD  — SA password (required)
#   MSSQL_HOST         — SQL Server host (default: localhost)
#   MSSQL_PORT         — SQL Server port (default: 1433)
#   MSSQL_DATABASE     — Database name (default: shumelahire)

set -euo pipefail

CONTAINER_NAME="${1:-shumelahire-sqlserver}"
BACKUP_DIR="${2:-./backups}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
DATABASE="${MSSQL_DATABASE:-shumelahire}"
BACKUP_FILE="${DATABASE}_${TIMESTAMP}.bak"

# Validate SA password is set
if [ -z "${MSSQL_SA_PASSWORD:-}" ]; then
    echo "ERROR: MSSQL_SA_PASSWORD environment variable is required"
    exit 1
fi

# Create backup directory if it doesn't exist
mkdir -p "${BACKUP_DIR}"

echo "=== ShumelaHire SQL Server Backup ==="
echo "Database:  ${DATABASE}"
echo "Container: ${CONTAINER_NAME}"
echo "Backup:    ${BACKUP_FILE}"
echo "Time:      $(date -u +%Y-%m-%dT%H:%M:%SZ)"
echo "======================================"

# Check if running in Docker
if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    echo "Running backup via Docker container..."

    # Create backup inside the container
    docker exec "${CONTAINER_NAME}" /opt/mssql-tools18/bin/sqlcmd \
        -S localhost -U sa -P "${MSSQL_SA_PASSWORD}" -C \
        -Q "BACKUP DATABASE [${DATABASE}] TO DISK = N'/backup/${BACKUP_FILE}' WITH FORMAT, INIT, COMPRESSION, STATS = 10"

    # Copy backup from container to host
    docker cp "${CONTAINER_NAME}:/backup/${BACKUP_FILE}" "${BACKUP_DIR}/${BACKUP_FILE}"

    echo "Backup copied to ${BACKUP_DIR}/${BACKUP_FILE}"
else
    echo "Running backup via direct SQL Server connection..."

    MSSQL_HOST="${MSSQL_HOST:-localhost}"
    MSSQL_PORT="${MSSQL_PORT:-1433}"

    sqlcmd -S "${MSSQL_HOST},${MSSQL_PORT}" -U sa -P "${MSSQL_SA_PASSWORD}" -C \
        -Q "BACKUP DATABASE [${DATABASE}] TO DISK = N'${BACKUP_DIR}/${BACKUP_FILE}' WITH FORMAT, INIT, COMPRESSION, STATS = 10"
fi

# Calculate backup size
if [ -f "${BACKUP_DIR}/${BACKUP_FILE}" ]; then
    BACKUP_SIZE=$(du -h "${BACKUP_DIR}/${BACKUP_FILE}" | cut -f1)
    echo "Backup completed: ${BACKUP_DIR}/${BACKUP_FILE} (${BACKUP_SIZE})"
else
    echo "WARNING: Backup file not found at ${BACKUP_DIR}/${BACKUP_FILE}"
    exit 1
fi

# Cleanup old backups (keep last 7 days)
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-7}"
echo "Cleaning up backups older than ${RETENTION_DAYS} days..."
find "${BACKUP_DIR}" -name "${DATABASE}_*.bak" -mtime "+${RETENTION_DAYS}" -delete 2>/dev/null || true

echo "=== Backup Complete ==="
