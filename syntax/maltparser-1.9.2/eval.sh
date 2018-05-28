#!/bin/sh
for feature in ./features/*
do
	path=`echo $feature`
	name=`echo $feature | sed -e 's/.\/features\///g' | sed -e 's/.xml//g'`
	echo $name
	echo $path
	java -jar maltparser-1.9.2.jar -c $name -i data/kaist_train.conll -f finalOptionsFile.xml -F $path -m learn
	echo $name training done
	java -jar maltparser-1.9.2.jar -c $name -i data/kaist_dev.conll -o dev_out.conll -f finalOptionsFile.xml -F $path -m parse
	echo $name parsing done
	head -n -1 dev_out.conll > temp.txt
	mv temp.txt dev_out.conll
	java -jar MaltEval.jar -s dev_out.conll -g data/kaist_dev.conll
done
