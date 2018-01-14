# Makefile for MapReduce project.

# Customize these paths for your environment.
# -----------------------------------------------------------

spark.root=/home/rohan/Downloads/spark-2.2.0-bin-hadoop2.7
jar.name=Model5-1.jar
jar.path=target/${jar.name}
local.input=training_data
local.output=output
local.valid=validation_data
# AWS EMR Execution
job.name=src.ModelFile
aws.emr.release=emr-5.2.1
aws.ami.version=3.11.0
aws.region=us-east-2
aws.bucket.name=rohan-mr-project
aws.subnet.id=subnet-773e051e
aws.input=input
aws.output=output5nodesCoalesceSeparateTimings2
aws.valid=test

aws.log.dir=log
aws.num.nodes=5
aws.instance.type=m4.large
# -----------------------------------------------------------
# LOCAL: STAND-ALONE MODE
#------------------------------------------------------------
# Compiles code and builds jar (with dependencies).
jar:
	mvn clean package

# Removes local output directory.
clean-local-output:
	rm -rf ${local.output}*

# Runs standalone
# Make sure Hadoop  is set up (in /etc/hadoop files) for standalone operation (not pseudo-cluster).
# https://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-common/SingleCluster.html#Standalone_Operation
alone: jar
	${spark.root}/bin/spark-submit --class ${job.name} --master local[*] ${jar.path} ${local.input} ${local.output} ${local.valid}


#-------------------------------------------------------------
# AWS CONFIGURATIONS:
#-------------------------------------------------------------
# Create S3 bucket.
make-bucket:
	aws s3 mb s3://${aws.bucket.name}

# Upload data to S3 input dir.
upload-input-aws:
	aws s3 sync ${local.input} s3://${aws.bucket.name}/${aws.input}
	
# Delete S3 output dir.
delete-output-aws:
	aws s3 rm s3://${aws.bucket.name}/ --recursive --exclude "*" --include "${aws.output}*"

# Upload application to S3 bucket.
upload-app-aws: 
	aws s3 cp ${jar.path} s3://${aws.bucket.name}

# Main EMR launch. ##### upload-app-aws
cloud: jar upload-app-aws
	aws emr create-cluster \
		--name "Subset" \
		--release-label ${aws.emr.release} \
		--instance-groups '[{"InstanceCount":${aws.num.nodes},"InstanceGroupType":"CORE","InstanceType":"${aws.instance.type}"},{"InstanceCount":1,"InstanceGroupType":"MASTER","InstanceType":"${aws.instance.type}"}]' \
	    --applications Name=Spark \
	    --steps '[{"Name":"Spark Program", "Args":["--class", "${job.name}", "--master", "yarn", "--deploy-mode", "cluster", "s3://${aws.bucket.name}/${jar.name}", "s3://${aws.bucket.name}/${aws.input}","s3://${aws.bucket.name}/${aws.output}","s3://${aws.bucket.name}/${aws.valid}"],"Type":"Spark","Jar":"s3://${aws.bucket.name}/${jar.name}","ActionOnFailure":"TERMINATE_CLUSTER"}]' \
		--log-uri s3://${aws.bucket.name}/${aws.log.dir} \
		--service-role EMR_DefaultRole \
		--ec2-attributes InstanceProfile=EMR_EC2_DefaultRole,SubnetId=${aws.subnet.id} \
		--configurations '[{"Classification":"spark", "Properties":{"maximizeResourceAllocation": "true"}}]' \
		--region ${aws.region} \
		--enable-debugging \
		--auto-terminate
		

