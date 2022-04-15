package widgetsinc.ordertaking.common
package simple

import widgetsinc.core.Newtype
import widgetsinc.core.RefNewtype

import io.circe.refined.*
import eu.timepit.refined.*
import eu.timepit.refined.string
import eu.timepit.refined.types.string.NonEmptyFiniteString
import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection
import eu.timepit.refined.cats.*
import eu.timepit.refined.boolean.And
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.auto.*

type String50 = String50.Type
object String50 extends RefNewtype[String, constraints.String50]

// must be non empty and contain @
type EmailAddress = EmailAddress.Type
object EmailAddress extends RefNewtype[String, constraints.Email]

// need to pattern match so using enum
// type WidgetCode = WidgetCode.Type
// object WidgetCode extends RefNewtype[String, constraints.Widget]

// type GizmoCode = GizmoCode.Type
// object GizmoCode extends RefNewtype[String, constraints.Gizmo]

enum ProductCode:
// starts with W and then 4 digits
  case WidgetCode(value: constraints.Widget)
// starts with G and then 3 digits
  case GizmoCode(value: constraints.Gizmo)

object ProductCode:

  def from(s: String): Either[String, ProductCode] =
    if (s.startsWith("W"))
      refineV[constraints.WidgetR](s)
        .map(WidgetCode.apply)
      // ProductCode.WidgetCode()
    else if (s.startsWith("G"))
      refineV[constraints.GizmoCodeR](s)
        .map(GizmoCode.apply)
    else
      Left(s"Unexpected input of $s found.")

// between 1 and 1000
type UnitQuantity = UnitQuantity.Type
object UnitQuantity extends RefNewtype[Int, constraints.UnitQuantity]

// between 0.5 and 100.0
type KilogramQuantity = KilogramQuantity.Type
object KilogramQuantity extends RefNewtype[Double, constraints.KilogramQuantity]

// non empty and < 50 chars
type OrderId = OrderId.Type
object OrderId extends RefNewtype[String, constraints.String50]

// non empty and < 50 chars
type OrderLineId = OrderLineId.Type
object OrderLineId extends RefNewtype[String, constraints.String50]

// must be exactly 5 digits
type ZipCode = ZipCode.Type
object ZipCode extends RefNewtype[String, constraints.Zipcode]

// between 0 and 1000
type Price = Price.Type
object Price extends RefNewtype[Double, constraints.Price]

type Uri = Uri.Type
object Uri extends RefNewtype[String, constraints.Uri]

// between 0 and 10,000
type BillingAmount = BillingAmount.Type
object BillingAmount extends RefNewtype[Double, constraints.BillingAmount]

type OrderQuantity = UnitQuantity | KilogramQuantity

object OrderQuantity {

  def from(
    fieldName: String,
    prodCode: ProductCode,
    quantity: Double,
  ): Either[String, OrderQuantity] =
    prodCode match {
      case ProductCode.WidgetCode(value) => ???
      case ProductCode.GizmoCode(value)  => ???
    }

}

// type ProductCode = WidgetCode | GizmoCode

// object ProductCode {

//   def from(s: String): Either[String, ProductCode] =
//     if (s.startsWith("W"))
//       WidgetCode.from(s)
//     else if (s.startsWith("G"))
//       GizmoCode.from(s)
//     else
//       Left(s"Unexpected input of $s found.")

// }

object constraints {
  type String50 = NonEmptyFiniteString[50]
  type WidgetR = string.MatchesRegex["W[0-9]{4}"]
  type GizmoCodeR = string.MatchesRegex["G[0-9]{3}"]
  type Widget = String Refined WidgetR
  type Gizmo = String Refined GizmoCodeR
  type Email = String Refined string.MatchesRegex[".*@.*\\.com"]
  type Zipcode = String Refined string.MatchesRegex["[0-9]{5}"]
  type UnitQuantity = Int Refined Interval.Open[1, 1000]
  type KilogramQuantity = Double Refined Interval.Open[0.5, 100.0]
  type Price = Double Refined Interval.Closed[0.0, 1000.0]
  type BillingAmount = Double Refined Interval.Closed[0.0, 1000.0]
  type Uri = String Refined string.Uri

}
