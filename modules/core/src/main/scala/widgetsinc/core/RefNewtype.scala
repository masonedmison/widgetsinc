package widgetsinc.core

import cats.kernel.Eq
import cats.kernel.Order
import cats.Show
import io.circe.Encoder
import io.circe.Decoder
import eu.timepit.refined.api.RefinedType
import eu.timepit.refined.api.RefinedTypeOps
import cats.implicits.*

// from g. volpe's FEDA
abstract class RefNewtype[T, RT](
  using
  eqv: Eq[RT],
  ord: Order[RT],
  shw: Show[RT],
  env: Encoder[RT],
  dec: Decoder[RT],
  rt: RefinedType.AuxT[RT, T],
) extends Newtype[RT]:
  object Ops extends RefinedTypeOps[RT, T]

  def from(t: T): Either[String, Type] = Ops.from(t).map(apply(_))
  def fromOption(t: Option[T]): Either[String, Option[Type]] = t.fold(None.asRight[String])(from(_) map(Some.apply))
  def unsafeFrom(t: T): Type = apply(Ops.unsafeFrom(t))
