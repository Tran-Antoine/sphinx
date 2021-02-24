package net.starype.quiz.discordimpl.parsing

import net.starype.quiz.api.game._
import net.starype.quiz.api.question.Question
import java.util.concurrent.TimeUnit

import net.starype.quiz.api.round.{ClassicalRoundFactory, IndividualRoundFactory, PollRoundFactory, QuizRound, TimedRaceRoundFactory}

import scala.util.parsing.combinator.RegexParsers

class GameSettingsParser extends RegexParsers {

  def settings: Parser[GameSettings] = CMD ~> repsep(quizArg, ",") ^^ {new GameSettings(_)}

  def quizArg: Parser[Argument] =
    rounds | showAnswers | questionSet

  def rounds: Parser[Argument] =
    arg("rounds", set(repsep(opt(NUMBER <~ "*") ~ set(round), ", ")) ^^ {
      rawRounds => RoundsArgument(unpack(rawRounds))
    })

  def showAnswers: Parser[Argument] =
    arg("show-answers", BOOL ^^ ShowAnswer)

  def questionSet: Parser[Argument] =
    arg("question-set", set(arg("repertoire", LITERAL ^^ QuestionSet)))

  def round: Parser[Question => QuizRound] = {
    repsep(roundArg, ",") ^^ {
      case List(name: String, time: Option[Int], tries: Option[Int]) => name match {
        case "Classical" => new ClassicalRoundFactory().create(_, tries.get, 1)
        case "Individual" => new IndividualRoundFactory().create(_, 2)
        case "TimedRace" => new TimedRaceRoundFactory().create(_, tries.get, 1, time.get, TimeUnit.SECONDS)
        case "Poll" => new PollRoundFactory.create(_, tries.get)
      }
    }
  }
  //arg("name", LITERAL) ~ opt("," ~> arg("time", NUMBER)) ~ opt("," ~> arg("tries", NUMBER))

  def roundArg: Parser[Any] = {
    arg("name", LITERAL) |
      arg("time", NUMBER) |
      arg("tries", NUMBER)
  }

  def arg[T](name: String, parser: Parser[T]): Parser[T] =
    (name ~ ":") ~> parser

  def set[T](parser: Parser[T]): Parser[T] =
    "{" ~> parser <~ "}"

  def unpack(list: List[Option[Int] ~ (Question => QuizRound)]): List[Question => QuizRound] = {
    list.flatMap(elem => List.fill(elem._1.getOrElse(1))(elem._2))
  }

  def CMD: Parser[String] = "/create "
  def BOOL: Parser[Boolean] = """[a-zA-Z]+""".r ^^ {_.toBoolean}
  def NUMBER: Parser[Int] = """[0-9]+""".r ^^ {_.toInt}
  def LITERAL: Parser[String] = """[a-zA-Z]+""".r ^^ {_.toString}


  class GameSettings(args: List[Argument]) {
  }

  trait Argument
  case class RoundsArgument(seq: List[Question => QuizRound]) extends Argument
  case class ShowAnswer(boolean: Boolean) extends Argument
  case class QuestionSet(repertoire: String) extends Argument

  trait RoundArgument
  case class NameArgument(name: String) extends RoundArgument
  case class TimeArgument(time: Int) extends RoundArgument
  case class TriesCountArgument(tries: Int) extends RoundArgument
}
