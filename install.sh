#!/usr/bin/env bash
set -e

echo "This script installs Foundation, it is developed against Paper and may depend on"
echo "Paper-specific features."
echo
echo "Installing..."

# Create the plugins directory if it doesn't exist.
if [ ! -d plugins ]; then
  echo "Creating plugins directory."
  mkdir plugins/
fi

# Base GitLab update manifest.
base_url="https://git.gorence.io/lgorence/foundation/-/jobs/artifacts/main/raw/"
query_params="job=build"

# Download the update manifest.
manifest=$(curl -Ls "$base_url/build/manifests/update.json?$query_params")

# Get plugins list from the manifest.
plugins=$(echo "$manifest" | jq -r 'keys | .[]')

# Download each plugin from the manifest, can also update plugins.
for plugin in $plugins
do
  # Determine download path, extract version and artifact path URL.
  dl_path="plugins/$plugin.jar"
  version=$(echo "$manifest" | jq -r " .[\"$plugin\"].version")
  artifact_path=$(echo "$manifest" | jq -r " .[\"$plugin\"].artifacts[0]")

  echo "Installing $plugin v$version to $dl_path"

  # Download the plugin and store it at the mentioned path.
  curl -Ls "$base_url/$artifact_path?$query_params" --output "$dl_path"
done
