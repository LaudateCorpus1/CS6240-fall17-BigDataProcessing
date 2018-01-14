## About the project

Project is about classifying the image pixel as bright or dark based upon its neighbours. Images are of brain scans and is in 3D format. 6 images of size 100GB(approx) were analyzed and used to train the Random Forest Classifier Model predicted the brightes/darkeness of the pixel with 99.056% accuracy.

Code was written in scala and run on Amazon Elastic MapReduce. 

Team Mates -
1. [Rohan Chitnis](https://www.linkedin.com/in/rohan-chitnis-57615b79/)
2. [Saurabh Singh](https://www.linkedin.com/in/saurabhsingh13nov/)

Source file: `ModelFile.scala`

Final Prediction output is stored in file- `ChitnisSingh.csv`

controller logs - Single log file for Training and Prediction program
controller logs for 12, 8 and 5 node execution are in the parent directory.

## How to run the code

a) Download the repository :
```shell
git clone https://github.com/saurabhsingh13no/CS6240-fall17-BigDataProcessing.git
```
b) Change directory to the root folder :
```shell
cd CS6240-fall17-BigDataProcessing
```
c) For Local or AWS execution, follow below steps:

makefile -
has 3 parameters for running main function -
1. input - training data directory(create this directory)
2. output - output directory
3. validation- testing data directory(create this directory)
Executions -
1. Local execution - 
	run `make alone` commmand for local execution
	training data should be in "training_data" directory
	For Validation - validation data should be kept in "validation_data" directory (remove test data if exists)
	For Testing - testing data should be kept in "validation_data" directory (remove validation data if exists)
	
2. AWS execution - 
	run `make cloud` commmand for AWS EMR execution
	change 3 parameters accordingly - 
		1. aws.input=.. 
		2. aws.output=..
		3. aws.valid=..

