package scalajobs.model

import cats.data.Validated

trait Form {
  def validate: Validated[List[String], String]

  def nonEmpty(str: Option[String]) = str.getOrElse("").length > 0
  def positive(int: Option[Int]) = int.getOrElse(0) > 0
}
