package com.etrans.demo

import java.util.function.Consumer

import com.etrans.demo.util.DateUtils
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model._
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
    val streamingContext = new StreamingContext(conf, Seconds(60))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "192.168.4.81:9092",
      "key.deserializer" -> classOf[ByteArrayDeserializer],
      "value.deserializer" -> classOf[ByteArrayDeserializer],
      "group.id" -> "hta-stream3",
      "auto.offset.reset" -> "earliest", // latest earliest none
      "session.timeout.ms" -> "60000",
      "request.timeout.ms" -> "70000",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val write_conf = WriteConfig(Map("uri" -> "mongodb://%s".format("192.168.4.224:20000,192.168.4.228:20000,192.168.4.231:20000,192.168.4.232:20000")
      , "database" -> args(1)
      , "collection" -> "gps_info_total"
    ))

    val topics = Array("RSAVER-TRACK")
    val stream = KafkaUtils.createDirectStream[Array[Byte], Array[Byte]](
      streamingContext,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[Array[Byte], Array[Byte]](topics, kafkaParams)
    )
     // .window(Minutes(60), Minutes(60))
    stream
      .foreachRDD{rdd =>

        val wordCounts = rdd.map(
          { case record : ConsumerRecord[Array[Byte], Array[Byte]]  =>
          {
            val track = Document.parse(new String(record.value(), "GBK"))
            // println(track)
            track
          }
        })

        val mongoConnector = MongoConnector(write_conf.asOptions)
        wordCounts.foreachPartition(iter => if (iter.nonEmpty) {

          mongoConnector.withDatabaseDo(write_conf, { database: MongoDatabase =>
            iter.grouped(write_conf.maxBatchSize).foreach(batch =>
              try {

                var maps = Map[String, java.util.ArrayList[WriteModel[Document]]]()
                var writeModels: java.util.ArrayList[WriteModel[Document]] = null
                batch.toList.asJava.forEach(new Consumer[Document] {
                  override def accept(row: Document): Unit = {
                    val gpsTime = row.getDate("gt")
                    val tableName = s"gps_info_${DateUtils.format(gpsTime, "yyMMdd")}"
                    if (maps.contains(tableName)) {
                     writeModels = maps(tableName)
                    } else {
                      writeModels = new java.util.ArrayList[WriteModel[Document]]()
                      maps += (tableName -> writeModels)
                    }

                    writeModels.add(new InsertOneModel[Document](row))
                  }
                })

                for (elem <- maps) {
                  val collection = database.getCollection(elem._1)
                  collection.bulkWrite(writeModels, new BulkWriteOptions().ordered(false))
                }

              } catch {
                case e:Exception =>
                  log.error(e.getMessage)
              })

          })
        })

        // 处理成功 手动提交offset
        val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        // some time later, after outputs have completed
        stream.asInstanceOf[CanCommitOffsets].commitAsync(offsetRanges)
      }

    //启动
    streamingContext.start()
    //等到
    streamingContext.awaitTermination()

  }
}
