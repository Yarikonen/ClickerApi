package com.cats.api.endpoints
import com.cats.api.data.User
import zhttp.http.*
import zio.*
import zio.json.*
import com.cats.api.repositories.UserRepo


object UserApp :
  def apply(): Http[UserRepo,Throwable,Request,Response]=
    Http.collectZIO[Request]{

      case Method.GET -> !! / "click"/id =>
        UserRepo
          .click(id)
          .map(clicks => Response.text(clicks.toString))
      case req @ (Method.POST -> !! / "users") =>
        for
          u <- req.bodyAsString.map(_.fromJson[User])
          r <- u match
            case Left(e) =>
              ZIO
                .debug(s"Failed to parse the input: $e")
                .as(
                  Response.text(e).setStatus(Status.BadRequest)
                )
            case Right(u) =>
              UserRepo
                .register(u)
                .map(id => Response.text(id))
        yield r

    }

