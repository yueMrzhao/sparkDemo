package com.etrans.demo.rddMethod

import org.apache.spark.rdd.PairRDDFunctions
import org.apache.spark.sql.SparkSession

class PairRdd(pair: PairRDDFunctions[Int,Int]) {


  def reduceBykey(): Unit ={
    pair.reduceByKey((x,y)=>x+y).collect().foreach(println)
  }

  def groupBykey(): Unit = {
    pair.groupByKey(3).foreach(println)
  }


  // 求每个键对应的平均值，必须是pair结构
  // 第一步 根据key分区，定义返回结构
  // 在各个分区 中元组1迭代加数值，元组2迭代加1
  // 在分区合并
  // map阶段 计算 k对应的 平均值，转换输出v的值
  def combineBykey(): Unit = {
    val result = pair.combineByKey(
      (v) => (v,1),
      (acc:(Int,Int),v) => (acc._1 +v, acc._2 +1),
      (acc1:(Int,Int),acc2:(Int,Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2)
    ).map({
      case (k,v) => (k, v._1/v._2.toFloat)
    })

    result.collect().foreach(println)
//    result.collectAsMap().map(println)
  }
}


object PairRdd extends App {

  def apply(pair: PairRDDFunctions[Int,Int]): PairRdd = new PairRdd(pair)

  val input = Seq((1,3),(2,3),(5,6),(2,7))


  val ss = SparkSession.builder()
    .master("local")
    .appName("zy-test")
    .getOrCreate()


  // 算法说明，得出(总和，个数)
  // 定义返回格式，并初始化值(0,0)
  // 在各个分区 中元组1迭代加数值，元组2迭代加1
  // 在分区合并
  val singleInput = Seq(1,2,3,4,5,6,6,3)
  val singleRdd = ss.sparkContext.parallelize(singleInput)
  singleInput.aggregate(0,0)(
    (x,y)=> (x._1 + y, x._2 +1),
    (x,y)=> (x._1 + y._1, x._2 + y._2)
  )


  val pair = ss.sparkContext.parallelize(input)

  val pairRdd = PairRdd(pair)

//  pairRdd.reduceBykey(ss.sparkContext)

//  pairRdd.groupBykey()

  pairRdd.combineBykey


}
