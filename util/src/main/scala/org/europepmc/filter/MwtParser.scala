package org.europepmc.filter;

import monq.jfa.Xml
import java.util.Map

// ADT product type
case class MwtAtts(tagname: String, content: String, db: String, valmethod: String, domain: String, context: String, wsize: String, sec: String)

class MwtParser(val map: java.util.Map[String, String]) {

  // <template><z:acc db="%1" valmethod="%2" domain="%3" context="%4" wsize="%5" sec="%6">%0</z:acc></template>
  def parse = {
    val tagname = map.get(Xml.TAGNAME);
    val content = map.get(Xml.CONTENT);

    val db = map.get("db");
    val valmethod = map.get("valmethod");
    val domain = map.get("domain");
    val context = map.get("context");
    val wsize = map.get("wsize");
    val sec = map.get("sec");

    MwtAtts(tagname, content, db, valmethod, domain, context, wsize, sec)
  }

  // partial function?
  def contextHandler(ma: MwtAtts): String => String = ???
  def domainHandler(ma: MwtAtts): String => String = ???
  // def allHandler(ma: MwtAtts): String => String = contextHandler _ andThen domainHandler _
  type Validator
  def generateValidators(ma: MwtAtts): Seq[Validator] = ???
  // use pattern matching with ma.
}
