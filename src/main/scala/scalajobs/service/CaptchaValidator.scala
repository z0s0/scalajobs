package scalajobs.service

import scalajobs.SttpClientService
import sttp.client.circe._
import sttp.client._
import scalajobs.configuration.RecaptchaConfig
import scalajobs.model.form.CaptchaVerificationResponse
import zio.{Has, Task, URLayer, ZIO, ZLayer}

object CaptchaValidator {
  val DEFAULT_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify"

  type CaptchaValidator = Has[Service]
  type Deps = Has[RecaptchaConfig] with Has[SttpClientService]

  trait Service {
    def isValid(captcha: String): Task[Boolean]
  }

  val live: URLayer[Deps, CaptchaValidator] = ZLayer.fromServices[RecaptchaConfig, SttpClientService, Service]{
    case (conf, client) =>
      (captcha: String) => {
        val reqBody = Map("secret" -> conf.apiSecret, "response" -> captcha)

        val req = basicRequest
          .post(uri"${DEFAULT_VERIFY_URL}")
          .body(reqBody)
          .response(asJson[CaptchaVerificationResponse])

        client.send(req).map(_.body).absolve.fold(_ => false, _.success)
      }
  }

  def isValid(captcha: String): ZIO[CaptchaValidator, Throwable, Boolean] =
    ZIO.accessM[CaptchaValidator](_.get.isValid(captcha))
}
