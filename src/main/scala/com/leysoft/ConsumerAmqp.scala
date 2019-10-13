package com.leysoft

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.amqp.{AmqpConnectionProvider, AmqpCredentials, AmqpDetailsConnectionProvider, NamedQueueSourceSettings, ReadResult}
import akka.stream.alpakka.amqp.scaladsl.AmqpSource
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

object ConsumerAmqp extends App {
  implicit val system = ActorSystem("amqp-system")
  implicit val materializer = ActorMaterializer()

  val queueName = "akka.q"
  val credentials: AmqpCredentials = AmqpCredentials(username = "guest", password = "guest")
  val connection: AmqpConnectionProvider =  AmqpDetailsConnectionProvider(host = "localhost", port = 5672)
    .withCredentials(credentials)

  val amqpSource: Source[ReadResult, NotUsed] =
    AmqpSource.atMostOnceSource(
      NamedQueueSourceSettings(connection, queueName)
        .withAckRequired(false),
      bufferSize = 10
    )

  val result: Future[Seq[ReadResult]] =
    amqpSource
      .map{ item => println(item)
        item
      }.runWith(Sink.seq)
}
