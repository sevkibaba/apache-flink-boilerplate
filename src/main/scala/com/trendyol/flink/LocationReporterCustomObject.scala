package com.trendyol.flink

import java.util.Properties

import com.trendyol.flink.functions.{AggregateFn, ParseCustomObjFn}
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._


object LocationReporterCustomObject {

  def main(args: Array[String]) {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("zookeeper.connect", "localhost:2181")
    properties.setProperty("group.id", "sevkiGroup")
    properties.setProperty("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")

    val producer = new FlinkKafkaProducer[String]("location-report", new SimpleStringSchema(), properties)
    val consumer = new FlinkKafkaConsumer[String]("orders", new SimpleStringSchema(), properties)

    val stream = env
      .addSource(consumer)

    val parsedStream = stream
      .map(new ParseCustomObjFn())

    val withTimestampsAndWatermarks = parsedStream
      .assignAscendingTimestamps(_._2.ts)
      .keyBy(_._1)
      .process(new AggregateFn())
      .addSink(producer)

    env.execute("Flink Kafka")
  }
}
