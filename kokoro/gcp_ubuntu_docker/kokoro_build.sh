#!/bin/bash

# Fail on any error.
set -e

cd "${KOKORO_ARTIFACTS_DIR}/github/android-test-releases"

# runner has @androidsdk//:legacy_test-30 as a dependency
/opt/android-sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root=/opt/android-sdk "platforms;android-30" "build-tools;30.0.0"
# building :axt_m2_repository uses @androidsdk//:build-tools/33.0.2/aapt2
/opt/android-sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root=/opt/android-sdk "build-tools;33.0.2"
/opt/android-sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root=/opt/android-sdk "platforms;android-34" "build-tools;34.0.0"

apt-get update
apt-cache madison openjdk-17
apt-get install -y openjdk-17-jdk=17.0.7+7
export JAVA_HOME="$(update-java-alternatives -l | grep "1.17" | head -n 1 | tr -s " " | cut -d " " -f 3)"

cd gradle-tests
./gradlew nexusOneApi30DebugAndroidTest
