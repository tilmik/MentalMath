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
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class WorkActivity : AppCompatActivity() {

    private lateinit var buttonSubmit: Button
    private lateinit var buttonFinish: Button
    private lateinit var tvProblem: TextView
    private lateinit var etAnswer: EditText

    private lateinit var timeSource: TimeSource
    private lateinit var startTime: TimeMark
    private lateinit var manager: MathManager
    private lateinit var problemString: String

    private var totalTime: Long = 0
    private var difficulty: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_work)
        buttonSubmit = findViewById(R.id.button_submit)
        buttonFinish = findViewById(R.id.button_finish)
        tvProblem = findViewById(R.id.problem)
        etAnswer = findViewById(R.id.result)
        timeSource = TimeSource.Monotonic

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val intendedOperator = intent.getStringExtra("operator") ?: "Addition"
        difficulty = intent.getBooleanExtra("difficulty", false)
        manager = MathManager(intendedOperator, difficulty)

        buttonSubmit.setOnClickListener {
            if (etAnswer.text.isEmpty()) {
                Toast.makeText(this, "Write the answer first", Toast.LENGTH_SHORT).show()
            }
            else {
                val elapsedTime = startTime.elapsedNow().inWholeMilliseconds
                val check = manager.checkAnswer(problemString, etAnswer.text.toString())
                if (!check) {
                    Toast.makeText(this, "WRONG", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "  YAY  ", Toast.LENGTH_SHORT).show()
                }

                totalTime += elapsedTime
                populateOperand()
            }
        }

        buttonFinish.setOnClickListener {
            val data = Intent()
            data.putExtra("score", manager.score)
            data.putExtra("totalProblems", manager.totalProblems)
            data.putExtra("totalTime", totalTime)
            setResult(Activity.RESULT_OK, data)
            finish()
        }

        populateOperand()
    }

    private fun populateOperand() {
        problemString = manager.newProblem()
        tvProblem.text = problemString
        etAnswer.text.clear()
        startTime = timeSource.markNow()
    }
}