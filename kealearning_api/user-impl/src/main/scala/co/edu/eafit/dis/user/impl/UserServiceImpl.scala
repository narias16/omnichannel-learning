package co.edu.eafit.dis.user.impl

import java.util.UUID

import akka.NotUsed
import co.edu.eafit.dis.user.api.{User, UserData, UserService}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import java.text.SimpleDateFormat
import scala.concurrent.Future

class UserServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends UserService {
  /**
    * read on the write side
    **/
  override def getUserObject(user_id: String): ServiceCall[NotUsed, User] = { _ =>
    val ref = persistentEntityRegistry.refFor[UserEntity](user_id)

    ref.ask(GetUserObject(user_id))
  }

  override def newUser: ServiceCall[UserData, UserID] = { userData =>
    val id = UUID.randomUUID().toString

    val ref = persistentEntityRegistry.refFor[UserEntity](id)

    val user: User = userData match {
      case UserData(address, email, phone, birthdate, gender, learningStyle, language) =>
        User(id,
          address,
          email,
          phone,
          new SimpleDateFormat("dd/MM/yyyy").parse(birthdate),
          gender,
          learningStyle,
          language)
    }

    ref.ask(CreateUser(user))

    Future.successful(user.id)
  }
}