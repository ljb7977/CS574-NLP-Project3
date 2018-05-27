#!/bin/bash
java -jar MaltOptimizer.jar -p 1 -m maltparser-1.9.2.jar -c data/kaist_train.conll 2>&1 | tee resultMessages.txt
java -jar MaltOptimizer.jar -p 2 -m maltparser-1.9.2.jar -c data/kaist_train.conll 2>&1 | tee -a resultMessages.txt
java -jar MaltOptimizer.jar -p 3 -m maltparser-1.9.2.jar -c data/kaist_train.conll 2>&1 | tee -a resultMessages.txt