package widgetsinc.ordertaking

import common.simple.*
import common.compound.*
import eu.timepit.refined.string.Uri
import cats.data.EitherT

object PublicTypes:

  final case class UnvalidatedCustomerInfo(
    firstName: String,
    lastName: String,
    emailAddress: String,
  )

  final case class UnvalidatedAddress(
    line1: String,
    line2: String,
    line3: String,
    line4: String,
    city: String,
    zipCode: String,
  )

  final case class UnvalidatedOrderLine(
    orderLineId: String,
    productCode: String,
    quantity: Double,
  )

  final case class UnvalidatedOrder(
    orderId: String,
    custInfo: UnvalidatedCustomerInfo,
    shippingAddr: UnvalidatedAddress,
    billingAddr: UnvalidatedAddress,
  )

  final case class OrderAcknowledgementSent(
    orderId: OrderId,
    emailAddr: EmailAddress,
  )

  final case class PricedOrderLine(
    orderLineId: OrderLineId,
    productCode: ProductCode,
    quantity: OrderQuantity,
    linePrice: Price,
  )

  final case class PricedOrder(
    orderId: OrderId,
    custInfo: CustomerInfo,
    shippingAddr: Address,
    billingAddr: Address,
    amountToBill: BillingAmount,
    lines: List[PricedOrderLine],
  )

  type OrderPlaced = PricedOrder

  final case class BillableOrderPlaced(
    orderId: OrderId,
    billingAddr: Address,
    amountToBill: BillingAmount,
  )

  type PlaceOrderEvent = OrderPlaced | BillableOrderPlaced | OrderAcknowledgementSent

  final case class ServiceInfo(name: String, endpoint: Uri)

  // final case
  enum PlaceOrderErr extends Exception:
    case PricingErr(msg: String)
    case RemoteServiceErr[E <: Throwable](service: ServiceInfo, exc: E)
    case ValidationErr(msg: String)

  type PlaceOrder[F[_]] = UnvalidatedOrder => EitherT[F, PlaceOrderErr, List[PlaceOrderEvent]]
