path='./features/addMergPOSTAGS0I0FORMInput0.xml'
java -jar maltparser-1.9.2.jar -c final -i data/kaist_train.conll -f finalOptionsFile.xml -F $path -m learn
java -jar maltparser-1.9.2.jar -c final -i data/kaist_test.conll -o test_out.conll -f finalOptionsFile.xml -F $path -m parse