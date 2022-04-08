package widgetsinc.core

import java.util.UUID
import cats.effect.kernel.Sync

trait GenUUID[F[_]]:
  def uuid: F[UUID]

object GenUUID:
  def apply[F[_]: GenUUID] = summon

  given [F[_]: Sync]: GenUUID[F] with
    def uuid: F[UUID] = Sync[F].delay(UUID.randomUUID)
