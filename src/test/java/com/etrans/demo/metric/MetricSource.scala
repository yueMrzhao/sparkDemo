package com.etrans.demo.metric

import com.codahale.metrics.MetricRegistry

trait MetricSource {

  val sourceName: String
  val metricRegistry: MetricRegistry

}
