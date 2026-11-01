#!/usr/bin/env bash
set -euo pipefail
# Сначала: ./gradlew connectedDebugAndroidTest
cd "$(dirname "$0")/.."
for lab in 1 2 3 4; do
  mkdir -p "docs/screenshots/lab${lab}"
  adb pull "/sdcard/Android/data/ru.kharlov.lab${lab}/files/screenshots/." "docs/screenshots/lab${lab}/"
done
