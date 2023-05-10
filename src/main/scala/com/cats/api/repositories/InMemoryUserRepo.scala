package com.cats.api.repositories
import zio.*

import scala.collection.mutable
import com.cats.api.data.User
import com.cats.api.repositories.UserRepo

case class InMemoryUserRepo(map: Ref[Map[String,User]]) extends UserRepo:
  def register(user: User): UIO[String] =
    for
      - <- map.updateAndGet(_ + (user.login -> user))
    yield user.login

  override def click(id: String): UIO[Int] =
    for {
      clicks <- map.modify(map => {
        val user = map(id)
        val updatedUser = user.copy(clicks = user.clicks + 1)
        (updatedUser.clicks,map.updated(id, updatedUser))
      })
    } yield clicks


  def lookup(id: String): UIO[Option[User]] =
    map.get.map(_.get(id))


  def users: UIO[List[User]] =
    map.get.map(_.values.toList)
object InMemoryUserRepo {
  def layer: ZLayer[Any, Nothing, InMemoryUserRepo] =
    ZLayer.fromZIO(
      Ref.make(Map.empty[String, User]).map(new InMemoryUserRepo(_))
    )
}