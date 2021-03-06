package org.europepmc

import java.io._
import java.util._

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io._

import com.cloudera.datascience.common.XmlInputFormat
import com.elsevier.spark_xml_utils.xslt.XSLTProcessor

import org.europepmc.annotator.MonqAnnotator
import org.europepmc.nlp.CoreNLP._

object RunAnnotator {
  def main(args: Array[String]) {
    val path = args(0)
    val out_path = args(1)
    val conf = new Configuration()
    val sc = new SparkContext()
    conf.set(XmlInputFormat.START_TAG_KEY, "<article ")
    conf.set(XmlInputFormat.END_TAG_KEY, "</article>")

    // xslt
    val stream = getClass.getResourceAsStream("/pmc150714.xsl")
    val xsl = scala.io.Source.fromInputStream(stream).getLines().toList.mkString("\n")
    val proc = XSLTProcessor.getInstance(xsl)

    // RDDs
    val kvRDD = sc.newAPIHadoopFile(path, classOf[XmlInputFormat], classOf[LongWritable], classOf[Text], conf)

    // TODO use for sequence for all the transformations.
    val rawXmlRDD = kvRDD.flatMap(p =>
		    try {
		      Some(scala.xml.XML.loadString(proc.transform(p._2.toString)))
        } catch {
		      case e: Exception => None
		    }
    )

    val pRDD = rawXmlRDD.map{ x => (x \\ "text").text }

    /* calling CoreNLP pipeline */
    val sentenceRDD = pRDD.mapPartitions(iter => {
      val pipeline = createNLPPipeline()
      iter.flatMap{ p => plainTextToSentences(p, pipeline) }
    })

    sentenceRDD.saveAsTextFile(out_path)
    // TODO get rid of try and catch, use Try?
    /*val annotationRDD = sentenceRDD.mapPartitions(it => {
       val ann = new MonqAnnotator()
       it.flatMap(e =>
		       try {
		         Some(ann.annotate(e))
		       } catch {
                         case ex: Exception => None
		       }
      )
     }
    )

    annotationRDD.saveAsTextFile(out_path)*/
  }
}
