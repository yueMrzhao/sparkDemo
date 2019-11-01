package com.etrans.demo.metric

import java.util.concurrent.{LinkedBlockingDeque, TimeUnit}

import com.codahale.metrics.{ConsoleReporter, Gauge, MetricRegistry, Slf4jReporter}
import org.apache.spark.internal.Logging

class ApplicationSource() extends MetricSource with Logging{
  override val sourceName = "source"

  override val metricRegistry = new MetricRegistry()

  val quene = new LinkedBlockingDeque[Int]()

//  val consoleMetric = ConsoleReporter.forRegistry(metricRegistry).build()

  var slf4jReporter = Slf4jReporter.forRegistry(metricRegistry).build()

  metricRegistry.register(MetricRegistry.name("metricTest","queue.size"), new Gauge[Int]{
    override def getValue = quene.size()
  })

  // 也可以外部累加
  val outerInc = metricRegistry.counter(MetricRegistry.name("metricTest","outer", "inc"))

  // 每秒请求量
  val meter = metricRegistry.meter(MetricRegistry.name("metricTest","queue", "request"))

}

object ApplicationSource {
  def apply(): ApplicationSource = new ApplicationSource()
}

object ApplicationTest extends App {
  val applicationSource:ApplicationSource = ApplicationSource()
//  applicationSource.consoleMetric.start(1, TimeUnit.SECONDS)
  applicationSource.slf4jReporter.start(1,TimeUnit.SECONDS)
  for (i <- 0 to 100000 if i % 2 == 0) {
    applicationSource.quene.add(i)
    Thread.sleep(1)
    applicationSource.outerInc.inc
    applicationSource.meter.mark()

  }

}
