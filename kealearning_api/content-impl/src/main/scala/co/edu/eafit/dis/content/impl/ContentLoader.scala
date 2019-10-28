package co.edu.eafit.dis.content.impl

import co.edu.eafit.dis.content.api.ContentService
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import play.api.libs.ws.ahc.AhcWSComponents
import com.softwaremill.macwire._

class ContentLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication =
    new ContentApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ContentApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[ContentService])
}

abstract class ContentApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[ContentService](wire[ContentServiceImpl])

  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = ContentSerializeRegistry

  // Register the user persistent entity
  persistentEntityRegistry.register(wire[ContentEntity])

  // Register any ReadSideProcessor's if any
  readSide.register(wire[ContentEntityProcessor])
}