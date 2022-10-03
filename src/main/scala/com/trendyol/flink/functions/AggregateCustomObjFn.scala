package com.trendyol.flink.functions

import java.text.SimpleDateFormat

import com.trendyol.flink.models.{InputJson, OutputJson}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector

import scala.collection.mutable


case class CountWithTimestamp(key: String, sellers: mutable.Set[String], products: Array[String], lastModified: Long)


class AggregateFn extends KeyedProcessFunction[String, (String, InputJson), String] {

  lazy val state: ValueState[CountWithTimestamp] = getRuntimeContext
    .getState(new ValueStateDescriptor[CountWithTimestamp]("myState", classOf[CountWithTimestamp]))

  val windowMillis = 10000
  override def processElement(
                               value: (String, InputJson),
                               ctx: KeyedProcessFunction[String, (String, InputJson), (String)]#Context,
                               out: Collector[String]): Unit = {

    val roundPrecision = this.roundTo5min(ctx.timestamp())
    val current: CountWithTimestamp = state.value match {
      case null =>
        CountWithTimestamp(value._1, mutable.Set(value._2.seller_id), Array(value._2.product_id), roundPrecision)
      case CountWithTimestamp(key, seller, products, lastModified) =>
        CountWithTimestamp(key, seller += value._2.seller_id, products :+ value._2.product_id, lastModified)
    }

    state.update(current)
    ctx.timerService().registerEventTimeTimer(current.lastModified + this.windowMillis)
  }

  def roundTo5min(millisToRound: Long): Long = {
    val remain = millisToRound % this.windowMillis
    millisToRound - remain
  }

  override def onTimer(
                        timestamp: Long,
                        ctx: KeyedProcessFunction[String, (String, InputJson), String]#OnTimerContext,
                        out: Collector[String]): Unit = {
    state.value match {
      case CountWithTimestamp(key, sellers, products, lastModified) if (timestamp == lastModified + this.windowMillis) =>
        val precisionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp)
        val result = OutputJson(
          location = key, seller_count = sellers.size,
          product_count = products.length, date = precisionDate
        )
        val resStr = result.toJsonString()
        out.collect(resStr)
      case _ =>
    }
    val reset = CountWithTimestamp(ctx.getCurrentKey(), mutable.Set(), Array(), timestamp)
    state.update(reset)
  }
}