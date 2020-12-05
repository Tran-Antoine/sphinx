package net.starype.quiz.discordimpl.parsing

import java.util.concurrent.TimeUnit

import scala.util.parsing.combinator.RegexParsers

class GameSettingsParser extends RegexParsers {

  def settings: Parser[GameSettings] = CMD ~> repsep(quizArg, ", ") ^^ {new GameSettings(_)}

  def quizArg: Parser[Argument] =
    rounds | showAnswers | questionSet

  def rounds: Parser[Argument] =
    arg("roundsGen", set(repsep(opt(NUMBER <~ "*") ~ set(round), ", ")) ^^ {
      rawRounds => RoundsArgument(unpack(rawRounds))
    })

  def showAnswers: Parser[Argument] =
    arg("show-answers", BOOL ^^ ShowAnswer)

  def questionSet: Parser[Argument] =
    arg("question-set", set(arg("repertoire", LITERAL ^^ QuestionSet)))

  def round: Parser[Question => GameRound] = {
    arg("name", LITERAL) ~ opt(", time:" ~> NUMBER) ~ opt(", tries:" ~> NUMBER) ^^ {
      case name ~ time ~ tries => name match {
        case "Classical" => ClassicalRound(_, tries.get, 1)
        case "Individual" => IndividualRound(_, 2)
        case "TimedRace" => TimedRaceRound(_, tries.get, 1, time.get, TimeUnit.SECONDS)
        case "Poll" => PollRound(_, tries.get)
      }
    }
  }

  def arg[T](name: String, parser: Parser[T]): Parser[T] =
    (name ~ ":") ~> parser

  def set[T](parser: Parser[T]): Parser[T] =
    "{" ~> parser <~ "}"

  def unpack(list: List[Option[Int] ~ (Question => GameRound)]): List[Question => GameRound] = {
    list.flatMap(elem => List.fill(elem._1.getOrElse(1))(elem._2))
  }

  def CMD: Parser[String] = "/create "
  def BOOL: Parser[Boolean] = """[a-zA-Z]+""".r ^^ {_.toBoolean}
  def NUMBER: Parser[Int] = """[0-9]+""".r ^^ {_.toInt}
  def LITERAL: Parser[String] = """[a-zA-Z]+""".r ^^ {_.toString}


  class GameSettings(args: List[Argument]) {
  }

  trait Argument
  case class RoundsArgument(seq: List[Question => GameRound]) extends Argument
  case class ShowAnswer(boolean: Boolean) extends Argument
  case class QuestionSet(repertoire: String) extends Argument


  trait Question
  trait GameRound
  trait QuizGame
  case class ClassicalRound(question: Question, tries: Int, score: Int) extends GameRound
  case class IndividualRound(question: Question, score: Int) extends GameRound
  case class TimedRaceRound(question: Question, tries: Int, score: Int, time: Int, unit: TimeUnit) extends GameRound
  case class PollRound(question: Question, tries: Int) extends GameRound
}
