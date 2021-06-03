package scalajobs.api

import scalajobs.model.ClientError.Invalid
import scalajobs.service.Layer.Services
import scalajobs.service.{CaptchaValidator, VacancyService}
import zio.IO
import sttp.tapir.ztapir._

object VacanciesRoutes {
  val listVacancies = Docs.vacanciesDocs.zServerLogic { _ =>
    VacancyService.list(List()).orElseFail(())
  }

  val getVacancy = Docs.vacancyDocs.zServerLogic { id =>
    VacancyService.get(id).catchAll(_ => IO.fail("internal err")).flatMap {
      case Some(vacancy) => IO.succeed(vacancy)
      case None          => IO.fail("not found")
    }
  }

  val createVacancy = Docs.createVacancyDocs.zServerLogic { form =>
    form.validate.fold(
      errors => IO.fail(errors.map(Invalid)),
      _ => {
        val captchaError = List(Invalid("Cannot validate captcha"))

        CaptchaValidator.isValid(form.captcha.get)
          .catchAll(_ => IO.fail(captchaError))
          .flatMap(captchaValid =>
            if (captchaValid)
              VacancyService.create(form).mapError(List(_))
            else
              IO.fail(captchaError)
          )
      }
    )
  }

  val routes = List(
    listVacancies.widen[Services],
    getVacancy.widen[Services],
    createVacancy.widen[Services]
  )
}
