#!/usr/bin/env bash
set -e

echo "This script installs Foundation, it is developed against Paper and may depend on"
echo "Paper-specific features."
echo

# Ensure curl and jq are installed.
if ! hash curl jq &> /dev/null; then
  echo "curl and jq must be installed"
  exit 1
fi

echo "Installing..."

# Create the plugins directory if it doesn't exist.
if [ ! -d plugins ]; then
  echo "Creating plugins directory."
  mkdir plugins/
fi

# Base GitLab update manifest.
base_url="https://artifacts.gay.pizza/foundation/"

# Download the update manifest.
manifest=$(curl --fail -Ls "$base_url/build/manifests/update.json" || (echo "Failed to download manifest."; exit 1))

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
  curl --fail -Ls "$base_url/$artifact_path" --output "$dl_path" || (echo "Failed to download ${artifact_path}"; exit 1)
done
