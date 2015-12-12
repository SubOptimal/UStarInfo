#!/bin/sh

# executes UStarInfo on all tar files in the sample directory
# to generate teh sample files you need to execute create_samples.sh first

for file in $(ls *.tar)
do
  printf "\n###\n### execute: java -jar UStarInfo-*.jar ${file}\n###\n"
  java -jar ../target/UStarInfo-*.jar ${file}
  printf "\nprocess exit code: $?\n"
done
