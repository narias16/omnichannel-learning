package co.edu.eafit.dis.kealearningstream.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.server._
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import play.api.libs.ws.ahc.AhcWSComponents
import co.edu.eafit.dis.kealearningstream.api.KealearningStreamService
import co.edu.eafit.dis.kealearning.api.KealearningService
import com.softwaremill.macwire._

class KealearningStreamLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new KealearningStreamApplication(context) {
      override def serviceLocator: NoServiceLocator.type = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new KealearningStreamApplication(context) with LagomDevModeComponents

  override def describeService = Some(readDescriptor[KealearningStreamService])
}

abstract class KealearningStreamApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with AhcWSComponents {

  // Bind the service that this server provides
  override lazy val lagomServer: LagomServer = serverFor[KealearningStreamService](wire[KealearningStreamServiceImpl])

  // Bind the KealearningService client
  lazy val kealearningService: KealearningService = serviceClient.implement[KealearningService]
}
