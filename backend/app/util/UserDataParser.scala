package util

import domain.UserAction
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import play.api.Logger

class   UserDataParser {
  
  def getSessionid = {
    val data = loadTestFile
    val groupedData: Map[String, Seq[UserAction]] = data.groupBy(x => x.sessionId)
    groupedData.map(entry => (entry._1, entry._2.size))
  }
  
  def loadTestFile: Seq[UserAction] = {
    Logger.info("Start Parsing UserAction File")
    val source = scala.io.Source.fromFile("/home/nmahle/tracking/realtimelogging_useraction_tracking.log.2015-12-10")
    val lines: Seq[String] = try source.getLines().toList finally source.close()
    lines.map(parseRow).flatten
  }
  
  def getTestString: String =
    """2015-12-10 00:00:38,681	15	ZI111N03Z-Q11	-	73001265740	102902E0376220805ACB476F62751E50.jvm_itr-http66_p0120	VIEWRECO	-	-	-	cda045d9-9145-4b9f-8fb5-d4979fda1878	n.	-""".stripMargin.trim

  def parseRow(rowData: String):Option[UserAction] = {
    val rowDataSplitted: Array[String] = rowData.split("\t")
    
    val timeStamp = rowDataSplitted(0)
    val appDomnain = rowDataSplitted(1).toInt
    val configSku = rowDataSplitted(2)
    val simpleSku = rowDataSplitted(3) match {
      case "-" => None
      case x => Some(x)
    }
    val customerId = rowDataSplitted(4) match {
      case "UNKNOWN_CUSTOMER" => None
      case "-" => None
      case x => Some(x.toLong)
    }
    val sessionId = rowDataSplitted(5)
    val action = rowDataSplitted(6)
    val quantity = rowDataSplitted(7)  match {
      case "-" => None
      case x => Some(x.toInt)
    }
    val price = rowDataSplitted(8)  match {
      case "-" => None
      case x => Some(x.toInt)
    }
    val orderId = rowDataSplitted(9)  match {
      case "-" => None
      case x => Some(x.toLong)
    } 
    val flowId = rowDataSplitted(10)
    val referrerLink = rowDataSplitted(12)  match {
      case "-" => None
      case x => Some(x)
    }

    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss,SSS")
    if(appDomnain == 1) {
      Some(UserAction(DateTime.parse(timeStamp, formatter),
        appDomnain,
        configSku,
        simpleSku,
        customerId,
        sessionId,
        action,
        quantity,
        price,
        orderId,
        flowId,
        referrerLink))
    } else {
      None
    }
  }
}
