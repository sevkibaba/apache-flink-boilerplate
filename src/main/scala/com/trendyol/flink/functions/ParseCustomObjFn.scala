package com.trendyol.flink.functions

import java.sql.Timestamp
import java.text.SimpleDateFormat

import com.trendyol.flink.models.InputJson
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper


class ParseCustomObjFn extends RichMapFunction[String, (String, InputJson)] {
  def map(in: String):(String, InputJson) = {
    val mapper = new ObjectMapper
    val res = mapper.readValue(in, classOf[InputJson])
    val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dateStringArr = res.order_date.split('.')
    val dateString = dateStringArr(0).replace("T", " ")
    res.setTs(formatter.parse(dateString).getTime)
    (res.location, res)
  }
}