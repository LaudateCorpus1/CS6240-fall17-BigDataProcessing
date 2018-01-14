Team Mates -
1. Rohan Chitnis
2. Saurabh Singh

Final Prediction output - ChitnisSingh.csv

We have model training and prediction in the same source file.
src -> ModelFile.scala

controller logs - Single log file for Training and Prediction program
controller logs for 12, 8 and 5 node execution are in the parent directory.


makefile -
has 3 parameters for running main function -
1. input - training data directory
2. output - output directory
3. validation/ testing data directory
Executions -
1. Local execution - 
	run "make alone" commmand for local execution
	training data should be in "training_data" directory
	For Validation - validation data should be kept in "validation_data" directory (remove test data if exists)
	For Testing - testing data should be kept in "validation_data" directory (remove validation data if exists)
	
2. AWS execution - 
	run "make cloud" commmand for AWS EMR execution
	change 3 parameters accordingly - 
		1. aws.input=.. 
		2. aws.output=..
		3. aws.valid=..

