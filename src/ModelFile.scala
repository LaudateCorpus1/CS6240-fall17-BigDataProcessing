package src
import scala.reflect.runtime.universe

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier,LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.feature.Bucketizer
import org.apache.spark.ml.feature.Normalizer
import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.functions.mean
import org.apache.spark.ml.evaluation.{MulticlassClassificationEvaluator, BinaryClassificationEvaluator}
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions.mean
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.mllib.tree.model.DecisionTreeModel

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object ModelFile {

	def main(args: Array[String]) {

	  // Spark Job Configuration
	  val conf = new SparkConf().setAppName("LoadDW")
		val sc = new SparkContext(conf)
		val sqlContext= new org.apache.spark.sql.SQLContext(sc)

	  // Reading Training Data
		val rdd = sc.textFile(args(0)+"/*.csv")
		// Convert string to Double as required by Random Forest Algorithm
		val data = rdd.map(_.split(",")).map(_.map(_.toDouble))
		
		import org.apache.spark.mllib.linalg.Vectors
    import org.apache.spark.mllib.regression.LabeledPoint

    // Create LabeledPoint for each row with label and feature vector
    val labeledPoints = data.map(x => LabeledPoint(if (x.last == 1) 1 else 0, 
    Vectors.dense(x.init)))

    // Using entire data for training
		val splits = labeledPoints.randomSplit(Array(1, 0), seed = 5043l)

		// Reading validation / testing file
		val rdd2 = sc.textFile(args(2)+"/*.csv")

		// Using entire data for training
    val trainingData = splits(0)

    // Convert test / validation data from string to Double
    val data2 = rdd2.map(_.split(",")).map(x => x.init).map(_.map(_.toDouble))
    
    // Create LabeledPoint from test / validation data for each row with label and feature vector
    val labeledPoints2 = data2.map(x => LabeledPoint(1,Vectors.dense(x)))
    val testData = labeledPoints2
    
    import org.apache.spark.mllib.tree.configuration.Algo
    import org.apache.spark.mllib.tree.impurity.Gini

    
    // set algorithm parameters
    val algorithm = Algo.Classification
    val impurity = Gini
    // 5, 10, 15
    val maximumDepth = 5
    // 100, 150, 200
    val treeCount = 200
    val featureSubsetStrategy = "auto"
    val seed = 5043
    
    import org.apache.spark.mllib.tree.configuration.Strategy
    import org.apache.spark.mllib.tree.RandomForest
    
    val trainStart = System.currentTimeMillis()

    // Train model
    val model = RandomForest.trainClassifier(trainingData, new Strategy(algorithm, 
      impurity, maximumDepth), treeCount, featureSubsetStrategy, seed)
      
    val trainEnd = System.currentTimeMillis()
    
    val predictStart = System.currentTimeMillis()

    // Predict for each row of test data
    val labeledPredictions = testData.map { labeledPoint =>
        val predictions = model.predict(labeledPoint.features)
        (1, predictions)
    }
	  
	  
		// Saving output file
	  labeledPredictions.map(x => x._2.toInt).coalesce(1).saveAsTextFile(args(1))
	  
	  val predictEnd = System.currentTimeMillis()
	  
	  println("Model Training Time - ")
	  println(trainEnd - trainStart)
	  println("Model Prediction Time - ")
	  println(predictEnd - predictStart)
	  
		
	}
	
}
