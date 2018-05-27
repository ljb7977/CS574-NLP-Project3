#!/bin/sh

java -jar maltparser-1.9.2.jar -c kaist -i data/kaist_dev.conll -o dev_out.conll -m parse
echo Parsing Done
head -n -1 dev_out.conll > temp.txt
mv temp.txt dev_out.conll
java -jar evaluator.jar dev_out.conll data/kaist_dev.conll