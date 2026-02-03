package org.llm4s.util

/**
 * Shared utility for printing colored output to the CLI.
 * Useful for demos, REPLs, and tracing visualization.
 */
object ConsoleColors {
  // Standard ANSI Codes
  val RESET  = "\u001B[0m"
  val RED    = "\u001B[31m"
  val GREEN  = "\u001B[32m"
  val YELLOW = "\u001B[33m"
  val BLUE   = "\u001B[34m"
  val PURPLE = "\u001B[35m"
  val CYAN   = "\u001B[36m"
  val WHITE  = "\u001B[37m"

  val BOLD   = "\u001B[1m"

  // Helper methods for cleaner code
  def red(text: String): String = s"$RED$text$RESET"
  def green(text: String): String = s"$GREEN$text$RESET"
  def yellow(text: String): String = s"$YELLOW$text$RESET"
  def cyan(text: String): String = s"$CYAN$text$RESET"
  def bold(text: String): String = s"$BOLD$text$RESET"

  // Generic styler
  def style(text: String, color: String): String = s"$color$text$RESET"
}