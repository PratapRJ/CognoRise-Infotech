package com.prj.bmicalculator

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class Result : AppCompatActivity() {

    // Declare views for displaying result data
    private lateinit var ageTextView: TextView
    private lateinit var heightTextView: TextView
    private lateinit var weightTextView: TextView
    private lateinit var genderTextView: TextView
    private lateinit var bmiScoreTextView: TextView
    private lateinit var bmiCategoryTextView: TextView
    private lateinit var bmiGaugeView: BmiGaugeView
    private lateinit var recalculateBmiButton: AppCompatButton
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Enable the Up button (back arrow)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        // Handle toolbar navigation click
        toolbar.setNavigationOnClickListener{onBackPressed()}
        // Initialize the views
        initializeViews()
        setListeners()
        // Extract data passed through the Intent
        val age = intent.getIntExtra("AGE", 0)
        val height = intent.getIntExtra("HEIGHT", 0)
        val weight = intent.getIntExtra("WEIGHT", 0)
        val gender = intent.getStringExtra("GENDER") ?: "Unknown"

        // Calculate the BMI value
        val bmi = calculateBMI(weight.toDouble(), height.toDouble())

        // Update the UI with received and calculated data
        updateUI(age, height, weight, gender, bmi)
    }

    /**
     * Initializes all the view components.
     */
    private fun initializeViews() {
        bmiGaugeView = findViewById(R.id.bmiGaugeView)
        ageTextView = findViewById(R.id.ageTextView)
        heightTextView = findViewById(R.id.heightTextView)
        weightTextView = findViewById(R.id.weightTextView)
        genderTextView = findViewById(R.id.genderTextView)
        bmiScoreTextView = findViewById(R.id.bmiScoreTextView)
        bmiCategoryTextView = findViewById(R.id.bmiCategoryTextView)
        recalculateBmiButton = findViewById(R.id.recalculateBmiButton)
    }

    // Set listeners for buttons and actions
    private fun setListeners() {
        recalculateBmiButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Updates the UI elements with the provided data.
     *
     * @param age Age of the user
     * @param height Height of the user in cm
     * @param weight Weight of the user in kg
     * @param gender Gender of the user
     * @param bmi Calculated BMI
     */
    private fun updateUI(age: Int, height: Int, weight: Int, gender: String, bmi: Double) {
        ageTextView.text = getString(R.string.age_format, age)
        heightTextView.text = getString(R.string.height_format, height)
        weightTextView.text = getString(R.string.weight_format, weight)
        genderTextView.text = gender

        // Update BMI score and category
        bmiScoreTextView.text = String.format("%.2f", bmi)
        updateBMICategory(bmi)

        // Update the gauge view based on the BMI
        bmiGaugeView.setBMIValue(bmi)
    }

    /**
     * Calculates the BMI using the provided weight and height.
     *
     * @param weight Weight in kg
     * @param height Height in cm
     * @return The calculated BMI
     */
    private fun calculateBMI(weight: Double, height: Double): Double {
        val heightInMeters = height / 100.0
        return weight / (heightInMeters * heightInMeters)
    }

    /**
     * Updates the BMI category and its corresponding text color based on the BMI value.
     *
     * @param bmi The calculated BMI
     */
    private fun updateBMICategory(bmi: Double) {
        val bmiCategory = getBMICategory(bmi)
        val categoryColor = getBMICategoryColor(bmi)

        bmiCategoryTextView.text = bmiCategory
        bmiCategoryTextView.setTextColor(categoryColor)
    }

    /**
     * Determines the BMI category based on the BMI value.
     *
     * @param bmi The calculated BMI
     * @return The BMI category as a string
     */
    private fun getBMICategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> getString(R.string.underweight)
            bmi < 25.0 -> getString(R.string.normal)
            bmi < 30.0 -> getString(R.string.overweight)
            else -> getString(R.string.obese)
        }
    }

    /**
     * Returns the appropriate color for the BMI category.
     *
     * @param bmi The calculated BMI
     * @return The color resource ID for the BMI category
     */
    private fun getBMICategoryColor(bmi: Double): Int {
        return when {
            bmi < 18.5 -> ContextCompat.getColor(this, R.color.underWeightColor)
            bmi < 25.0 -> ContextCompat.getColor(this, R.color.normalWeightColor)
            bmi < 30.0 -> ContextCompat.getColor(this, R.color.overWeightColor)
            else -> ContextCompat.getColor(this, R.color.obeseColor)
        }
    }
}
