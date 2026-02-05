package org.llm4s.samples.toolapi

import org.llm4s.toolapi._
import org.llm4s.trace.AnsiColors._

object ImprovedErrorMessageDemo extends App {

  println(separator(length = 70))
  println(s"   ${BOLD}IMPROVED ERROR MESSAGE DEMONSTRATION${RESET}   ")
  println(separator(length = 70))

  // Helper to print scenarios cleanly
  def printScenario(id: Int, title: String, error: ToolCallError): Unit = {
    println(s"\n${YELLOW}$id. Scenario:${RESET} ${BOLD}$title${RESET}")
    println(s"   Result: ${colorize(error.getFormattedMessage, RED)}")
  }

  // 1. Unknown tool
  val unknownTool = ToolCallError.UnknownFunction("calculate_tax")
  printScenario(1, "Unknown tool", unknownTool)

  // 2. Null arguments
  val nullArgs = ToolCallError.NullArguments("add_inventory_item")
  printScenario(2, "Null arguments", nullArgs)

  // 3. Missing required parameter
  val missingParam = ToolCallError.InvalidArguments(
    "add_inventory_item",
    List(ToolParameterError.MissingParameter("quantity", "number", List("item_id")))
  )
  printScenario(3, "Missing required parameter", missingParam)

  // 4. Null value for required parameter
  val nullParam = ToolCallError.InvalidArguments(
    "add_inventory_item",
    List(ToolParameterError.NullParameter("quantity", "number"))
  )
  printScenario(4, "Parameter is null", nullParam)

  // 5. Type mismatch
  val typeMismatch = ToolCallError.InvalidArguments(
    "add_inventory_item",
    List(ToolParameterError.TypeMismatch("quantity", "number", "string"))
  )
  printScenario(5, "Type mismatch", typeMismatch)

  // 6. Multiple parameter errors
  val multipleErrors = ToolCallError.InvalidArguments(
    "submit_order",
    List(
      ToolParameterError.MissingParameter("customer_id", "string"),
      ToolParameterError.TypeMismatch("quantity", "number", "string"),
      ToolParameterError.NullParameter("product_id", "string")
    )
  )
  printScenario(6, "Multiple parameter errors", multipleErrors)

  // 7. Nested parameter error
  val nestedError = ToolCallError.InvalidArguments(
    "update_profile",
    List(ToolParameterError.InvalidNesting("email", "user", "string"))
  )
  printScenario(7, "Nested parameter error", nestedError)

  // --- RESTORED JSON SECTION (Requested by Reviewer) ---
  println(separator(length = 70))
  println(s"   ${BOLD}JSON FORMAT (as returned to LLM)${RESET}   ")
  println(separator(length = 70))

  def toJsonError(error: ToolCallError): String = {
    val message = error.getFormattedMessage
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
      .replace("\n", "\\n")
    s"""{ "isError": true, "error": "$message" }"""
  }

  println("\nExample JSON responses:")
  println(s"${BOLD}1. Missing parameter:${RESET}")
  println(toJsonError(missingParam))

  println(s"\n${BOLD}2. Multiple errors:${RESET}")
  println(toJsonError(multipleErrors))

  println(s"\n${CYAN}Demo Complete.${RESET}")
}
