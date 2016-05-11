package org.europepmc

import java.io._
import java.util._
import monq.jfa._
import monq.programs.DictFilter

class Annotator extends Serializable {
  val reader = new StringReader("<mwt><template>[%0](%1)</template>" +"<t p1='333'>of</t></mwt>")
  val dict = new DictFilter(reader, "raw", "", false)
  val r = dict.createRun()

  def annotate(text: String) = {
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

object Annotator extends Serializable {
  def apply(text: String) = new Annotator().annotate(text)
}
