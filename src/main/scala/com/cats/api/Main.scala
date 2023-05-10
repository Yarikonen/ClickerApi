package com.cats.api
import com.cats.api.endpoints.UserApp
import com.cats.api.repositories.{InMemoryUserRepo, PersistentUserRepo}
import zio.*
import zhttp.service.Server

object Main extends ZIOAppDefault:
  def run: ZIO[Environment with ZIOAppArgs with Scope, Any, Any] =
    Server.start(port = 8080, http = UserApp())
      .provide(
        PersistentUserRepo.layer
      )


