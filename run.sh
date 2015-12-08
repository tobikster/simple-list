# !/usr/bin/env bash
adb logcat -c
adb logcat V:* > logcat.txt &
export LOGCAT_PID=$!

chmod +x gradlew
./gradlew --daemon --stacktrace cAT

echo "BEFORE"
ps -e | grep adb

echo "LOGCAT PID $LOGCAT_PID. Killing..."
kill $LOGCAT_PID
unset LOGCAT_PID

echo "AFTER"
ps -e | grep adb

wc -l logcat.txt >> log_sizes
echo ''
tail -n 10 log_sizes
