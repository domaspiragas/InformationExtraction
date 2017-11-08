# InformationExtraction

Compile the project with the following command in the root directory:

javac -cp stanford-corenlp-3.8.0.jar:stanford-corenlp-3.8.0-models.jar Template.java Constants.java infoextract.java


Run the program with the following command: 

java -cp .:stanford-corenlp-3.8.0.jar:stanford-corenlp-3.8.0-models.jar infoextract < path to input file >

where < path to input file > is replaced with the actual path to the input file
