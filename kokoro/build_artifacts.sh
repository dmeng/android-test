#!/bin/bash

# Fail on any error.
set -e

# this directory must exist for artifacts to be uploaded
mkdir -p "${KOKORO_ARTIFACTS_DIR}/artifacts"
cd "${KOKORO_ARTIFACTS_DIR}/github/android-test-releases"

source kokoro/common.sh
install_bazelisk
# runner has @androidsdk//:legacy_test-30 as a dependency
/opt/android-sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root=/opt/android-sdk "platforms;android-30" "build-tools;30.0.0"
# building :axt_m2_repository uses @androidsdk//:build-tools/33.0.2/aapt2
/opt/android-sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root=/opt/android-sdk "build-tools;33.0.2"
/opt/android-sdk/cmdline-tools/latest/bin/sdkmanager --sdk_root=/opt/android-sdk "platforms;android-34" "build-tools;34.0.0"

bazelisk build :axt_m2repository

cp bazel-bin/axt_m2repository.zip .
unzip bazel-bin/axt_m2repository.zip -d /root/.m2

curl -s https://repos.azul.com/azul-repo.key | sudo gpg --dearmor -o /usr/share/keyrings/azul.gpg
echo "deb [signed-by=/usr/share/keyrings/azul.gpg] https://repos.azul.com/zulu/deb stable main" | sudo tee /etc/apt/sources.list.d/zulu.list
apt-get update
apt-get install -y zulu17-jdk
export JAVA_HOME="$(update-java-alternatives -l | grep "1.17" | head -n 1 | tr -s " " | cut -d " " -f 3)"

cd gradle-tests
./gradlew nexusOneApi30DebugAndroidTest --info -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect -Dandroid.experimental.androidTest.numManagedDeviceShards=1

