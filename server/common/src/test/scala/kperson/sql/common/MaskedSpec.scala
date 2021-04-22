package kperson.sql.common

import kperson.sqlh.common._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should._


class MaskedSpec extends AnyFlatSpec with Matchers {

  "Masked" should "mask values" in {
    val secret = "my.secret.password"
    val masked = Masked(secret)
    masked.toString shouldBe "**********"
    masked.value shouldBe secret
  }

}