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
  type Relation
  type Event
  type Tagger = String => Mention
  type Filter = Mention => NamedEntity
  object Filter
}

// object Pipeline

trait CollectionReader

// Type class for Mention?
trait Filterable[Mention[_]]

trait Tagger {
  def transform(in: String): String
}

trait Filter {
  def transform(in: String): String
}

// trait Filter with ValidationRuleSet

trait Annotation {
  def exsits: Boolean
  def uri: Option[String]
  def get: NamedEntity
}

sealed trait NamedEntity {
  def id: String
  def name: String
}

// type Gene <: NamedEntity
case class Gene(id: String, name: String) extends NamedEntity
case class Species(id: String, name: String) extends NamedEntity
case class GO(id: String, name: String) extends NamedEntity
case class Disease(id: String, name: String) extends NamedEntity
case class Chemical(id: String, name: String) extends NamedEntity
case class AccNumber(id: String, name: String) extends NamedEntity


// import Pipeline._
// module

// trait SectionTagger[Article] extends Tagger {
trait SectionTagger extends Tagger {
  def transform(in: String): String = in
}

// object sectionTagger extends Tagger with SectionTagger
object sectionTagger extends SectionTagger

object RunTagger
object RunFilter
