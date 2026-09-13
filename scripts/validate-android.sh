#!/usr/bin/env bash
# JobPulse validation entry point for local development and automation.
set -euo pipefail

project_root="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$project_root"

exec bash ./gradlew --no-daemon --stacktrace --continue \
  testDebugUnitTest \
  lintDebug \
  assembleDebug \
  compileReleaseKotlin \
  assembleDebugAndroidTest \
  "$@"
