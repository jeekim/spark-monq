package org.europepmc.filter;

// TODO use pattern matching to generate queries depending on db types.

case class QueryGenerator(q: String) {

  override def toString() = s"query: $q"

}
