package widgetsinc.ordertaking.common

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

// must be non empty and contain @
type EmailAddress = EmailAddress.Type
object EmailAddress extends RefNewtype[String, constraints.Email]

// starts with W and then 4 digits
type WidgetCode = WidgetCode.Type
object WidgetCode extends RefNewtype[String, constraints.Widget]

// starts with G and then 3 digits
type GizmoCode = GizmoCode.Type
object GizmoCode extends RefNewtype[String, constraints.Gizmo]

// between 1 and 1000
type UnitQuantity = UnitQuantity.Type
object UnitQuantity extends RefNewtype[Int, constraints.UnitQuantity]

// between 0.5 and 100.0
type KilogramQuantity = KilogramQuantity.Type
object KilogramQuantity extends RefNewtype[Double, constraints.KilogramQuantity]

// non empty and < 50 chars
type OrderId = OrderId.Type
object OrderId extends RefNewtype[String, NonEmptyFiniteString[50]]

// non empty and < 50 chars
type OrderLineId = OrderLineId.Type
object OrderLineId extends RefNewtype[String, NonEmptyFiniteString[50]]

// must be exactly 5 digits
type ZipCode = ZipCode.Type
object ZipCode extends RefNewtype[String, constraints.Zipcode]

// between 0 and 1000
type Price = Price.Type
object Price extends RefNewtype[Double, constraints.Price]

// between 0 and 10,000
type BillingAmount = BillingAmount.Type
object BillingAmount extends RefNewtype[Double, constraints.BillingAmount]

type OrderType = UnitQuantity | KilogramQuantity

type ProductCode = WidgetCode | GizmoCode

object constraints {
  type Widget = String Refined string.MatchesRegex["W[0-9]{4}"]
  type Gizmo = String Refined string.MatchesRegex["G[0-9]{3}"]
  type Email = String Refined And[collection.NonEmpty, collection.Contains["@"]]
  type Zipcode = String Refined string.MatchesRegex["[0-9]{5}"]
  type UnitQuantity = Int Refined Interval.Open[1, 1000]
  type KilogramQuantity = Double Refined Interval.Open[0.5, 100.0]
  type Price = Double Refined Interval.Closed[0.0, 1000.0]
  type BillingAmount = Double Refined Interval.Closed[0.0, 1000.0]
}
