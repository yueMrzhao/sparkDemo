package com.etrans.demo

import java.util
import java.util.function.Consumer

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.{UpdateOneModel, UpdateOptions, WriteModel}
import com.mongodb.spark.MongoConnector
import com.mongodb.spark.config.WriteConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.bson.Document

import scala.collection.JavaConverters._


object SparkStream extends Logging {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster(args(0)).setAppName("hta-stream")
    val streamingContext = new StreamingContext(conf, Seconds(20))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.4.81:9092",
      "key.deserializer" -> classOf[ByteArrayDeserializer],
      "value.deserializer" -> classOf[ByteArrayDeserializer],
      "group.id" -> "hta-stream",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (true: java.lang.Boolean)
    )

    val write_conf = WriteConfig(Map("uri" -> "mongodb://%s".format("192.168.4.224:20000,192.168.4.228:20000,192.168.4.231:20000,192.168.4.232:20000")
      , "database" -> "coach_business_test"
      , "collection" -> "gps_info_191101"
    ))



    val topics = Array("RSAVER-TRACK")
    val stream = KafkaUtils.createDirectStream[Array[Byte], Array[Byte]](
      streamingContext,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[Array[Byte], Array[Byte]](topics, kafkaParams)
    )
//      .window(Minutes(60), Minutes(60))
    stream
      .foreachRDD{rdd =>

        val wordCounts = rdd.map(
          { case record : ConsumerRecord[Array[Byte], Array[Byte]]  =>
          {
            val track = Document.parse(new String(record.value(), "GBK"))
//            println(track)
            track
          }
        })

        val mongoConnector = MongoConnector(write_conf.asOptions)
        wordCounts.foreachPartition(iter => if (iter.nonEmpty) {
          mongoConnector.withCollectionDo(write_conf, { collection: MongoCollection[Document] =>
            iter.grouped(write_conf.maxBatchSize).foreach(batch =>
              try {
//                collection.insertMany(batch.toList.asJava)

                val writeModels = new util.ArrayList[WriteModel[Document]]()
                batch.toList.asJava.forEach(new Consumer[Document] {
                  override def accept(row: Document): Unit = {
                    writeModels.add(new UpdateOneModel[Document](
                      new Document("_id", row.getString("_id")).append("vehicle_id", row.getInteger("vehicle_id"))
                      , new Document("$set", row)
                      , new UpdateOptions().upsert(true)))
                  }
                })
                collection.bulkWrite(writeModels)
              } catch {
                case e:Exception =>
                  log.error(e.getMessage)
              })

          })
        })
      }

    //启动
    streamingContext.start()
    //等到
    streamingContext.awaitTermination()

  }
}
