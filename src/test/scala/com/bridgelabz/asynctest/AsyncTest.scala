package com.bridgelabz.asynctest

import com.typesafe.config.Config
import org.scalatest.flatspec.AsyncFlatSpec
import com.bridgelabz.async.Config._
import spray.json.JsValue

class AsyncTest extends AsyncFlatSpec{

  behavior of "sendCSVRequest"
  it should "eventually throw an error since passing a null object" in {

    val csvFuture = sendCSVRequest(null)
    csvFuture map {ret => assert(ret.getClass.equals(new Throwable().getClass))}
  }
}
