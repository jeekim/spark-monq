package org
package europepmc
package pipeline

/* Status */
sealed trait Status
case object Raw extends Status
case object Tagged extends Status
case object Filtered extends Status

sealed trait Section
case object Title extends Section
case object Abstract extends Section
case object Introduction extends Section
case object Method extends Section
case object Result extends Section
case object Discussion extends Section

/* Pipeline */
trait Pipeline {
  // Data source: Patent, Wikipage, MEDLINE abstract, etc.
  type Collection[Article]
  type Source
  type Sink

  type Text[Status]
  type Section = Seq[Sentence]
  type Sentence
}

object Pipeline

trait PipelineService {
  // def compose: Tagger => Filter => Tagger
  type TaggingError
  // type Tagger[+A] = String => Either[TaggingError, A]
  // just like State?
  type Text
  type Tagger = Text => (Text, Unit)
  // map or flatMap

  def buildPipeline(taggers: Seq[Tagger]): Pipeline
  def runPipeline: Unit
}

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
// trait Tagger[A] {
trait Tagger {
  type Text
  type Dictionary
  def transform: Text => Text
  // def flatMap
}

trait TaggingService {
  type Text
  type TaggedText

  def buildTagger: Dictionary => Tagger // primitive? unit?
  def runTagger[A <: Text]: Tagger => Text => TaggedText // primitive? run?
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
}

trait NamedEntity {
  def id: String
  def name: String
}

object NamedEntity {
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
