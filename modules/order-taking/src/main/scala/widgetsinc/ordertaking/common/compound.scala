package widgetsinc.ordertaking.common
package compound

import simple.*

import eu.timepit.refined.types.string.NonEmptyFiniteString

case class PersonalName(firstName: String50, lastName: String50)

case class CustomerInfo(name: PersonalName, email: EmailAddress)

case class Address(
  line1: String50,
  line2: Option[String50],
  line3: Option[String50],
  line4: Option[String50],
  city: String50,
  zipCode: ZipCode,
)
