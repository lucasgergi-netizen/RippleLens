#!/bin/sh
set -eu

if [ -n "${DATABASE_URL:-}" ] && [ -z "${SPRING_DATASOURCE_URL:-}" ]; then
  case "$DATABASE_URL" in
    postgresql://*)
      database_address=${DATABASE_URL#postgresql://}
      case "$database_address" in
        *@*) database_address=${database_address#*@} ;;
      esac
      if [ -z "$database_address" ]; then
        echo "DATABASE_URL does not contain a database address." >&2
        exit 1
      fi
      case "$database_address" in
        *\?*) separator="&" ;;
        *) separator="?" ;;
      esac
      export SPRING_DATASOURCE_URL="jdbc:postgresql://${database_address}${separator}sslmode=require"
      unset DATABASE_URL
      ;;
    *)
      echo "DATABASE_URL must use the postgresql:// scheme." >&2
      exit 1
      ;;
  esac
fi

exec java -jar /app/app.jar
