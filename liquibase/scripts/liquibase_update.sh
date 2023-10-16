#!/bin/sh
set -e


echo "Setting up liquibase"

: "${DATABASE_HOST:=docker.for.mac.localhost}"
: "${DATABASE_PORT:=3306}"
: "${DATABASE_USERNAME:=admin}"
: "${DATABASE_PASSWORD:=SecretPassword}"
: "${DATABASE_NAME:=cmsapi}"
: "${LOG_LEVEL:=warning}"
: "${DATABASE_CONNECTION_ATTEMPTS:=10}"
: "${CHANGELOG_FILE:=changelog.xml}"

cat >> /liquibase/liquibase.docker.properties <<EOF
changelog-file: $CHANGELOG_FILE
url: jdbc:mysql://$DATABASE_HOST:$DATABASE_PORT/$DATABASE_NAME?zeroDateTimeBehavior=convertToNull&createDatabaseIfNotExist=true
username: $DATABASE_USERNAME
password: $DATABASE_PASSWORD
log-level: $LOG_LEVEL
EOF

echo "Applying changes to the $DATABASE_NAME ($DATABASE_HOST:$DATABASE_PORT) database. Changelog: $CHANGELOG_FILE"

# Check to see if the DB is available
while ! mysql --host=$DATABASE_HOST --port=$DATABASE_PORT -u$DATABASE_USERNAME -p$DATABASE_PASSWORD -e "SELECT 1" >/dev/null; do
  : "$((DATABASE_CONNECTION_ATTEMPTS-=1))"
  if [ "$DATABASE_CONNECTION_ATTEMPTS" -gt 0 ]; then
    echo "Database connection attempt failed, $DATABASE_CONNECTION_ATTEMPTS attempts left"
    sleep 10
  else
    echo "Database connection attempt failed, aborting!"
    exit 1
  fi
done

echo "Generating SQL changes"
liquibase --defaultsFile=/liquibase/liquibase.docker.properties update-sql
echo "Executing the SQL changes"
liquibase --defaultsFile=/liquibase/liquibase.docker.properties update
