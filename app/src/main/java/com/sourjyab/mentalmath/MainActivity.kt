package com.sourjyab.mentalmath

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.transition.Visibility

class MainActivity : AppCompatActivity() {

    private lateinit var buttonStart: Button
    private lateinit var operatorDropdown: AutoCompleteTextView
    private lateinit var difficultySwitch: SwitchCompat
    private lateinit var summaryLayout: TableLayout
    private lateinit var totalProblemsTextView: TextView
    private lateinit var accuracyTextView: TextView
    private lateinit var speedTextView: TextView
    private lateinit var allowedOperators: HashSet<String>
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        buttonStart = findViewById(R.id.button_start)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        summaryLayout = findViewById(R.id.layout_summary)
        totalProblemsTextView = findViewById(R.id.summary_total_problems)
        accuracyTextView = findViewById(R.id.summary_accuracy)
        speedTextView = findViewById(R.id.summary_speed)
        summaryLayout.visibility = View.GONE

        val operators = resources.getStringArray(R.array.operators)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_operator, operators)
        operatorDropdown = findViewById(R.id.operatorDropdown)
        operatorDropdown.setAdapter(arrayAdapter)

        allowedOperators = HashSet()
        for (o in operators)
            allowedOperators.add(o)

        difficultySwitch= findViewById(R.id.switch_difficulty)

        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val score = result.data?.getIntExtra("score", 0) ?: 0
                val totalProblems = result.data?.getIntExtra("totalProblems", 0) ?: 0
                val totalTime = result.data?.getLongExtra("totalTime", 0) ?: 0

                if (totalProblems > 0) {
                    val accuracy = Math.round(score.toFloat() * 10000.0.toFloat() / totalProblems.toFloat()) / 100.0.toFloat()
                    val speed = Math.round(totalTime.toDouble() / totalProblems.toDouble()) / 1000.0.toDouble()

                    //Toast.makeText(this, "Accuracy $accuracy and speed $speed", Toast.LENGTH_LONG).show()
                    totalProblemsTextView.text = resources.getString(R.string.summary_total_problems, totalProblems)
                    accuracyTextView.text = resources.getString(R.string.summary_accuracy, accuracy)
                    speedTextView.text = resources.getString(R.string.summary_speed, speed)
                    summaryLayout.visibility = View.VISIBLE
                }
            }
        }

        buttonStart.setOnClickListener {
            val operator = operatorDropdown.text.toString()
            if (allowedOperators.contains(operator)) {
                summaryLayout.visibility = View.GONE

                val intent = Intent(this, WorkActivity::class.java)
                intent.putExtra("operator", operator)
                intent.putExtra("difficulty", difficultySwitch.isChecked)
                resultLauncher.launch(intent)
            }
            else {
                Toast.makeText(this, "Select an operator first", Toast.LENGTH_SHORT).show()
            }
        }
    }
}