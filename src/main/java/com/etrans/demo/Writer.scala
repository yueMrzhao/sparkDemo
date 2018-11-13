package com.etrans.demo

import com.mongodb.spark._
import org.apache.spark.SparkContext
import org.bson.Document
class Writer {

  def writer (doc : Document, sc : SparkContext) : Unit = {
      sc.parallelize(Seq(doc))
  }

  def method (f : (Seq[Document], SparkContext) => Unit, seq : Seq[Document], sc : SparkContext) : Unit = {
    f(seq, sc)
  }

  val writerDoc = (docs : Seq[Document], sc : SparkContext) => {
    val rdd = sc.parallelize(docs)
    rdd.toJavaRDD()
    MongoSpark.save(rdd)
  }



}
