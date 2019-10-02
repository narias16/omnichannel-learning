package co.edu.eafit.dis.kealearning.impl

import co.edu.eafit.dis.kealearning.api
import co.edu.eafit.dis.kealearning.api.KealearningService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}

/**
  * Implementation of the KealearningService.
  */
class KealearningServiceImpl(persistentEntityRegistry: PersistentEntityRegistry) extends KealearningService {

  override def hello(id: String) = ServiceCall { _ =>
    // Look up the kealearning entity for the given ID.
    val ref = persistentEntityRegistry.refFor[KealearningEntity](id)

    // Ask the entity the Hello command.
    ref.ask(Hello(id))
  }

  override def useGreeting(id: String) = ServiceCall { request =>
    // Look up the kealearning entity for the given ID.
    val ref = persistentEntityRegistry.refFor[KealearningEntity](id)

    // Tell the entity to use the greeting message specified.
    ref.ask(UseGreetingMessage(request.message))
  }

  override def greetingsTopic(): Topic[api.GreetingMessageChanged] =
    TopicProducer.singleStreamWithOffset {
      fromOffset =>
        persistentEntityRegistry.eventStream(KealearningEvent.Tag, fromOffset)
          .map(ev => (convertEvent(ev), ev.offset))
    }

  private def convertEvent(helloEvent: EventStreamElement[KealearningEvent]): api.GreetingMessageChanged = {
    helloEvent.event match {
      case GreetingMessageChanged(msg) => api.GreetingMessageChanged(helloEvent.entityId, msg)
    }
  }
}