package com.sourjyab.mentalmath

class MathManager(op: String, diff: Boolean) {

    private var operator: Int = 0
    private var difficulty: Boolean = false

    private var problem: String = ""
    private var answer: Int = 0

    var score: Int = 0
    var totalProblems: Int = 0

    init {
        when (op) {
            "Addition" -> operator = 0
            "Subtraction" -> operator = 1
            "Multiplication" -> operator = 2
            "Division" -> operator = 3
        }
        difficulty = diff
    }

    fun newProblem(): String{
        val number1: Int
        val number2: Int
        val operatorText: String

        when (operator) {
            1 -> {
                number2 = (20..89).random()
                number1 = number2 + (30..69).random()
                operatorText = "-"
                answer = number1 - number2
            }
            2 -> {
                number1 = (20..50).random()
                number2 = (5..19).random()
                operatorText = "*"
                answer = number1 * number2
            }
            3 -> {
                number2 = (5..15).random()
                number1 = number2 * (5..35).random()
                operatorText = "÷"
                answer = number1 / number2
            }
            else -> {
                number1 = (100..200).random()
                number2 = (20..99).random()
                operatorText = "+"
                answer = number1 + number2
            }
        }

        problem = "$number1 $operatorText $number2"
        return problem
    }

    fun checkAnswer(prob: String, ans: String): Boolean {
        var ansValue: Int
        try {
            ansValue = ans.toInt()
        } catch (nfe: NumberFormatException) {
            // not a valid int
            ansValue = Int.MIN_VALUE
        }

        if (problem == prob && answer == ansValue) {
            score++
            totalProblems++
            return true
        }

        if (problem == prob)
            totalProblems++

        return false
    }
}