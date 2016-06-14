package org.europepmc.pipeline

/* Pipeline */
trait Pipeline {
  // ADT
  sealed trait Status
  case object Raw extends Status
  case object Tagged extends Status
  case object Filtered extends Status

  type Text[Status]

  // Data source
  // Patent, Wikipage, MEDLINE abstract, etc.
  type Collection[Article]

  type Section = Seq[Sentence]
  type Title <: Section
  type Abstract <: Section
  type Introduction <: Section
  type Method <: Section
  type Result <: Section
  type Discussion <: Section
  type Sentence
}

trait PipelineService {
  def compose: Tagger => Filter => Tagger
  def buildPipeline: Pipeline
}

object Pipeline

/* Article */
case class Article (xml: String) {
  type Section = String
  type Sentence = String

  def split: Seq[Section] = ???
}

trait TaggedArticle
trait ArticleRepository

/* Dictionary */
trait Dictionary
trait DictionaryRepository

trait DictionaryService {
  def generate: String => Dictionary
}

/* Tagger */
trait Tagger {
  type Text
  type Dictionary
  def transform: Text => Text
}

trait TaggingService {
  type Text
  type TaggedText

  def buildTagger: Dictionary => Tagger
  def runTagger[A <: Text]: Tagger => Text => TaggedText
}

// trait SectionTagger[Article] extends Tagger {
trait SectionTagger extends Tagger {
  // def transform(in: String): String = in
  type Text = String
  def transform = s => "<" + s + ">"
  def run: Tagger = ???
}

object testTaggingService extends TaggingService {
  type Text = String
  type TaggedText = String
  def buildTagger = ???
  def runTagger[Article] = ???
}

/* Filter */
trait Blacklist

trait Filter {
  type Text[A]
  type Mention
  def transform: Text[Mention] => Text[Option[Mention]]
}

trait Filterable[Mention[_]]

trait FilteringService {
  def buildFilter: Blacklist => Filter
}

/* Annotation */
trait Annotation {
  type AnnotatedSentence
  type Mention
  type Relation
  type Event

  def exsits: Boolean
  def uri: Option[String]
  def get: NamedEntity

  sealed trait NamedEntity {
    def id: String
    def name: String
  }

  case class Gene(id: String, name: String) extends NamedEntity
  case class Species(id: String, name: String) extends NamedEntity
  case class GO(id: String, name: String) extends NamedEntity
  case class Disease(id: String, name: String) extends NamedEntity
  case class Chemical(id: String, name: String) extends NamedEntity
  case class AccNumber(id: String, name: String) extends NamedEntity
}

trait AnnotationRepository
trait AnnotationService


// object sectionTagger extends Tagger with SectionTagger
object sectionTagger extends SectionTagger
object RunTagger
object RunFilter

/* Others */
trait TextAnalyticsService
trait ReportGenerationService
trait RdfGenerationService
trait CollectionReader


package application {

  object App {
    import org.europepmc.pipeline.sectionTagger._

    def main(args: Array[String]) {
      println(transform("Hello, world!"))
    }
  }
}
