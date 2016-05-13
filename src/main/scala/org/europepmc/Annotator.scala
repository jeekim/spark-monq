package org.europepmc

import java.io._
import java.util._
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._

import monq.jfa._
import monq.programs.DictFilter

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io._

import com.cloudera.datascience.common.XmlInputFormat

import edu.stanford.nlp.ling.CoreAnnotations.{LemmaAnnotation, SentencesAnnotation, TokensAnnotation}
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}


class Annotator extends Serializable {

  val stream = getClass.getResourceAsStream("/acc150612.mwt")
  val acc = scala.io.Source.fromInputStream(stream).getLines().toList.mkString("\n")
  val reader = new StringReader(acc)
  val dict = new DictFilter(reader, "raw", "", false)
  val r = dict.createRun()

  def annotate(text: String): String = {
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
    val path = "oa201603/PMC4736427_PMC4765918.xml"
    // val path = "oa201603/*.xml"
    val conf = new Configuration()
    val sc = new SparkContext()
    conf.set(XmlInputFormat.START_TAG_KEY, "<article ")
    conf.set(XmlInputFormat.END_TAG_KEY, "</article>")

    // RDDs
    val kvs = sc.newAPIHadoopFile(path, classOf[XmlInputFormat], classOf[LongWritable], classOf[Text], conf)
    val rawXmls = kvs.flatMap(p => try { Some(scala.xml.XML.loadString(p._2.toString)) } catch { case e: Exception => None } )
    // val titles = rawXmls.map{ x => (x \\ "article-title").text }
    val ps = rawXmls.map{ x => (x \\ "p").text }
    val sentences = ps.mapPartitions(iter => {
      val pipeline = createNLPPipeline()
      iter.flatMap{ p => plainTextToSentences(p, pipeline) }
    })
    val annotations = sentences.mapPartitions(it => { val ann = new Annotator(); it.flatMap(e => try { Some(ann.annotate(e)) } catch { case ex: Exception => None }) })
    annotations.saveAsTextFile("xxxxxxxx")
  }

  def createNLPPipeline(): StanfordCoreNLP = {
    val props = new Properties()
    props.put("annotators", "tokenize, ssplit")
    // props.put("annotators", "tokenize, ssplit, pos, lemma")
    new StanfordCoreNLP(props)
  }

  def plainTextToSentences(text: String, pipeline: StanfordCoreNLP): Seq[String] = {
    val doc = new Annotation(text)
    pipeline.annotate(doc)
    val outs = new ArrayBuffer[String]()
    val sentences = doc.get(classOf[SentencesAnnotation])
    for (sentence <- sentences.asScala) {
      outs += sentence.toString()
    }
    outs 
}

}


