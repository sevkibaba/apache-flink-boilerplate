package com.trendyol.flink.models

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty


case class OutputJson (
  @JsonProperty("seller_count") seller_count: Int,
  @JsonProperty("product_count") product_count: Int,
  @JsonProperty("location") location: String,
  @JsonProperty("date") date: String
) {
  def toJsonString(): String = {
    s"""{
       |"seller_count": "${this.seller_count}",
       |"product_count": "${this.product_count}",
       |"location": "${this.location}",
       |"date": "${this.date}"
       |}""".stripMargin
  }
}
