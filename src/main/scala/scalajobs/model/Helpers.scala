package scalajobs.model

import java.time.LocalDateTime
import java.util.UUID
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

import scala.util.Try

object Helpers {
  def isValidUUID(s: String) =
    Try(UUID.fromString(s)).fold(_ => false, _ => true)
  def isValidDateTime(str: String) =
    Try(localDateTimeFromISO8601(str)).fold(_ => false, _ => true)

  def localDateTimeFromISO8601(str: String) =
    LocalDateTime.parse(str, ISO_DATE_TIME)
}
