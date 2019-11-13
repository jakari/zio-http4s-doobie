package example.database.Migrations

import zio._
import scala.io.Source

case class MigrationVersion(version: String, path: String)

object FolderReader {
  def getUpVersions: List[MigrationVersion] = files("up")

  private def files(direction: String) = {
    val folderStream = getClass.getResourceAsStream("/migrations")

    if (folderStream == null) {
      throw new Error("Migrations directory missing")
    }

    val versionMatcher = ("""^version-(\d{14})-""" + direction + """\.sql$""").r
    val versions = Source.fromInputStream(folderStream)
      .getLines()
      .toList
      .flatMap(file => {
        file match {
          case versionMatcher(version) => Some(MigrationVersion(version, file))
          case _ => None
        }
      })
      .sortBy(_.version)

    versions
  }

  def readFile(path: String): Task[String] = {
    ZIO.effect[String] {
      val handle = Source.fromInputStream(getClass.getResourceAsStream("/migrations/" + path))
      val content = handle.getLines.mkString
      handle.close()
      content
    }
  }
}
