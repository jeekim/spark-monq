package org.europepmc.pipeline

trait Pipeline {

  type Tagger
  type Filter

  type Article
  type Section
  type T
  type Sentence[T]

  type Mention
  type Entity

  type Gene <: Entity
  type Species
  type Chemical
  type Disease
  type GO

  // type Tagging = Sentence[T] => Sentence[T]
  type Tagging = String => Annotation
}

trait Tagger {
  def transform(in: String): String
}

trait Annotation {
  def exsits: Boolean
}
