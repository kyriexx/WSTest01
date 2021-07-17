package cn.wondershare.scalatest

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author kyrie
 * @create 2021-07-06 10:34
 */
object SparkWordCount {

  def main(args: Array[String]): Unit = {

    // 由于往HDFS中的写入数据存在权限问题，所以在代码中设置用户为HDFS目录的所属用户
    //System.setProperty("HADOOP_USER_NAME", "root")

    val sparkConf: SparkConf = new SparkConf()
      .setAppName("SparkWordCount")
      .setMaster("local[*]") //设置为local模式运行

    //创建SparkContext，使用SparkContext来创建RDD
    val sc: SparkContext = new SparkContext(sparkConf)

    val lines: RDD[String] = sc.textFile("word/word.txt")

    //Transformation开始
    //切分压平
    val words: RDD[String] = lines.flatMap(_.split(" "))

    //将单词和1组合放在元组里
    val wordAndOne: RDD[(String, Int)] = words.map((_, 1))

    //分组聚合，reduceByKey可以先局部聚合再全局聚合
    val reduced: RDD[(String, Int)] = wordAndOne.reduceByKey(_ + _)

    //排序
    val sorted: RDD[(String, Int)] = reduced.sortBy(_._2, false)
    //Transformation结束

    //调用Action将计算结果保存到HDFS中
    //sorted.saveAsTextFile("out1")
    val result: Array[(String, Int)] = sorted.collect()

    println(result.mkString("--"))

    //释放资源
    sc.stop()

  }

}
