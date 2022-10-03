package com.trendyol.flink.models

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty


case class InputJson(
      customer_id: String,
      location: String,
      order_date: String,
      var ts: Long,
      order_id: String,
      price: Double,
      product_id: String,
      seller_id: String,
      status: String
) {
  def setTs(ts: Long): Unit = {
    this.ts = ts
  }
  def toJsonString(): String = {
    s"""{
       |"customer_id": ${this.customer_id},
       |"location": ${this.location},
       |"order_date": ${this.order_date},
       |"order_id": ${this.order_id}
       |"ts": ${this.ts}
       |"price": ${this.price}
       |"product_id": ${this.product_id}
       |"seller_id": ${this.seller_id}
       |"status": ${this.status}
       |}""".stripMargin
  }
}
