package example.database.Migrations

import doobie.ConnectionIO
import example.database.Transaction
import zio._
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.update.Update0

object Migrations {
  def up(): ZIO[Transaction, Throwable, Any] = {
    ZIO.accessM[Transaction]( t =>
      FolderReader
        .getUpVersions
        .flatMap(getMigrationContent)
        .flatMap(
          versions => t.get.transact {
            migrate(versions)
          }
        )
    )
  }

  private def migrate(versions: List[MigrationVersion]): ConnectionIO[Unit] = for {
    _ <- createVersionTable
    installed <- getExistingMigrations
    _ <- versions
      .filter(file => !installed.contains(file.version))
      .map(migrateVersion)
      .foldRight(Fragment.empty.update.run)((a, b) => a.flatMap(_ => b))
  } yield ()

  def migrateVersion(content: MigrationVersion) = for {
    _ <- Update0(content.content, None).run
      _ <- sql"INSERT INTO migration_versions VALUES(${content.version}, CURRENT_TIMESTAMP)".update.run
  } yield ()

  private def createVersionTable =
    sql"CREATE TABLE IF NOT EXISTS migration_versions(version VARCHAR(14), executed_at timestamp without time zone)".update.run

  private def getExistingMigrations =
    sql"SELECT version FROM migration_versions"
      .query[String]
      .to[List]

  private def getMigrationContent(paths: List[MigrationVersionPath]): ZIO[Any, Throwable, List[MigrationVersion]] =
    ZIO.collectAll {
      paths.map { version =>
        FolderReader
          .readFile(version.path)
          .map(MigrationVersion(version))
      }
    }
}
