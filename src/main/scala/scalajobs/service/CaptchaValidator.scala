package scalajobs.service

import zio.{Has, Task, ULayer, ZIO, ZLayer}

object CaptchaValidator {
  type CaptchaValidator = Has[Service]

  trait Service {
    def isValid(captcha: String): Task[Boolean]
  }

  val live: ULayer[CaptchaValidator] = ZLayer.succeed((captcha: String) => Task(true))

  def isValid(captcha: String): ZIO[CaptchaValidator, Throwable, Boolean] =
    ZIO.accessM[CaptchaValidator](_.get.isValid(captcha))
}
