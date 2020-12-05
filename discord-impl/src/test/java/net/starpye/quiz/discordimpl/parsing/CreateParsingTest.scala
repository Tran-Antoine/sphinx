package net.starpye.quiz.discordimpl.parsing

object CreateParsingTest extends GameSettingsParser {

  def main(args: Array[String]): Unit = {
    println(parse(settings, "/create ") match {
      case Success(matched, _) => matched
      case Failure(msg, _) => "Fail " + msg
      case Error(msg, _) => "Error " + msg
    })
  }
}
