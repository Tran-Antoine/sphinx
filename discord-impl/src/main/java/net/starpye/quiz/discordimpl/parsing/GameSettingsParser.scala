package net.starpye.quiz.discordimpl.parsing

import java.util.concurrent.TimeUnit

import net.starype.quiz.api.game.question.Question
import net.starype.quiz.api.game.{ClassicalRound, GameRound, IndividualRound, PollRound, TimedRaceRound}

import scala.util.parsing.combinator.RegexParsers

class GameSettingsParser extends RegexParsers {

  def settings: Parser[GameSettings] = CMD ~> " " ~> repsep(quizArg, ", ") ^^ GameSettings

  def quizArg: Parser[Argument] =
    rounds | showAnswers | questionSet

  def rounds: Parser[Argument] =
    arg("rounds:",set(repsep(opt(NUMBER <~ "*") ~ set(round), ",")) ^^ {
      rawRounds => RoundsArgument(unpack(rawRounds))
    })

  def showAnswers: Parser[Argument] =
    arg("show-answers", BOOL ^^ ShowAnswer)

  def questionSet: Parser[Argument] =
    arg("question-set", set(arg("repertoire", LITERAL ^^ QuestionSet)))

  def round: Parser[Question => GameRound] = {
    ("name:" ~> LITERAL) ~ opt(", time:" ~> NUMBER) ~ opt(", tries:" ~> NUMBER) ^^ {
      case name ~ time ~ tries => name match {
        case "Classical" => new ClassicalRound(_, tries.get, 1)
        case "Individual" => new IndividualRound(_, 2)
        case "TimedRace" => new TimedRaceRound(_, tries.get, 1, time.get, TimeUnit.SECONDS)
        case "Poll" => new PollRound(_, tries.get)
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
  def LITERAL: Parser[String] = """[a-zA-Z]+"""


  case class GameSettings(args: List[Argument])
  case class Argument()
  case class RoundsArgument(seq: List[Question => GameRound]) extends Argument
  case class ShowAnswer(boolean: Boolean) extends Argument
  case class QuestionSet(repertoire: String) extends Argument
}

/*
  /create rounds:{5*{name: "Classical", time: 60, tries: 2}, }, question-set:{repertoire:"Linear Algebra"}
 */