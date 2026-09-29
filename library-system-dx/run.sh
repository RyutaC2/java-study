#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
exec java -jar target/library-system-dx-1.0.0.jar
