package com.cats.api.repositories


import com.cats.api.data.User
import io.getquill.context.ZioJdbc.DataSourceLayer
import io.getquill.{Escape, PostgresZioJdbcContext}
import io.getquill.jdbczio.Quill
import io.getquill.*
import zio.*

import java.util.UUID
import javax.sql.DataSource


case class UserTable(uuid: UUID, name: String, clicks: Int)

class PersistentUserRepo(ds: DataSource) extends UserRepo {
  val ctx = new PostgresZioJdbcContext(Escape)

  import ctx._

  override def register(user: User): Task[String] = {
    for
      id <- Random.nextUUID
      _ <- ctx.run {
        quote {
          query[UserTable].insertValue {
            lift(UserTable(id, user.login, user.clicks))
          }
        }
      }
    yield id.toString
  }.provide(ZLayer.succeed(ds))

  override def click(id: String): Task[Int] =
    ctx
      .run{
        quote{
          query[UserTable]
            .filter(p=>p.uuid == lift(UUID.fromString(id)))
            .update(p=>p.clicks->(p.clicks+1))

        }.returning(_.clicks)
      }
      .provide(ZLayer.succeed(ds))


  override def lookup(id: String): Task[Option[User]] =
    ctx
      .run {
        quote {
          query[UserTable]
            .filter(p => p.uuid == lift(UUID.fromString(id)))
            .map(u => User(u.name, u.clicks))
        }
      }
      .provide(ZLayer.succeed(ds))
      .map(_.headOption)

  override def users: Task[List[User]] =
    ctx
      .run {
        quote {
          query[UserTable].map(u => User(u.name, u.clicks))
        }
      }
      .provide(ZLayer.succeed(ds))


}
object PersistentUserRepo:
  def layer: ZLayer[Any, Throwable, PersistentUserRepo] =
    Quill.DataSource.fromPrefix("UserApp") >>>
      ZLayer.fromFunction(PersistentUserRepo(_))

