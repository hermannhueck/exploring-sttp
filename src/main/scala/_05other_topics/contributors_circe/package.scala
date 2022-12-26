package _05other_topics

import scala.util.chaining._
import util._

package object contributors_circe {

  final case class Contributor(login: String, contributions: Int)

  def printContributors(repo: String, contributors: List[Contributor]): Unit = {
    s"$line5 Contributors of repo $repo $line10".cyan pipe println
    contributors.foreach { contributor =>
      println(s"Contributor ${contributor.login} made ${contributor.contributions} contributions")
    }
    printContributorsSummary(repo, contributors.size, contributors.map(_.contributions).sum)
  }

  def printMostBusyContributor(repo: String, contributors: List[Contributor]): Unit = {
    val mostBusyContributor = contributors.maxBy(_.contributions)
    s"The most busy contributor of repo $repo is '${mostBusyContributor.login}' with ${mostBusyContributor.contributions} contributions.".cyan pipe println
  }

  def printContributorsSummary(repo: String, contributors: Int, contributions: Int): Unit =
    s"Repo $repo has ${contributors} contributors who made ${contributions} contributions in total.".cyan pipe println
}
