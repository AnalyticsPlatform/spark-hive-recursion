# spark-hive-recursion
This project is intended to create a recursive data set, taking in a hive select query with child and parent values, writing a flattened data set with 4 columns: child, parent, level, dp_proc_time, in the format specified.

1) Build the package with sbt-clean-assembly
2) Execute with spark package with spark-submit

sh /<spark2 home>/bin/spark-submit  --class org.kaveh_hariri.utility.spark.hive_recursion.MainRun --master <master> --conf <conf1> --conf<conf2> <path to jar> <appname> <levelParallelism> "SELECT child, parent  FROM <hiveschema.hivetable>" "s3a://<bucket>/<path to file>" format (orc,parquet,etc)

This is a remake of this udf using spark -- the original udf did not function properly due to the distributed nature of these frameworks.  This project works correctly because a distinct map of the child/parent values is distributed to each node using a broadcast variable. https://blog.pythian.com/recursion-in-hive/
