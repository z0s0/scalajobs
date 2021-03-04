package scalajobs.api

import scalajobs.api.OrganizationsRoutes.OrganizationsRoutes
import scalajobs.api.VacanciesRoutes.VacanciesRoutes
import scalajobs.service.Layer.Services
import zio.ZLayer

object Layer {
  type Routes = VacanciesRoutes with OrganizationsRoutes

  val live
    : ZLayer[Services, Nothing, Routes] = VacanciesRoutes.live ++ OrganizationsRoutes.live

}
