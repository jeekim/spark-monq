package org.europepmc

import java.io._
import java.util._
import monq.jfa._
import monq.programs.DictFilter
import org.apache.spark.{SparkContext, SparkConf}
import com.cloudera.datascience.common.XmlInputFormat
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io._

class Annotator extends Serializable {
// class Annotator(val acc: String) extends Serializable {

  val stream = getClass.getResourceAsStream("/acc150612.mwt")
  val acc = scala.io.Source.fromInputStream(stream).getLines().toList.mkString("\n")
  // val acc = scala.io.Source.fromFile("acc150612.mwt").getLines().toList.mkString("\n")
  val reader = new StringReader(acc)
  val dict = new DictFilter(reader, "raw", "", false)
  val r = dict.createRun()

  def annotate(text: String) = {
  // def annotate(text: String)(implicit r: DfaRun) = {
    val baos = new ByteArrayOutputStream()

    // TODO use for and Try
    val fr = new StringReader(text)
    val rcs = new ReaderCharSource(fr)
    val writer = new PrintStream(baos)

    r.setIn(rcs)
    r.filter(writer)
  
    fr.close()
    writer.close()
    baos.toString("UTF8")
  }
}

object AnnotatorTest {
  def main(args: Array[String]) {
    val path = "oa201603/*.xml"
    val conf = new Configuration()
    val sc = new SparkContext()

    conf.set(XmlInputFormat.START_TAG_KEY, "<article ")
    conf.set(XmlInputFormat.END_TAG_KEY, "</article>")

    val kvs = sc.newAPIHadoopFile(path, classOf[XmlInputFormat], classOf[LongWritable], classOf[Text], conf)
    val rawXmls = kvs.flatMap(p => try { Some(scala.xml.XML.loadString(p._2.toString)) } catch { case e: Exception => None } )
    // val titles = rawXmls.map{ x => (x \\ "article-title").text }
    val ps = rawXmls.map{ x => (x \\ "p").text }

    // val annotations = titles.mapPartitions(it => { val acc = "<mwt><template><z:acc db='%1'>%0</z:acc></template>" + "<r p1='1'>of</r>" + "<r p1='2'>GenBank</r>" + "</mwt>"; val ann = new Annotator(acc); it.flatMap(e => try { Some(ann.annotate(e)) } catch { case e: Exception => None }) })
    // val annotations = titles.mapPartitions(it => { val ann = new Annotator(); it.flatMap(e => try { Some(ann.annotate(e)) } catch { case ex: Exception => None }) })
    val annotations = ps.mapPartitions(it => { val ann = new Annotator(); it.flatMap(e => try { Some(ann.annotate(e)) } catch { case ex: Exception => None }) })
    annotations.saveAsTextFile("xxxxxxxx")
  }
}


