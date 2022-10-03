package com.trendyol.flink.models


case class InputJsonPrimitive(
                      customer_id: String,
                      location: String,
                      order_date: String,
                      order_id: String,
                      price: Double,
                      product_id: String,
                      seller_id: String,
                      status: String
                    ) {
  def toJsonString(): String = {
    s"""{
       |"customer_id": "${this.customer_id}",
       |"location": "${this.location}",
       |"order_date": "${this.order_date}",
       |"order_id": "${this.order_id}",
       |"price": ${this.price},
       |"product_id": "${this.product_id}",
       |"seller_id": "${this.seller_id}",
       |"status": "${this.status}"
       |}""".stripMargin
  }
}

