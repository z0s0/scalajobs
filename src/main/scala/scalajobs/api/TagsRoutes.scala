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
    Tag(6, "Http4s"),
    Tag(7, "Apache Spark"),
    Tag(8, "Apache Flink"),
    Tag(9, "Hadoop"),
    Tag(10, "FS2"),
    Tag(11, "Doobie"),
    Tag(12, "Tagless Final"),
    Tag(13, "Izumi"),
    Tag(14, "Monix"),
    Tag(15, "ToFu")
  )
  private val qTags =
    List(
      Tag(300, "Kafka"),
      Tag(301, "NATS"),
      Tag(302, "RabbitMQ"),
      Tag(303, "SQS"),
      Tag(304, "Apache Pulsar")
    )

  private val dbTags = List(
    Tag(115, "PostgreSQL"),
    Tag(116, "Elasticsearch"),
    Tag(117, "Cassandra"),
    Tag(119, "Scylla"),
    Tag(120, "Riak"),
    Tag(121, "Oracle"),
    Tag(122, "Redis"),
    Tag(123, "Tarantool"),
    Tag(124, "Clickhouse"),
    Tag(125, "DynamoDB"),
    Tag(126, "MongoDB"),
    Tag(127, "Neo4j"),
    Tag(128, "Couchbase")
  )

  private val infraTags = List(
    Tag(200, "Kubernetes"),
    Tag(201, "AWS"),
    Tag(202, "Azure"),
    Tag(203, "GCP"),
    Tag(204, "Prometheus")
  )

  private val secondaryTags = List(Tag(2000, "GraphQL"), Tag(2001, "gRPC"))
  private val tags = dbTags ++ qTags ++ scalaTags ++ infraTags ++ secondaryTags

  val listTags = Docs.listTags.zServerLogic { _ => IO.succeed(tags) }

  val routes = List(listTags)
}
