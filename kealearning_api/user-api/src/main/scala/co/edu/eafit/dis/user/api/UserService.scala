package co.edu.eafit.dis.user.api

import java.util.Date

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}


object UserService {
  val TOPIC_NAME = "user"
}

trait UserService extends Service {

  type UserID = String

  def newUser: ServiceCall[UserData, UserID]

  def getUserObject(user_id: UserID): ServiceCall[NotUsed, User]

  override def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named("user")
      .withCalls(
        restCall(method = Method.GET, "/user/:id", getUserObject _),
        restCall(method = Method.POST, "/user/new/", newUser)
      )
      .withAutoAcl(autoAcl = true)
    // @formatter:on
  }
}

/**
  * The user case class
  * */
case class User(id: String,
                address: String,
                email: String,
                phone: String,
                birthdate: Date,
                gender: String,
                learningStyle: String,
                language: String)


object User {
  implicit val format: Format[User] = Json.format
}

case class UserData(address: String,
                    email: String,
                    phone: String,
                    birthdate: String,
                    gender: String,
                    learningStyle: String,
                    language: String)

object UserData {
  implicit val format: Format[UserData] = Json.format
}