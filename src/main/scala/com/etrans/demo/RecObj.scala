package com.etrans.demo

import java.util

abstract class RecObj {

  def encodeKey: String

  def encodeVal: String

  def encodeTo(_dst: util.Map[String, String]): Unit = {
    _dst.put(encodeKey, encodeVal)
  }
}
