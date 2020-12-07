package scalajobs.configuration

import zio.{Has, Layer, Tag, ULayer, URLayer, ZLayer}

import zio.config._
import zio.config.magnolia.DeriveConfigDescriptor._
import zio.config.typesafe._
import zio.config.ReadError

object Configuration {

  val rootDescriptor: ConfigDescriptor[Config] = descriptor[Config]

  val live: Layer[ReadError[String], Has[Config]] =
    TypesafeConfig.fromDefaultLoader(rootDescriptor)

  val noErrors: ULayer[Has[Config]] = live.orDie

  type AllConfigs = Has[ApiConfig] with Has[DbConfig]

  val allConfigs: ULayer[AllConfigs] =
    noErrors >>> (
      subConfig(_.dbConfig) ++
        subConfig(_.apiConfig)
    )

  def subConfig[T: Tag](f: Config => T): URLayer[Has[Config], Has[T]] =
    ZLayer.fromService(f)
}
