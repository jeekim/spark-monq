package org.europepmc.pipeline

trait Pipeline {

  // Data source
  // Patent, Wikipage, MEDLINE abstract, etc.
  type Collection[Article]
  type Article[Section]
  // type Section[Sentence]
  // type Collection = Seq[Article]
  // type Article = Seq[Section]
  type Section = Seq[Sentence]

  type Title <: Section
  type Abstract <: Section
  type Introduction <: Section
  type Method <: Section
  type Result <: Section
  type Discussion <: Section

  type Sentence

  // Annotation
  type AnnotatedSentence
  type Mention
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

// type Gene <: NamedEntity
case class Gene(id: String, name: String) extends NamedEntity
case class Species(id: String, name: String) extends NamedEntity
case class GO(id: String, name: String) extends NamedEntity
case class Disease(id: String, name: String) extends NamedEntity
case class Chemical(id: String, name: String) extends NamedEntity
case class AccNumber(id: String, name: String) extends NamedEntity

object RunTagger
object RunFilter
