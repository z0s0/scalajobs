package scalajobs.api

import scalajobs.model.Tag
import zio.IO
import sttp.tapir.ztapir._

object TagsRoutes {
  private val scalaTags = List(
    Tag(1, "ZIO"),
    Tag(2, "Cats-Effect"),
    Tag(3, "Scalaz"),
    Tag(4, "Akka"),
    Tag(5, "Play"),
    Tag(6, "Http4s")
  )
  private val qTags =
    List(Tag(7, "Kafka"), Tag(8, "NATS"), Tag(9, "RabbitMQ"), Tag(10, "SQS"))

  private val dbTags = List(
    Tag(15, "PostgreSQL"),
    Tag(16, "Elasticsearch"),
    Tag(17, "Cassandra"),
    Tag(19, "Scylla"),
    Tag(20, "Riak"),
    Tag(21, "Oracle"),
    Tag(22, "Redis"),
    Tag(23, "Tarantool"),
    Tag(24, "Clickhouse")
  )

  private val infraTags = List(
    Tag(200, "Kubernetes"),
    Tag(201, "AWS"),
    Tag(202, "Azure"),
    Tag(203, "GCP"),
    Tag(204, "Prometheus")
  )
  private val tags = dbTags ++ qTags ++ scalaTags ++ infraTags

  val listTags = Docs.listTags.zServerLogic { _ =>
    IO.succeed(tags)
  }

  val routes = List(listTags)
}
