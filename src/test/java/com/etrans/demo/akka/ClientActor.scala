package com.etrans.demo.akka

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

class ClientActor(host:String,port:Int) extends Actor{

  var selector : ActorSelection = _

  override def preStart(): Unit = {
    selector = context.actorSelection(s"akka.tcp://server@$host:$port/user/serverActor")
  }

  override def receive: Receive = {
    case "start" => println("客户端已启动")
    case mes:String => selector ! ClientMessage(mes)
    case ServerMesage(mes) => println(s"接收来自服务端消息：$mes")
  }

}


object ClientActor {
  def main(args: Array[String]): Unit = {

    val host  = "127.0.0.1"
    val clientPort  = 9089
    val port = 9088

    val config = ConfigFactory.parseString(s"""
      | akka.actor.provider="akka.remote.RemoteActorRefProvider"
      | akka.remote.netty.tcp.hostname=$host
      | akka.remote.netty.tcp.port=$clientPort
      """.stripMargin
    )

    val myFactory = ActorSystem("client", config)
    val clientActor = myFactory.actorOf(Props(new ClientActor(host,port)), "clientActor")
    clientActor ! "start"

    while(true){
      Thread.sleep(500)
      val readLine = StdIn.readLine("请输入内容：")
      clientActor ! readLine
      if (readLine.equals("stop")) {
        Thread.sleep(500)
        println("客户端即将关闭")
        System.exit(0)

      }

    }
  }
}
