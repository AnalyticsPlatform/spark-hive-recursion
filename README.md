# spark-hive-recursion
This project is intended to create a recursive data set, taking in a hive select query with child and parent values. 

1) Build the package with sbt-clean-assembly
2) Execute with spark package with spark-submit

sh /<spark2 home>/bin/spark-submit  --class org.kaveh_hariri.utility.spark.hive_recursion.MainRun --master <master> --conf <conf1> --conf<conf2> <path to jar> <appname> <levelParallelism> "SELECT child, parent  FROM <hiveschema.hivetable>" "s3a://<bucket>/<path to file>" format (orc,parquet,etc)
