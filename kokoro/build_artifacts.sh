#!/bin/bash

# Fail on any error.
set -e

# move to where kokoro put the repository root
cd "${KOKORO_ARTIFACTS_DIR}/github/android-test-releases"

unzip "${KOKORO_GFILE_DIR}/axt_m2repository.zip" -d "/root/.m2"

curl -s https://repos.azul.com/azul-repo.key | sudo gpg --dearmor -o /usr/share/keyrings/azul.gpg
echo "deb [signed-by=/usr/share/keyrings/azul.gpg] https://repos.azul.com/zulu/deb stable main" | sudo tee /etc/apt/sources.list.d/zulu.list
apt-get update
apt-get install -y zulu17-jdk
export JAVA_HOME="$(update-java-alternatives -l | grep "1.17" | head -n 1 | tr -s " " | cut -d " " -f 3)"

cd gradle-tests
cp gradle/verification-metadata.xml.kokoro gradle/verification-metadata.xml

./gradlew nexusOneDebugAndroidTest -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect -Dandroid.experimental.androidTest.numManagedDeviceShards=1

