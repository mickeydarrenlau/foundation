#!/usr/bin/env bash
set -e

BACKBLAZE_B2_VERSION="3.6.0"

curl -sL -o b2 "https://github.com/Backblaze/B2_Command_Line_Tool/releases/download/v${BACKBLAZE_B2_VERSION}/b2-linux"
chmod +x b2
./b2 authorize-account "${ARTIFACTS_KEY_ID}" "${ARTIFACTS_APP_KEY}"
./b2 sync --delete --replaceNewer artifacts/ "b2:///${ARTIFACTS_BUCKET}/foundation/"
