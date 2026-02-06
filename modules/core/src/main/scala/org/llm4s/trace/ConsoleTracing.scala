package org.llm4s.trace

import org.llm4s.error.UnknownError
import org.llm4s.agent.AgentState
import org.llm4s.llmconnect.model.{ TokenUsage, Completion }
import org.llm4s.types.Result
import scala.util.Try

/**
 * Console-based [[Tracing]] implementation with colored, formatted output.
 *
 * Prints trace events to standard output with ANSI color formatting
 * for improved readability during development and debugging.
 * Returns `Result[Unit]` to support functional composition.
 *
 * == Features ==
 *
 *  - Color-coded output by event type (errors in red, success in green, etc.)
 *  - Visual separators and headers for different trace sections
 *  - Formatted display of all [[TraceEvent]] types
 *  - Truncation of long JSON content for readability
 *  - Timestamps on all events
 *
 * == Usage ==
 *
 * {{{
 * val tracing: Tracing = new ConsoleTracing()
 *
 * // Trace events functionally
 * for {
 *   _ <- tracing.traceEvent(TraceEvent.AgentInitialized("query", Vector("tool1")))
 *   _ <- tracing.traceTokenUsage(TokenUsage(100, 50, 150), "gpt-4", "completion")
 * } yield ()
 * }}}
 *
 * @see [[NoOpTracing]] for silent tracing
 * @see [[LangfuseTracing]] for production observability
 * @see [[AnsiColors]] for color constants used
 */
class ConsoleTracing extends Tracing {
  
  private def printHeader(title: String): Unit = {
    val sep = "=" * 60
    println(sep)
    println(title)
    println(sep)
  }

  private def printSubHeader(title: String): Unit =
    println(s"--- $title ---")

  private def formatJson(json: String, maxLength: Int): String =
    if (json.length > maxLength) {
      json.take(maxLength) + "..."
    } else {
      json
    }

  def traceEvent(event: TraceEvent): Result[Unit] =
    Try {
      event match {
        case e: TraceEvent.AgentInitialized =>
          println()
          printSubHeader("AGENT INITIALIZED")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Query: ${e.query}")
          println(s"Tools: ${e.tools.mkString(", ")}")
          println()

        case e: TraceEvent.CompletionReceived =>
          println()
          printHeader("COMPLETION RECEIVED")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Model: ${e.model}")
          println(s"ID: ${e.id}")
          println(s"Tool Calls: ${e.toolCalls}")
          println(s"Content: ${formatJson(e.content, 200)}")
          println()

        case e: TraceEvent.ToolExecuted =>
          println()
          printSubHeader("TOOL EXECUTED")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Tool: ${e.name}")
          println(s"Success: ${e.success}")
          println(s"Duration: ${e.duration}ms")
          println(s"Input: ${formatJson(e.input, 100)}")
          println(s"Output: ${formatJson(e.output, 100)}")
          println()

        case e: TraceEvent.ErrorOccurred =>
          println()
          printHeader("ERROR OCCURRED")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Type: ${e.error.getClass.getSimpleName}")
          println(s"Message: ${e.error.getMessage}")
          println(s"Context: ${e.context}")
          println()

        case e: TraceEvent.TokenUsageRecorded =>
          println()
          printSubHeader("TOKEN USAGE")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Model: ${e.model}")
          println(s"Operation: ${e.operation}")
          println(s"Prompt Tokens: ${e.usage.promptTokens}")
          println(s"Completion Tokens: ${e.usage.completionTokens}")
          println(s"Total Tokens: ${e.usage.totalTokens}")
          println()

        case e: TraceEvent.AgentStateUpdated =>
          println()
          printSubHeader("AGENT STATE UPDATED")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Status: ${e.status}")
          println(s"Messages: ${e.messageCount}")
          println(s"Logs: ${e.logCount}")
          println()

        case e: TraceEvent.CustomEvent =>
          println()
          printSubHeader("CUSTOM EVENT")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Name: ${e.name}")
          println(s"Data: ${e.data}")
          println()

        case e: TraceEvent.EmbeddingUsageRecorded =>
          println()
          printSubHeader("EMBEDDING USAGE")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Model: ${e.model}")
          println(s"Operation: ${e.operation}")
          println(s"Input Count: ${e.inputCount}")
          println(s"Prompt Tokens: ${e.usage.promptTokens}")
          println(s"Total Tokens: ${e.usage.totalTokens}")
          println()

        case e: TraceEvent.CostRecorded =>
          println()
          printSubHeader("COST RECORDED")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Model: ${e.model}")
          println(s"Operation: ${e.operation}")
          println(s"Token Count: ${e.tokenCount}")
          println(s"Cost Type: ${e.costType}")
          // FIXED: Used f-interpolator for proper currency formatting
          println(f"Cost (USD): $$${e.costUsd}%.6f")
          println()

        case e: TraceEvent.CacheHit =>
          println()
          printSubHeader("CACHE HIT")
          println(s"Timestamp: ${e.timestamp}")
          // FIXED: Used f-interpolator for floats
          println(f"Similarity: ${e.similarity}%.4f")
          println(f"Threshold: ${e.threshold}%.4f")
          println()

        case e: TraceEvent.CacheMiss =>
          println()
          printSubHeader("CACHE MISS")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Reason: ${e.reason.value}")
          println()

        case e: TraceEvent.RAGOperationCompleted =>
          println()
          printSubHeader("RAG OPERATION COMPLETED")
          println(s"Timestamp: ${e.timestamp}")
          println(s"Operation: ${e.operation}")
          println(s"Duration: ${e.durationMs}ms")
          e.embeddingTokens.foreach(t => println(s"Embedding Tokens: $t"))
          e.llmPromptTokens.foreach(t => println(s"LLM Prompt Tokens: $t"))
          e.llmCompletionTokens.foreach(t => println(s"LLM Completion Tokens: $t"))
          // FIXED: Used f-interpolator for proper currency formatting
          e.totalCostUsd.foreach(c => println(f"Total Cost (USD): $$${c}%.6f"))
          println()
      }
    }.toEither.left.map(error => UnknownError(error.getMessage, error))

  def traceAgentState(state: AgentState): Result[Unit] = {
    val event = TraceEvent.AgentStateUpdated(
      status = state.status.toString,
      messageCount = state.conversation.messages.length,
      logCount = state.logs.length
    )
    traceEvent(event)
  }

  def traceToolCall(toolName: String, input: String, output: String): Result[Unit] = {
    val event = TraceEvent.ToolExecuted(toolName, input, output, 0, true)
    traceEvent(event)
  }

  def traceError(error: Throwable, context: String): Result[Unit] = {
    val event = TraceEvent.ErrorOccurred(error, context)
    traceEvent(event)
  }

  def traceCompletion(completion: Completion, model: String): Result[Unit] = {
    val event = TraceEvent.CompletionReceived(
      id = completion.id,
      model = model,
      toolCalls = completion.message.toolCalls.size,
      content = completion.message.content
    )
    traceEvent(event)
  }

  def traceTokenUsage(usage: TokenUsage, model: String, operation: String): Result[Unit] = {
    val event = TraceEvent.TokenUsageRecorded(usage, model, operation)
    traceEvent(event)
  }
}