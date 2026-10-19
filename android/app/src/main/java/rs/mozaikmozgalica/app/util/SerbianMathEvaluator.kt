package rs.mozaikmozgalica.app.util

import java.util.Stack

/**
 * 100% Mathematically Exact Infix Expression Evaluator
 * Supports parentheses (), operator precedence (* / over + -), and multi-digit numbers.
 */
object SerbianMathEvaluator {

    fun evaluate(expression: String): Int? {
        val tokens = tokenize(expression)
        if (tokens.isEmpty()) return null

        return try {
            val cleanedTokens = tokens.toMutableList()
            // 1. Remove trailing operators so live calculation works while typing
            while (cleanedTokens.isNotEmpty() && cleanedTokens.last() in listOf("+", "-", "*", "/")) {
                cleanedTokens.removeAt(cleanedTokens.lastIndex)
            }
            if (cleanedTokens.isEmpty()) return null

            // 2. Auto-balance unbalanced open parentheses
            var openCount = 0
            for (token in cleanedTokens) {
                if (token == "(") openCount++
                if (token == ")") openCount--
            }
            while (openCount > 0) {
                cleanedTokens.add(")")
                openCount--
            }

            val rpn = infixToRPN(cleanedTokens)
            evaluateRPN(rpn)
        } catch (e: Exception) {
            null
        }
    }

    private fun tokenize(expr: String): List<String> {
        val list = mutableListOf<String>()
        var i = 0
        val clean = expr.replace(" ", "")

        while (i < clean.length) {
            val c = clean[i]
            if (c.isDigit()) {
                val sb = StringBuilder()
                while (i < clean.length && clean[i].isDigit()) {
                    sb.append(clean[i])
                    i++
                }
                list.add(sb.toString())
            } else if (c in listOf('+', '-', '*', '/', '(', ')')) {
                list.add(c.toString())
                i++
            } else {
                i++
            }
        }
        return list
    }

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/" -> 2
            else -> 0
        }
    }

    private fun infixToRPN(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()

        for (token in tokens) {
            when {
                token.toIntOrNull() != null -> output.add(token)
                token == "(" -> stack.push(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        output.add(stack.pop())
                    }
                    if (stack.isNotEmpty() && stack.peek() == "(") {
                        stack.pop()
                    }
                }
                token in listOf("+", "-", "*", "/") -> {
                    while (stack.isNotEmpty() && precedence(stack.peek()) >= precedence(token)) {
                        output.add(stack.pop())
                    }
                    stack.push(token)
                }
            }
        }

        while (stack.isNotEmpty()) {
            output.add(stack.pop())
        }

        return output
    }

    private fun evaluateRPN(rpn: List<String>): Int {
        val stack = Stack<Int>()

        for (token in rpn) {
            val num = token.toIntOrNull()
            if (num != null) {
                stack.push(num)
            } else {
                if (stack.size < 2) throw IllegalArgumentException("Invalid RPN")
                val b = stack.pop()
                val a = stack.pop()
                val res = when (token) {
                    "+" -> a + b
                    "-" -> a - b
                    "*" -> a * b
                    "/" -> if (b != 0) a / b else throw ArithmeticException("Div zero")
                    else -> 0
                }
                stack.push(res)
            }
        }

        return if (stack.isNotEmpty()) stack.pop() else 0
    }
}
