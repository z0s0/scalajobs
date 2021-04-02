package scalajobs.api

import java.util.UUID

import scalajobs.model.ClientError.Invalid
import scalajobs.service.VacancyService
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
      _ => VacancyService.create(form).mapError(List(_))
    )
  }

  val routes = List(listVacancies, getVacancy, createVacancy)
}
