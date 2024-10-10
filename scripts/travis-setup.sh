#!/bin/bash -xv
base=${INDEX_DIR:-/data/lucene}
version=${INDEX_VERSION:-20230725-5}
bucket=${DATA_BUCKET:-ala-nameindexes}
if [ -z "${AWS_AP}" ]; then
  access=""
else
  access=.ap-${AWS_AP}
fi
access=${AWS_AP:us-east-1}
sudo mkdir -p "$base"
sudo chown "$USER" "$base"
sudo chmod ug+rwx "$base"
for f in linnaean vernacular location
do
  file="${f}-${version}.zip"
  curl --output "${base}/${file}" "https://${bucket}.s3${access}.amazonaws.com/${version}/${file}"
  unzip -d "${base}" "${base}/${file}"
done