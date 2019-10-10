package co.edu.eafit.dis.user.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}


object UserService {
  val TOPIC_NAME = "user"
}

trait UserService extends Service {

  def newUser: ServiceCall[NotUsed, User]

  def getUserObject(user_id: String): ServiceCall[NotUsed, User]

  override def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("user")
      .withCalls(
        restCall(method = Method.GET, "/user/:id", getUserObject _),
        pathCall("/user/new/", newUser)
      )
      .withAutoAcl(autoAcl = true)
    // @formatter:on
  }
}

/**
  * The user case class
  * */
case class User(id: String)

object User {
  implicit val format: Format[User] = Json.format
}