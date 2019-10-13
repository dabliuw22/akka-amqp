package com.leysoft

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.amqp.{AmqpConnectionProvider, AmqpCredentials, AmqpDetailsConnectionProvider, AmqpWriteSettings, BindingDeclaration, Declaration, ExchangeDeclaration, QueueDeclaration}
import akka.stream.alpakka.amqp.scaladsl.AmqpSink
import akka.stream.scaladsl.Source
import akka.util.ByteString

object ProducerAmqp extends App {
  implicit val system = ActorSystem("amqp-system")
  implicit val materializer = ActorMaterializer()

  val routingKey = "akka.rk"
  val queueName = "akka.q"
  val exchangeName = "akka.e"
  val credentials: AmqpCredentials = AmqpCredentials(username = "guest", password = "guest")
  val connection: AmqpConnectionProvider =  AmqpDetailsConnectionProvider(host = "localhost", port = 5672)
    .withCredentials(credentials)
  val queue = QueueDeclaration(queueName)
    .withAutoDelete(false)
    .withDurable(true)
  val exchange = ExchangeDeclaration(exchangeName, "direct")
  val binding = BindingDeclaration(queueName, exchangeName).withRoutingKey(routingKey)

  Source(List("Hello", "Akka", "Alpakka", "RabbitMQ"))
    .map { item => ByteString(item) }
    .runWith(AmqpSink.simple(AmqpWriteSettings(connection)
      .withRoutingKey(routingKey)
      .withExchange(exchangeName)
      .withDeclarations(Seq(exchange, queue, binding))))
}