#!/usr/bin/env bash
set -e

rm -rf artifacts/

mkdir -p artifacts/
mkdir -p artifacts/build/manifests
cp build/manifests/update.json artifacts/build/manifests/

find . -name "*-plugin.jar" | grep "foundation-" | while read -r jar
do
  DN=`dirname ${jar}`
  mkdir -p "artifacts/$DN"
  cp $jar "artifacts/$jar"
done
