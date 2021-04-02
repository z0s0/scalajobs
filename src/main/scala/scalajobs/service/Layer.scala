package scalajobs.service

import scalajobs.dao.Layer.Daos
import scalajobs.service.OrganizationService.OrganizationService
import scalajobs.service.VacancyService.VacancyService
import zio.ZLayer

object Layer {
  type Services = VacancyService with OrganizationService

  val live
    : ZLayer[Daos, Nothing, Services] = VacancyService.live ++ OrganizationService.live
}
