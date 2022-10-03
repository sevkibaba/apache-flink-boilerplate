import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.trendyol.flink.functions.AggregateFn
import com.trendyol.flink.models.{InputJson, InputJsonPrimitive}
import org.apache.flink.api.common.typeinfo.Types
import java.lang.String

import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.java.tuple.Tuple
import org.apache.flink.streaming.api.operators.{KeyedProcessOperator, OneInputStreamOperatorFactory, StreamFlatMap}
import org.apache.flink.streaming.util.{KeyedOneInputStreamOperatorTestHarness, KeyedTwoInputStreamOperatorTestHarness, ProcessFunctionTestHarnesses, ProcessFunctionTestHarnessesTest}
import org.json4s.native.Json
import org.scalatest.BeforeAndAfter


class KeyedProcessTest extends AnyFlatSpec with Matchers with BeforeAndAfter {
  val input1 = InputJsonPrimitive(
    customer_id = "7db3189b4bdc9acbf79b73544122d740",
    location = "İstanbul",
    order_date = "2021-01-01T00:00:02.000+03:00",
    order_id = "da789b5f48713d13740ad80de8822210",
    price = 35.99,
    product_id = "5aa2b3255e3aeef7ab35d6546eda2c72",
    seller_id = "a532400ed62e772b9dc0b86f46e583ff",
    status = "Created"
  ).toJsonString()
  val input2 = InputJsonPrimitive(
    customer_id = "e3a8313bebfe70d0f5d5935853051447",
    location = "İstanbul",
    order_date = "2021-01-01T00:01:25.000+03:00",
    order_id = "b036de824ef00be990b81c4ec6ebee23",
    price = 39.5,
    product_id = "50c1ab6e3ceb11814356b7f45e00a75c",
    seller_id = "4c200035cc0dd8b911725f8222ef58cf",
    status = "Created"
  ).toJsonString()
  private var harness: KeyedOneInputStreamOperatorTestHarness[String, ((String, Long), String), String] = null

  before {
    val processFunction = new AggregatePrimitiveFn
    val keySelector = new KeySelector[((String, Long), String), String] {
      @throws(classOf[Exception])
      override def getKey(value: ((String, Long), String)): String = value._1._1
    }
    harness = new KeyedOneInputStreamOperatorTestHarness[String, ((String, Long), String), String](new KeyedProcessOperator(processFunction), keySelector, Types.STRING)
    harness.getExecutionConfig().setAutoWatermarkInterval(10000);
    harness.open();
  }

  "Aggregate function" should "group data by key with timewindow" in {
    harness.processElement((("İstanbul", 1609448400000L), input1), 1609448400000L)
    harness.processElement((("İstanbul", 1609448410000L), input2), 1609448410000L)
    harness.processWatermark(1609448410000L)
    harness.processElement((("İstanbul", 1609448420000L), input2), 1609448420000L)
    harness.processWatermark(1609448420000L)
    harness.extractOutputValues().get(0) should equal("{\n\"seller_count\": \"2\",\n\"product_count\": \"2\",\n\"location\": \"İstanbul\",\n\"date\": \"2021-01-01 00:00:10\"\n}")
    harness.extractOutputValues().get(1) should equal("{\n\"seller_count\": \"1\",\n\"product_count\": \"1\",\n\"location\": \"İstanbul\",\n\"date\": \"2021-01-01 00:00:20\"\n}")
  }
}
