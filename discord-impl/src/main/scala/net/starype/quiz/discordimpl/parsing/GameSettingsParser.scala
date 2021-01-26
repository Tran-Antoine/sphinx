package net.starype.quiz.discordimpl.parsing

import java.util.concurrent.TimeUnit

import net.starype.quiz.api.game.question.Question
import net.starype.quiz.api.game._

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

  def round: Parser[Question => GameRound] = {
    repsep(roundArg, ",") ^^ {
      case List(name: String, time: Option[Int], tries: Option[Int]) => name match {
        case "Classical" => new ClassicalRound(_, tries.get, 1)
        case "Individual" => new IndividualRound(_, 2)
        case "TimedRace" => new TimedRaceRound(_, tries.get, 1, time.get, TimeUnit.SECONDS)
        case "Poll" => new PollRound(_, tries.get)
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

  trait RoundArgument
  case class NameArgument(name: String) extends RoundArgument
  case class TimeArgument(time: Int) extends RoundArgument
  case class TriesCountArgument(tries: Int) extends RoundArgument
}
