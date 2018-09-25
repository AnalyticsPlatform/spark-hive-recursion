package org.kaveh_hariri.utility.spark.hive_recursion

import java.text.SimpleDateFormat

import scalikejdbc._
import org.apache.spark.rdd._
import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkException}
import org.apache.spark.sql.SaveMode
import scala.util.{Failure, Success, Try}
import org.kaveh_hariri.utility.spark.hive_recursion.models._
object MainRun {

  private val usage: String = "usage: appname, levelParallelism, sql, outPath, format"

  def calculateAncestors(id: String, tree: Map[String,Option[String]],expandTree:scala.collection.mutable.Map[String,List[String]]): List[String] =
    tree.getOrElse(id,None) match {
      case Some(parent) => id :: getAncestors(parent,tree,expandTree)
      case None => List(id)
    }

  def getAncestors(id: String, tree: Map[String,Option[String]],expandTree:scala.collection.mutable.Map[String,List[String]]): List[String] = {

    expandTree.getOrElseUpdate(id, calculateAncestors(id,tree,expandTree))
  }
  def main(args:Array[String]): Unit = {
    if(args.length!=5){
      println(usage)
      System.exit(1)
    }
    val Array(appname, levelParallelism, sql, outPath,format) = args
    //val tree = new MapAccumulatorV2()
    val dp_proc_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
    val spark = SparkSession.builder().appName(appname)
      .config("spark.sql.warehouse.dir", "/apps/hive/warehouse")
      .enableHiveSupport().getOrCreate()

    //spark.sparkContext.register(tree, "tree")

    spark.conf.set("mapreduce.fileoutputcommitter.algorithm.version", "2")
    spark.conf.set("spark.default.parallelism",levelParallelism)
    spark.conf.set("spark.serializer","org.apache.spark.serializer.KryoSerializer")
    spark.conf.set("spark.driver.extraJavaOptions", "-XX:+UseG1GC")
    spark.conf.set("spark.executor.extraJavaOptions", "-XX:+UseG1GC")
    spark.conf.set("spark.driver.maxResultSize", "5g")
    spark.conf.set("spark.sql.broadcastTimeout", "300")
    spark.conf.set("spark.network.timeout", "200")
    spark.conf.set("spark.executor.heartbeatInterval", "20")
    spark.conf.set("spark.yarn.executor.memoryOverhead","2g")
    import spark.implicits._

    val mRdd: RDD[(String,String)] = spark.sql(sql).as[(String, String)].rdd

    val tree = mRdd.map(v => v._1 -> Some(v._2)).collectAsMap().toMap

    val broadCastLookupMap = spark.sparkContext.broadcast(tree)

        val mRei: RDD[String] = spark.sparkContext.parallelize(tree.keys.toSeq)
        val finRDD: RDD[child_parent_level] = mRei.map(a => {
          val expandTree = collection.mutable.Map[String,List[String]]()
          getAncestors(a,broadCastLookupMap.value,expandTree).zipWithIndex.map(b => {
            child_parent_level(a,b._1,b._2, dp_proc_time)
          })}).flatMap(z => z)


        spark.createDataFrame(finRDD.filter(rei => rei.parent != null).filter(a => !a.parent.equalsIgnoreCase(""))).write.mode(SaveMode.Overwrite).format(format).save(outPath)


  }

}
