package org.europepmc.pipeline

trait Pipeline {
  type Article
  type Section
  type Sentence[Mention]

  type Mention
  /* type Entity

  type Gene <: Entity
  type Species
  type Chemical
  type Disease
  type GO */

  // type Tagger
  // type Filter

  type Tagger = String => Mention
  type Filter = Mention => NamedEntity
}

trait Tagger {
  def transform(in: String): String
}

trait Annotation {
  def exsits: Boolean
  def get: NamedEntity
}

sealed trait NamedEntity
case class Gene(id: String, name: String) extends NamedEntity
case class Species(id: String, name: String) extends NamedEntity
case class GO(id: String, name: String) extends NamedEntity
case class Disease(id: String, name: String) extends NamedEntity
case class Chemical(id: String, name: String) extends NamedEntity
case class AccNumber(id: String, name: String) extends NamedEntity

object RunTagger
object RunFilter
