package org.llm4s.samples.toolapi

import org.llm4s.toolapi._
// IMPORT the new utility we just made
import org.llm4s.util.ConsoleColors._ 

object ImprovedErrorMessageDemo extends App {

  // No more hardcoded object Colors { ... } here! 
  // We are using the shared one from 'core'.

  println(s"${BOLD}${CYAN}" + "=" * 70 + RESET)
  println(s"${BOLD}${CYAN}   IMPROVED ERROR MESSAGE DEMONSTRATION   ${RESET}")
  println(s"${BOLD}${CYAN}" + "=" * 70 + RESET)

  // Example 1: Unknown tool
  println("") 
  println(s"${YELLOW}1. Scenario: ${RESET} ${BOLD}Unknown tool${RESET}")
  val unknownTool = ToolCallError.UnknownFunction("calculate_tax")
  
  // Notice we can now use the helper method 'red(...)'
  println(s"   Result: ${red(unknownTool.getFormattedMessage)}")

  // Example 2: Null arguments
  println("")
  println(s"${YELLOW}2. Scenario: ${RESET} ${BOLD}Null arguments${RESET}")
  val nullArgs = ToolCallError.NullArguments("add_inventory_item")
  println(s"   Result: ${red(nullArgs.getFormattedMessage)}")

  // Example 3: Missing required parameter
  println("")
  println(s"${YELLOW}3. Scenario: ${RESET} ${BOLD}Missing required parameter${RESET}")
  val missingParam = ToolCallError.InvalidArguments(
    "add_inventory_item",
    List(ToolParameterError.MissingParameter("quantity", "number", List("item_id")))
  )
  println(s"   Result: ${red(missingParam.getFormattedMessage)}")
  
  println(s"\n${CYAN}Demo Complete.${RESET}")
}