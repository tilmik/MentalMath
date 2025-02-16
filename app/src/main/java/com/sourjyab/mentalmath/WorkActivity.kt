package com.sourjyab.mentalmath

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class WorkActivity : AppCompatActivity() {

    private lateinit var buttonSubmit: Button
    private lateinit var buttonFinish: Button
    private lateinit var operand1: TextView
    private lateinit var operand2: TextView
    private lateinit var tvOperator: TextView
    private lateinit var result: EditText
    private lateinit var timeSource: TimeSource
    private lateinit var startTime: TimeMark

    private var number1: Int = 0
    private var number2: Int = 0
    private var score: Int = 0
    private var totalProblems: Int = 0
    private var totalTime: Long = 0
    private var operator: Int = 0
    private var difficulty: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_work)
        buttonSubmit = findViewById(R.id.button_submit)
        buttonFinish = findViewById(R.id.button_finish)
        operand1 = findViewById(R.id.operand_first)
        operand2 = findViewById(R.id.operand_second)
        result = findViewById(R.id.result)
        timeSource = TimeSource.Monotonic

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intendedOperator = intent.getStringExtra("operator") ?: "Addition"
        difficulty = intent.getBooleanExtra("difficulty", false)
        when (intendedOperator) {
            "Addition" -> operator = 0
            "Subtraction" -> operator = 1
            "Multiplication" -> operator = 2
            "Division" -> operator = 3
        }

        tvOperator = findViewById(R.id.operator)
        tvOperator.text = when (operator) {
            0 -> "+"
            1 -> "-"
            2 -> "*"
            3 -> "÷"
            else -> "+"
        }

        buttonSubmit.setOnClickListener {
            if (result.text.isEmpty()) {
                Toast.makeText(this, "Write the answer first", Toast.LENGTH_SHORT).show()
            }
            else {
                val elapsedTime = startTime.elapsedNow().inWholeMilliseconds
                val check = verifyResult(result.text.toString())
                if (!check) {
                    Toast.makeText(this, "WRONG", Toast.LENGTH_SHORT).show()
                } else {
                    score++
                    Toast.makeText(this, "  YAY  ", Toast.LENGTH_SHORT).show()
                }

                totalProblems++
                totalTime += elapsedTime
                populateOperand()
            }
        }

        buttonFinish.setOnClickListener {
            val data = Intent()
            data.putExtra("score", score)
            data.putExtra("totalProblems", totalProblems)
            data.putExtra("totalTime", totalTime)
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        populateOperand()
    }

    private fun populateOperand() {
        if (operator == 0) {
            number1 = (100..200).random()
            number2 = (20..99).random()
        } else if (operator == 1) {
            number2 = (20..89).random()
            number1 = number2 + (30..69).random()
        } else if (operator == 2) {
            number1 = (20..50).random()
            number2 = (5..19).random()
        } else if (operator == 3) {
            number2 = (5..15).random()
            number1 = number2 * (5..35).random()
        }

        operand1.text = number1.toString()
        operand2.text = number2.toString()
        result.text.clear()

        startTime = timeSource.markNow()
    }

    private fun verifyResult(resultString: String) : Boolean {
        var resultValue = 0

        try {
            resultValue = resultString.toInt()
        } catch (nfe: NumberFormatException) {
            // not a valid int
        }

        // Assuming addition
        val expectedValue = when(operator) {
            0 -> number1 + number2
            1 -> number1 - number2
            2 -> number1 * number2
            3 -> number1 / number2
            else -> number1 + number2
        }

        return expectedValue == resultValue
    }
}