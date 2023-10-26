#!/bin/bash

# Fail on any error.
set -e

cd "${KOKORO_ARTIFACTS_DIR}/github/android-test-releases"

mkdir maven_repo
export MAVEN_CONFIG="${PWD}/maven_repo"
unzip "${KOKORO_GFILE_DIR}/axt_m2repository.zip" maven_repo

curl -s https://repos.azul.com/azul-repo.key | sudo gpg --dearmor -o /usr/share/keyrings/azul.gpg
echo "deb [signed-by=/usr/share/keyrings/azul.gpg] https://repos.azul.com/zulu/deb stable main" | sudo tee /etc/apt/sources.list.d/zulu.list
apt-get update
apt-get install -y zulu17-jdk
export JAVA_HOME="$(update-java-alternatives -l | grep "1.17" | head -n 1 | tr -s " " | cut -d " " -f 3)"

cd gradle-tests
./gradlew nexusOneApi30DebugAndroidTest --info -Pandroid.testoptions.manageddevices.emulator.gpu=swiftshader_indirect -Dandroid.experimental.androidTest.numManagedDeviceShards=1
