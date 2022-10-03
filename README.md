# Default Documentation
A Flink application project using Scala and SBT.

To run and test your application locally, you can just execute `sbt run` then select the main class that contains the Flink job . 

You can also package the application into a fat jar with `sbt assembly`, then submit it as usual, with something like: 

```
flink run -c org.example.WordCount /path/to/your/project/my-app/target/scala-2.11/testme-assembly-0.1-SNAPSHOT.jar
```


You can also run your application from within IntelliJ:  select the classpath of the 'mainRunner' module in the run/debug configurations.
Simply open 'Run -> Edit configurations...' and then select 'mainRunner' from the "Use classpath of module" dropbox. 

# Sevki's Notes

This is an example Flink code for reading data from a Kafka topic, processing it, and re-writing it to another Kafka topic.

Run `docker-compose up` to run the Kafka services. In Intellij select the `mainRunner` folder for the `class path` variable.

The data source is needed to run this pipe, and it will process it and re-write into another topic.

The code is in a state that is almost-running. Please don't hesitate to reach out if there are any issues that you can't fix.
