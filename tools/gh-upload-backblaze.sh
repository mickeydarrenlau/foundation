#!/usr/bin/env bash
set -e

BACKBLAZE_B2_VERSION="3.6.0"
BACKBLAZE_B2_PLATFORM="linux"
BACKBLAZE_B2_REPO="https://github.com/Backblaze/B2_Command_Line_Tool"

[ "$(uname -s)" = "Darwin" ] && BACKBLAZE_B2_PLATFORM="darwin"

curl --fail -sL -o b2 "${BACKBLAZE_B2_REPO}/releases/download/v${BACKBLAZE_B2_VERSION}/b2-${BACKBLAZE_B2_PLATFORM}"
chmod +x b2
./b2 authorize-account "${ARTIFACTS_KEY_ID}" "${ARTIFACTS_APP_KEY}"
./b2 sync --delete --replaceNewer --allowEmptySource artifacts/ "b2://${ARTIFACTS_BUCKET}/foundation/"
rm b2
