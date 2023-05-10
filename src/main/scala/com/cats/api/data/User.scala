package com.cats.api.data
import zio.json.*
import java.util.UUID

case class User(login: String, clicks: Int = 0)

object User:
  given JsonEncoder[User] =
    DeriveJsonEncoder.gen[User]
  given JsonDecoder[User] =
    DeriveJsonDecoder.gen[User]