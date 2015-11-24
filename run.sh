#!/usr/bin/env bash
adb logcat -c
adb logcat V:* > logcat.txt &
LOGCAT_PID=$!

echo $LOGCAT_PID

chmod +x gradlew
./gradlew --stacktrace cAT
