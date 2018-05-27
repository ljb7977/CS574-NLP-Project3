-------------------------------------------------------------------------------------------------------------------
                  MaltOptimizer 1.0
-------------------------------------------------------------------------------------------------------------------
The following package contains the Java implementation of MaltOptimizer.

TO TEST THE SYSTEM PLEASE FOLLOW THE INSTRUCTIONS IN THIS README FILE


The zip file contains:
-MaltOptimizer.jar
-malt.jar (MaltParser jar file)
-Example training set for English (Stanford Dependencies in CoNLL format): WSJ00.basic.conll
-Default feature model files (*.xml)
-Evaluation scripts (*.pl)
-Validation scripts (*.py and *.pyc)
-Lib directory (needed for MaltParser)
-This README file

Note: WSJ000.basic.conll treebank is just a small example training set and the results will
therefore not be representative of the accuracy (or improvement) that can be obtained with
a real training set. Running the 3 optimization phases with WSJ00.basic.conll takes 15-20
minutes on a Core I5 (2.3Ghz) laptop.

-------------------------------------------------------------------------------------------------------------------
INSTALLING THE SYSTEM


Running MaltOptimizer requires a Java Runtime Environment (JRE). You need version JRE 1.6
or later version. If you don't have JRE installed on your system, download and install the latest
version of the Java Runtime Environment.

1. Unzip the zip file.

2. After installing MaltOptimizer, you can run it to verify that it is working properly on your system.
  To run MaltOptimizer type the following at the command line prompt (it is important that you are
  in the MaltOptimizer installation directory):

     java -jar MaltOptimizer.jar

If MaltOptimizer displays something like the message below, the distribution has been installed
successfully.

-----------------------------------------------------------------------------
                  MaltOptimizer 1.0
-----------------------------------------------------------------------------

-----------------------------------------------------------------------------
Usage:
java -jar MaltOptimizer.jar -p  -m <-path to MaltParser-> -c <-path to training corpus->
java -jar malt.jar -h for more help and options

-----------------------------------------------------------------------------

USAGE:
 java -jar MaltOptimizer.jar -p <-phase number-> -m <-MaltParser jar path-> -c <-training corpus->

-------------------------------------------------------------------------------------------------------------------
EXAMPLE RUN

To run MaltOptimizer on the example data set, execute the following commands:


  java -jar MaltOptimizer.jar -p 1 -m malt.jar -c WSJ00.basic.conll
  java -jar MaltOptimizer.jar -p 2 -m malt.jar -c WSJ00.basic.conll
  java -jar MaltOptimizer.jar -p 3 -m malt.jar -c WSJ00.basic.conll

Note: Phase 1 must be run before Phase 2, and Phase 2 before Phase 3.

-------------------------------------------------------------------------------------------------------------------
PHASE 1: DATA ANALYSIS

In the data analysis, MaltOptimizer gathers information about the following properties of the

training set:

  Number of words/sentences
  Percentage of non-projective arcs/trees
  Existence of ''covered roots'' (arcs spanning tokens with HEAD = 0)
  Frequency of labels used for tokens with HEAD = 0
  Existence of non-empty feature values in the LEMMA and FEATS columns
  Identity (or not) of feature values in the CPOSTAG and POSTAG columns

Usage:
  java -jar MaltOptimizer.jar -p 1 -m <-MaltParser jar path-> -c <-training corpus->

-------------------------------------------------------------------------------------------------------------------
PHASE 2: PARSING ALGORITHM SELECTION

MaltOptimizer selects the best algorithm implemented in MaltParser for the input training set.

Usage:
  java -jar MaltOptimizer.jar -p 2 -m <-MaltParser jar path-> -c <-training corpus->

-------------------------------------------------------------------------------------------------------------------
PHASE 3: FEATURE SELECTION

MaltOptimizer runs the following feature selection experiments:

1. Tune the window of POSTAG n-grams over the parser state
2. Tune the window of FORM features over the parser state
3. Tune DEPREL and POSTAG features over the partially built dependency tree
4. Add POSTAG and FORM features over the input string
5. Add CPOSTAG, FEATS, and LEMMA features if available
6. Add conjunctions of POSTAG and FORM features

Usage:
  java -jar MaltOptimizer.jar -p 3 -m <-MaltParser jar path-> -c <-training corpus->

------------------------------------------------------------------------------------------------------------------
EXTRA OPTIONS

Evaluation metric (-e)
  las =  labeled attachment score (DEFAULT)
  uas = unlabeled attachment score
  lcm = labeled complete match
  ucm = unlabeled complete match

Usage:
  java -jar MaltOptimizer.jar -p <-phase number-> -m <-MaltParser jar path-> -c <-training corpus-> -e (las|uas|lcm|ucm)

Punctuation in evaluation (-s)
  true = include punctuation (DEFAULT)
  false = exclude punctuation


Usage:
  java -jar MaltOptimizer.jar -p <-phase number-> -m <-MaltParser jar path-> -c <-training corpus-> -s false
