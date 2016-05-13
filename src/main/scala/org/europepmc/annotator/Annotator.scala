package org.europepmc.annotator

import java.io._
import java.util._

trait Annotator {
  def annotate(text: String): String
}

class MonqAnnotator extends Serializable with Annotator {

  import monq.jfa._
  import monq.programs.DictFilter

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
