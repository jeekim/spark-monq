package org.europepmc.nlp

import java.io._
import java.util._
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConverters._


import edu.stanford.nlp.ling.CoreAnnotations.{TextAnnotation, LemmaAnnotation, SentencesAnnotation, TokensAnnotation, PartOfSpeechAnnotation}
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
// import org.europepmc.annotator.MonqAnnotator

object CoreNLP {

  def createNLPPipeline(): StanfordCoreNLP = {
    val props = new Properties()
    props.put("annotators", "tokenize, ssplit, pos")
    // props.put("annotators", "tokenize, ssplit, pos, lemma")
    new StanfordCoreNLP(props)
  }

  def plainTextToSentences(text: String, pipeline: StanfordCoreNLP): Seq[String] = {
    val doc = new Annotation(text)
    pipeline.annotate(doc)
    val outs = new ArrayBuffer[String]()
    val sentences = doc.get(classOf[SentencesAnnotation])

    // http://stanfordnlp.github.io/CoreNLP/api.html
    for (sentence <- sentences.asScala) {
      // outs += sentence.toString()
      for (token <- sentence.get(classOf[TokensAnnotation]).asScala)
         // outs += token.get(classOf[TextAnnotation])
         outs += token.get(classOf[PartOfSpeechAnnotation])
    }
    outs
  }

}
