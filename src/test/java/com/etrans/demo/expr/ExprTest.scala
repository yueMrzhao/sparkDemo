package com.etrans.demo.expr

object ExprTest {

  def main(args: Array[String]): Unit = {
//    ifTest()
    whileTest()
  }

  def ifTest() = {
    val max = 2;
    val result = if (max > 1)  1 else 0
    println(s"计算结果为：$result")
  }

  def whileTest() = {
    var (r, n) = (10,0)
    while (r > 0) {
      n = n + r
      r = r -1
    }

    println(s"计算结果为：$n")

    for (i <- 1 to 10) {
      println(s"for 循环输出结果：$i")
    }

    for (i <- 1 until  10) {
      println(s"for 循环输出结果：$i")
    }

    for (i <- 1 to 10 if i % 2 == 0) { // 取1到10的偶数
      println(s"for 循环输出结果：$i")
    }
  }

}
