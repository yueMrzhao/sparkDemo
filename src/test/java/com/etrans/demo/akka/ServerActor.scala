package com.etrans.demo.akka

import akka.actor.{Actor, ActorSystem,Props}
import com.typesafe.config.ConfigFactory

class ServerActor extends Actor{

  override def receive: Receive = {
    case "start" => println("服务端开始启动")
    case ClientMessage(mes) => {
      println(s"服务端接收到：$mes")
      mes match {
        case "stop" => {
          println("服务端开始关闭")
          context.stop(self)
          context.system.terminate()
        }

        case mes:String => {
          sender() ! ServerMesage("服务器接收消息，并回复")
        }
      }
    }
  }

}

object ServerActor {
  def main(args: Array[String]): Unit = {
    val host = "127.0.0.1"
    val port = 9088
    val config = ConfigFactory.parseString(s"""
      | akka.actor.provider="akka.remote.RemoteActorRefProvider"
      | akka.remote.netty.tcp.hostname=$host
      | akka.remote.netty.tcp.port=$port
    """.stripMargin)
    val myFactory = ActorSystem("server",config)

    val actor = myFactory.actorOf(Props[ServerActor],"serverActor")

    actor ! "start"

  }
}




case class ServerMesage(mes:String)

case class ClientMessage(mes: String)