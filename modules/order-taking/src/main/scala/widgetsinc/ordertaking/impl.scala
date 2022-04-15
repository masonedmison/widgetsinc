package widgetsinc.ordertaking

import common.compound.*
import common.simple.*
import widgetsinc.core.Newtype
import PublicTypes.*
import widgetsinc.ordertaking.PublicTypes.UnvalidatedAddress
import cats.data.EitherT
import widgetsinc.ordertaking.PublicTypes.UnvalidatedOrder
import cats.Applicative
import cats.implicits.*
import widgetsinc.core.RefNewtype
import cats.Functor

type CheckProductCodeExists = ProductCode => Boolean

enum AddressValidationError extends Exception:
  case InvalidFormat(msg: String)
  case AddressNotFound(msg: String)

case class CheckedAddress(value: UnvalidatedAddress)

type CheckAddressExists[F[_]] =
  UnvalidatedAddress => EitherT[F, AddressValidationError, CheckedAddress]

case class ValidatedOrderLine(
  orderLineId: OrderLineId,
  productCode: ProductCode,
  quantity: OrderQuantity,
)

case class ValidatedOrder(
  orderId: OrderId,
  customerInfo: CustomerInfo,
  shippingAddress: Address,
  billingAddress: Address,
  lines: List[ValidatedOrderLine],
)

type ValidateOrder[F[_]] =
  CheckProductCodeExists => CheckAddressExists[F] => UnvalidatedOrder => EitherT[
    F,
    PlaceOrderErr.ValidationErr,
    ValidatedOrder,
  ]

type GetProductPrice = ProductCode => Price

type PriceOrder[F[_]] =
  GetProductPrice => ValidateOrder[F] => EitherT[F, PlaceOrderErr.PricingErr, PricedOrder]

case class HtmlString(value: String)

case class OrderAck(emailAddr: EmailAddress, letter: HtmlString)

type CreateOrderAckLetter = PricedOrder => HtmlString

enum SendResult:
  case Sent
  case NotSent

type SendOrderAck = OrderAck => SendResult

type AckOrder[F[_]] =
  CreateOrderAckLetter => SendOrderAck => PricedOrder => Option[OrderAcknowledgementSent]

type CreateEvents[F[_]] = PricedOrder => Option[OrderAcknowledgementSent] => List[PlaceOrderEvent]

// validate order step - unvalidatedCustInfo to customInfo

private def emptyStringOpt(s: String): Option[String] = s.isEmpty.guard[Option].as(s)

def toCustomerInfo[F[_]: Applicative](
  unvalCustInfo: UnvalidatedCustomerInfo
): EitherT[F, PlaceOrderErr, CustomerInfo] =
  (
    for {
      first <- String50.from(unvalCustInfo.firstName)
      last <- String50.from(unvalCustInfo.lastName)
      email <- EmailAddress.from(unvalCustInfo.emailAddress)
    } yield CustomerInfo(PersonalName(first, last), email)
  )
    .leftMap(new PlaceOrderErr.ValidationErr(_))
    .toEitherT[F]

def toAddress[F[_]: Applicative](
  ca: CheckedAddress
): EitherT[F, PlaceOrderErr, Address] =
  val s50fromOpt = String50.fromOption compose emptyStringOpt
  (
    for {
      line1 <- String50.from(ca.value.line1)
      line2 <- s50fromOpt(ca.value.line2)
      line3 <- s50fromOpt(ca.value.line3)
      line4 <- s50fromOpt(ca.value.line4)
      city <- String50.from(ca.value.city)
      zipcode <- ZipCode.from(ca.value.zipCode)
    } yield Address(
      line1,
      line2,
      line3,
      line4,
      city,
      zipcode,
    )
  )
    .leftMap(new PlaceOrderErr.ValidationErr(_))
    .toEitherT[F]

def toCheckedAddress[F[_]: Functor](
  checkAddrExists: CheckAddressExists[F]
)(
  addr: UnvalidatedAddress
): EitherT[F, PlaceOrderErr, CheckedAddress] = checkAddrExists(addr)
  .leftMap {
    case AddressValidationError.InvalidFormat(msg)   => new PlaceOrderErr.ValidationErr(msg)
    case AddressValidationError.AddressNotFound(msg) => new PlaceOrderErr.ValidationErr(msg)
    // case _ => new PlaceOrderErr.ValidationErr("Unknown validation err.")
  }

def toOrderId(orderId: String) = OrderId
  .from(orderId)
  .leftMap(PlaceOrderErr.ValidationErr.apply)

def toOrderIdLineId(orderLineId: String) = OrderLineId
  .from(orderLineId)
  .leftMap(PlaceOrderErr.ValidationErr.apply)

def toProductCode(checkProdExists: CheckProductCodeExists)(prodCode: String) = ProductCode
  .from(prodCode)
  .leftMap(PlaceOrderErr.ValidationErr.apply)
  .flatMap { p =>
    if (checkProdExists(p))
      Right(p)
    else
      new PlaceOrderErr.ValidationErr(s"Invalid product code: $p.").asLeft[ProductCode]
  }

// def toOrderQuantity(prodCode: ProductCode)(quantity: Int) =
//   OrderQuantity.from()
