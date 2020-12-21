package example.database.Migrations

import example.database.Connection
import zio._
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.util.update.Update0
import zio.interop.catz._

object Migrations {
  def up(): ZIO[Connection, Throwable, Any] = {
    ZIO.accessM[Connection](c => {
      val xa = c.get.xa
      for {
        _ <- createVersionTable.transact(xa)
        installed <- getExistingMigrations.transact(xa)
        versions <- ZIO.effect(FolderReader.getUpVersions.filter(file => !installed.contains(file.version)))
        migrated <- migrateVersions(xa, versions)
      } yield migrated
    })
  }

  private def createVersionTable =
    sql"CREATE TABLE IF NOT EXISTS migration_versions(version VARCHAR(14), executed_at timestamp without time zone)".update.run

  private def getExistingMigrations =
    sql"SELECT version FROM migration_versions"
      .query[String]
      .to[List]

  private def migrateVersions(xa: Transactor[Task], versions: List[MigrationVersion]): ZIO[Any, Throwable, Any] = {
    if (versions.isEmpty) ZIO.succeed(0)
    else
      versions.map {
        version => for {
          contents <- FolderReader.readFile(version.path)
            _ <- Update0(contents, None).run.transact(xa)
            _ <- sql"INSERT INTO migration_versions VALUES(${version.version}, CURRENT_TIMESTAMP)".update.run.transact(xa)
        } yield ()
      }
        .reduce((a, b) => a.flatMap(_ => b))
  }
}
