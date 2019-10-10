package co.edu.eafit.dis.user.impl


import java.util.Date

import akka.Done
import co.edu.eafit.dis.user.api.User
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, AggregateEventTagger, PersistentEntity}
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

class UserEntity extends PersistentEntity {
  override type Command = UserCommand[_]
  override type Event = UserEvent
  override type State = UserState

  override def initialState: UserState = UserState(null, null, null, null, null, null, null, null)

  override def behavior: Behavior = {
    case UserState(_, _, _, _, _, _, _, _) => Actions().onCommand[CreateUser, Done] {
        case (CreateUser(user), ctx, _) =>
          ctx.thenPersist(
            UserCreated(user)
          ) { _ =>
            ctx.reply(Done)
          }
      }.onReadOnlyCommand[GetUserObject, User] {
        case (GetUserObject(id), ctx, state) =>
          if (id == state.id)
            ctx.reply(User(state.id, state.address, state.email, state.phone, state.birthdate,
                                      state.gender, state.learningStyle, state.language))
          else ctx.commandFailed(UserNotFoundException("User doesn't exist"))
      }.onEvent {
        case (UserCreated(user), _) => user match {
          case User(id, address, email, phone, birthdate, gender, learningStyle, language) =>
            UserState(id, address, email, phone, birthdate, gender, learningStyle, language)
        }
      }
  }
}

/**
  * STATE
  * The current state held by the persistent entity.
  * */
case class UserState(
                      id: String,
                      address: String,
                      email: String,
                      phone: String,
                      birthdate: Date,
                      gender: String,
                      learningStyle: String,
                      language: String)

object UserState {
  implicit val format: Format[UserState] = Json.format
}

/**
  * EVENTS
  * This interface defines all the events that the UserEntity supports.
  * */
sealed trait UserEvent extends AggregateEvent[UserEvent] {
  override def aggregateTag: AggregateEventTagger[UserEvent] = UserEvent.Tag
}

object UserEvent {
  val Tag: AggregateEventTag[UserEvent] = AggregateEventTag[UserEvent]
}

/**
  * An event that represents a save in [[UserState]].
  */
case class UserCreated(user: User) extends UserEvent

object UserCreated {

  /**
    * Format for the user created event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialize.
    */
  implicit val format: Format[UserCreated] = Json.format
}


/**
  * COMMANDS
  *
  * This interface defines all the commands that the UserEntity supports.
  */
sealed trait UserCommand[R] extends ReplyType[R]

/**
  * A command to create the user object
  *
  * It has a reply type of [[Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */
case class CreateUser(user: User) extends UserCommand[Done]

object CreateUser {

  /**
    * Format for the [[CreateUser]] command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[CreateUser] = Json.format
}

case class UserNotFoundException(msg: String) extends Throwable

object UserNotFoundException {
  implicit val format: Format[UserNotFoundException] = Json.format
}

/**
  * A command that represents a read only for the [[UserState]]
  *
  * It has a reply type of User
  * */
case class GetUserObject(id: String) extends UserCommand[User]

object GetUserObject {
  implicit val format: Format[GetUserObject] = Json.format
}

/**
  * Akka serialization, used by both persistence and remoting, needs to have
  * serializers registered for every type serialized or deserialized. While it's
  * possible to use any serializer you want for Akka messages, out of the box
  * Lagom provides support for JSON, via this registry abstraction.
  *
  * The serializers are registered here, and then provided to Lagom in the
  * application loader.
  */

object UserSerializeRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[UserState],
    JsonSerializer[UserCreated],
    JsonSerializer[CreateUser],
    JsonSerializer[UserNotFoundException]
  )
}