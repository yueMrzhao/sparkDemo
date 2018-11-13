package com.etrans.demo

import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.bson.Document
import com.mongodb.spark._
import com.mongodb.spark.config.WriteConfig

import scala.collection.JavaConverters._


object MonodbConnector {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
                  .master("local")
                  .appName("zyLocal")
                  .config("spark.mongodb.input.uri", "mongodb://10.10.4.138:27017/coach_business.alarm_info_181020?readPreference=primaryPreferred")
                  .config("spark.mongodb.output.uri", "mongodb://10.10.4.138:27017/coach_business.output")
                  .getOrCreate();

    val sc = spark.sparkContext;

//    val document = sc.parallelize((1 to 10).map( a => Document.parse(s"{spark:$a}")))

//    sparkReadTest(spark)

//    sparkSaveTest(sc);

    scalaMethodWriterTest(sc);



//    val rd2 = sc.loadFromMongoDB(ReadConfig(Map("uri" -> "mongodb://10.10.4.138:27017/coach_business.alarm_info_181021"))).cache()



  }

  def sparkReadTest(spark : SparkSession) : Unit = {
    case class Alarm( alarm_kind: Long )
    val rdf = MongoSpark.load(spark)
    rdf.show();
  }

  def scalaMethodWriterTest(sc: SparkContext): Unit = {
    val writer = new Writer

    val doc = "{\"name\": \"Bilbo Baggins\", \"age\": 50}\n      {\"name\": \"Gandalf\", \"age\": 1000}\n      {\"name\": \"Thorin\", \"age\": 195}\n      {\"name\": \"Balin\", \"age\": 178}\n      {\"name\": \"Kíli\", \"age\": 77}\n      {\"name\": \"Dwalin\", \"age\": 169}\n      {\"name\": \"Óin\", \"age\": 167}\n      {\"name\": \"Glóin\", \"age\": 158}\n      {\"name\": \"Fíli\", \"age\": 82}\n      {\"name\": \"Bombur\"}"
      .trim.stripMargin.split("[\\n\\r]").toSeq;
    val writeConfig = WriteConfig(Map("collection" -> "spark", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))
    writer.method(writer.writerDoc, doc.map(Document.parse) , sc)
//    MongoSpark.save(rdd)

  }

  def sparkSaveTest(sc: SparkContext): Unit ={
    // write
    import org.bson.Document
        val doc = "{\"name\": \"Bilbo Baggins\", \"age\": 50}\n      {\"name\": \"Gandalf\", \"age\": 1000}\n      {\"name\": \"Thorin\", \"age\": 195}\n      {\"name\": \"Balin\", \"age\": 178}\n      {\"name\": \"Kíli\", \"age\": 77}\n      {\"name\": \"Dwalin\", \"age\": 169}\n      {\"name\": \"Óin\", \"age\": 167}\n      {\"name\": \"Glóin\", \"age\": 158}\n      {\"name\": \"Fíli\", \"age\": 82}\n      {\"name\": \"Bombur\"}"
                  .trim.stripMargin.split("[\\n\\r]").toSeq;

        sc.parallelize(doc.map(Document.parse)).saveToMongoDB()

        val document = sc.parallelize((1 to 10).map( a => Document.parse(s"{spark:$a}")))
        document.map(println(_))
        MongoSpark.save(document);


        val writeConfig = WriteConfig(Map("collection" -> "spark", "writeConcern.w" -> "majority"), Some(WriteConfig(sc)))
        document.saveToMongoDB(writeConfig)


        val documents = sc.parallelize(Seq(new Document("fruits", List("apple","oranges", "pears").asJava)))
        documents.map(println(_));
        documents.saveToMongoDB(writeConfig)
  }


}


