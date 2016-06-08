package org.europepmc.pipeline

trait Pipeline {

  sealed trait Status
  case object Raw extends Status
  case object Tagged extends Status
  case object Filtered extends Status

  type Text[Status]

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
  type Tagger
  type Filter
  // object Filter

  def compose: Tagger => Tagger => Tagger
}

object Pipeline {
  def buildTagger: Tagger = ???
  def buildFilter: Filter = ???
  def buildPipeline: Pipeline = ???
}

trait CollectionReader

// Type class for Mention?
trait Filterable[Mention[_]]

trait Tagger {
  type Text
  def transform: Text => Text
}

trait Filter {
  type Text[A]
  type Mention
  def transform: Text[Mention] => Text[Option[Mention]]
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

// trait SectionTagger[Article] extends Tagger {
trait SectionTagger extends Tagger {
  // def transform(in: String): String = in
  type Text = String
  def transform = s => s + "tagged"
}

// object sectionTagger extends Tagger with SectionTagger
object sectionTagger extends SectionTagger
object RunTagger
object RunFilter

package application {

  object App {
    import org.europepmc.pipeline.sectionTagger._

    def main(args: Array[String]) {
      println(transform("Hello, world!"))
    }
  }
}
