package _05other_topics

import scala.util.chaining._
import util._

package object jsonsupport_circe {

  final case class Contributor(login: String, contributions: Int)

  def printContributors(repo: String, contributors: List[Contributor]): Unit = {
    s"$line5 Contributors of repo $repo $line10".cyan pipe println
    contributors.foreach { contributor =>
      println(s"Contributor ${contributor.login} made ${contributor.contributions} contributions")
    }
    printContributorsSummary(repo, contributors.size, contributors.map(_.contributions).sum)
  }

  def printContributorsSummary(repo: String, contributors: Int, contributions: Int): Unit =
    s"Repo $repo has ${contributors} contributors who made ${contributions} contributions in total.".cyan pipe println
}
