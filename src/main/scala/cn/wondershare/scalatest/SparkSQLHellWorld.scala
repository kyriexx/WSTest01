package cn.wondershare.scalatest

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * @author kyrie
 * @create 2021-07-10 15:52
 */
object SparkSQLHellWorld {

  def main(args: Array[String]): Unit = {

    // 提供隐式转换支持，如RDDs to DataFrame
    // 是用于将DataFrame隐式转换成RDD，使DataFrame能够使用RDD中的方法
    // import spark.implicits._

    //屏蔽掉WARN级别以下的日志（INFO,DEBUG)
    Logger.getLogger("org").setLevel(Level.WARN)

    // 创建一个sparksql的编程入口(包含sparkcontext，也包含sqlcontext)
    val spark: SparkSession = SparkSession.builder()
      .appName(this.getClass.getSimpleName)
      .master("local[*]")
      .getOrCreate()

    /*
    val sc: SparkContext = spark.sparkContext
    val sqlsc: SQLContext = spark.sqlContext
    */
    // 加载json数据文件为dataframe
    val df: DataFrame = spark.read.json("word/people.json")
    // 打印df中的schema元信息
    df.printSchema()

    // 打印df中的数据
    df.show(50, false)

    // 在df上，用调api方法的形式实现sql
    df.where("age > 30").show()

    // 将df注册成一个“表”（视图），然后写原汁原味的sql
    df.createTempView("people")

    spark.sql("select * from people where age > 30 order by age desc").show()

    spark.close()
  }

}
