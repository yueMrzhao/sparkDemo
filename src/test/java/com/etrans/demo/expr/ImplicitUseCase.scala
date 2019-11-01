package com.etrans.demo.expr

class ImplicitUseCase(someOne: SomeOne) {
  def speak(): Unit = {
    println("I'm speaking ")
  }

  def play(implicit name: String):Unit = {
    println("I'm playwhith "+ name)
  }
}

class SomeOne

object ImplicitUseCase extends App {
  implicit def speak2(a:SomeOne) = new ImplicitUseCase(a)

  val someOne = new SomeOne

  someOne.speak()  // 方法的隐式，调用方法的类必须是隐式方法所在类的成员变量

  implicit var name = "Lucy"

  someOne.play("Tome")
  someOne.play    // 参数的隐式， 在作用域内 设定隐式参数，在方法参数用implicit修饰，当找不到参数时，会找作用域的隐式参数

  implicit def converIntString(aa: Int):String = {
    aa + ""
  }

  someOne.play(1) // 利用了上面 int 转换为String 的隐式方法，所以play 能接受不是 String 的 int 参数

  implicit class Calcultor(a:Int) {
    def add(b: Int): Int = {
      a+b
    }
  }

  println(1.add(2))  // 类的隐式







}
