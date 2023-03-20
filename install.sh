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
base_url="https://artifacts.gay.pizza/foundation"

# Download the update manifest.
manifest=$(curl --fail -Ls "$base_url/manifest.json" || (echo "Failed to download manifest."; exit 1))

# Get items list from the manifest.
items=$(echo "$manifest" | jq -r '.items[].name')

i=0

# Download each plugin from the manifest, can also update plugins.
for item in $items
do
  type=$(echo "$manifest" | jq -r " .items[$i].type")

  # Determine download path, extract version and artifact path URL.
  dl_path="plugins/$item.jar"
  version=$(echo "$manifest" | jq -r " .items[$i].version")

  function get_artifact_path() {
    echo "$manifest" | jq -r " .items[$i].files[] | select(.type == \"${1}\") | .path"
  }

  if [ "${type}" = "bukkit-plugin" ]
  then
    artifact_path=$(get_artifact_path "plugin-jar")
    echo "Installing $item v$version to $dl_path from $base_url/$artifact_path"
    # Download the plugin and store it at the mentioned path.
    curl --fail -Ls "$base_url/$artifact_path" --output "$dl_path" || (echo "Failed to download ${artifact_path}"; exit 1)
  fi

  i=$((i + 1))
done
