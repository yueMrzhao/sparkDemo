package com.etrans.demo


import com.mongodb.spark.MongoSpark
import org.apache.spark.sql.SparkSession
import org.bson.Document

class AccAnalyser {


  def analyser(spark : SparkSession) : Unit = {
    val rdd = MongoSpark.load(spark.sparkContext)
    val query = Document.parse("{$match:{'vehicle_id':374}}")
    val project = Document.parse("{$project:{vehicle_id:'$vehicle_id',vehicle_no:'$vehicle_no',work_unit_name:'$work_unit_name',gps_time:'$gps_time',acc:'$acc'}} ")
    val aggregateRdd = rdd.withPipeline(Seq(project))

    var count:Int = 0
    val result  = aggregateRdd.reduce((k,v) => {

      val acc:Option[Boolean] = Option(v.getBoolean("acc"))

      acc match {
        case Some(true) => count += 1
        case _ =>
      }

      new Document("count", count)
    })

    println(result)

  }

}