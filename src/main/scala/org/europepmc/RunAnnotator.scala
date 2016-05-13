package org.europepmc

import java.io._
import java.util._

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io._

import com.cloudera.datascience.common.XmlInputFormat

import edu.stanford.nlp.ling.CoreAnnotations.{LemmaAnnotation, SentencesAnnotation, TokensAnnotation}
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import org.europepmc.annotator.MonqAnnotator
import org.europepmc.nlp.CoreNLP._

object RunAnnotator {
  def main(args: Array[String]) {
    // val path = "oa201603/PMC4736427_PMC4765918.xml"
    // val path = "oa201603/*.xml"
    val path = args(0)
    val out_path = args(1)
    val conf = new Configuration()
    val sc = new SparkContext()
    conf.set(XmlInputFormat.START_TAG_KEY, "<article ")
    conf.set(XmlInputFormat.END_TAG_KEY, "</article>")

    // RDDs
    val kvs = sc.newAPIHadoopFile(path, classOf[XmlInputFormat], classOf[LongWritable], classOf[Text], conf)
    val rawXmls = kvs.flatMap(p => try { Some(scala.xml.XML.loadString(p._2.toString)) } catch { case e: Exception => None } )
    // val titles = rawXmls.map{ x => (x \\ "article-title").text }
    val ps = rawXmls.map{ x => (x \\ "p").text }

    /* calling CoreNLP pipeline */
    val sentences = ps.mapPartitions(iter => {
      val pipeline = createNLPPipeline()
      iter.flatMap{ p => plainTextToSentences(p, pipeline) }
    })
    val annotations = sentences.mapPartitions(it => { val ann = new MonqAnnotator(); it.flatMap(e => try { Some(ann.annotate(e)) } catch { case ex: Exception => None }) })
    // annotations.saveAsTextFile("xxxxxxxx")
    annotations.saveAsTextFile(out_path)
  }

}


