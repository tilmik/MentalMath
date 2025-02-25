package com.sourjyab.mentalmath

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
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

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var operatorDropdown: AutoCompleteTextView
    private lateinit var limitedSwitch: SwitchCompat
    private lateinit var numberOfProblems: EditText
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
        startButton = findViewById(R.id.button_start)

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

        limitedSwitch= findViewById(R.id.switch_limited)
        numberOfProblems = findViewById(R.id.edit_number_of_problems)
        numberOfProblems.filters = arrayOf<InputFilter>(MinMaxFilter(1, 99))
        numberOfProblems.visibility = View.GONE
        limitedSwitch.setOnCheckedChangeListener{_, isChecked->
            if (isChecked) {
                numberOfProblems.visibility = View.VISIBLE
                limitedSwitch.text = resources.getString(R.string.fixed)
            } else {
                numberOfProblems.visibility = View.GONE
                limitedSwitch.text = resources.getString(R.string.unlimited)
            }
        }

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

        startButton.setOnClickListener {
            val operator = operatorDropdown.text.toString()
            val isFixed = limitedSwitch.isChecked
            val fixedNumber = try {
                numberOfProblems.text.toString().toInt()
            } catch (e: Exception) {
                Int.MIN_VALUE
            }

            if (allowedOperators.contains(operator) &&
                (!isFixed || fixedNumber in 1..99)) {
                summaryLayout.visibility = View.GONE

                val intent = Intent(this, WorkActivity::class.java)
                intent.putExtra("operator", operator)
                intent.putExtra("fixed", isFixed)
                intent.putExtra("number", fixedNumber)
                resultLauncher.launch(intent)
            }
            else {
                Toast.makeText(this, "Select an operator first", Toast.LENGTH_SHORT).show()
            }
        }
    }
}