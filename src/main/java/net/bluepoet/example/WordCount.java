package net.bluepoet.example;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Created by Mac on 2016. 2. 16..
 */
public class WordCount {
    public static void main(String[] args) {
        String inputFile = args[0];
        String outputFile = args[1];

        SparkConf conf = new SparkConf().setAppName("wordCount");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> input = sc.textFile(inputFile);
        JavaRDD<String> word = input.flatMap(
                new FlatMapFunction<String, String>() {
                    @Override
                    public Iterable<String> call(String s) throws Exception {
                        return Arrays.asList(s.split(" "));
                    }
                }
        );

        JavaPairRDD<String, Integer> counts = word.mapToPair(
                new PairFunction<String, String, Integer>() {
                    public Tuple2<String, Integer> call(String x) {
                        return new Tuple2(x, 1);
                    }
                }).reduceByKey(
                new Function2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer x, Integer y) throws Exception {
                        return x+y;
                    }
                }
        );

        counts.saveAsTextFile(outputFile);
    }
}
