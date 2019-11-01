package com.etrans.demo.rddMethod

import org.apache.spark.sql.SparkSession
import org.apache.spark.{HashPartitioner, SparkContext}

class RddTest {


  private[rddMethod] def testFlatMap(sc: SparkContext):Unit = {
    val links = Seq(("www.com.zy",Seq("www.com.qq","www.com.lyq","www.com.zds")),("www.com.lyq",Seq("www.com.zy","www.com.zds")),
      ("www.com.qiqi",Seq("www.com.zy")),("www.com.zds",Seq("www.com.lyq","www.com.zy")))

    val linkrdd = sc.parallelize(links).partitionBy(new HashPartitioner(4)).persist

    var rankrdd = linkrdd.mapValues(v => 1.0)

    for (i <- 1 to 10 ) {
      val rankScore = linkrdd.join(rankrdd).flatMap{
        case (k,(eachlinks,rank)) => {
          eachlinks.map(link => (link, rank/eachlinks.size))
        }
      }


      rankrdd = rankScore.reduceByKey((x,y) => x+y).mapValues(v => 0.85 + 0.15*v)
    }

    val userdir = System.getProperty("user.dir")
    rankrdd.map{
      case (k,v) => (v,k)}
      .sortByKey()
      .saveAsTextFile(s"${userdir}/out/pagerank")

    linkrdd.unpersist()
  }


}

object RddTest extends App {

  val rddTest = new RddTest

  val sc = SparkSession.builder()
    .appName("zy-test")
    .master("local[4]")
    .getOrCreate()

  rddTest.testFlatMap(sc.sparkContext)




}
