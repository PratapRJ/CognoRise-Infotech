package com.prj.bmicalculator

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar;


class MainActivity : AppCompatActivity() {

    // UI components
    private lateinit var calculateBmiButton: AppCompatButton
    private lateinit var maleButton: ImageButton
    private lateinit var femaleButton: ImageButton
    private lateinit var heightAddButton: ImageButton
    private lateinit var heightRemoveButton: ImageButton
    private lateinit var weightAddButton: ImageButton
    private lateinit var weightRemoveButton: ImageButton
    private lateinit var ageAddButton: ImageButton
    private lateinit var ageRemoveButton: ImageButton
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var toolbar: Toolbar

    // Variables
    private var isMaleSelected: Boolean = true
    private val minAge = 1
    private val maxAge = 150
    private val minHeight = 50   // Example: Minimum height in cm
    private val maxHeight = 250  // Example: Maximum height in cm
    private val minWeight = 20   // Example: Minimum weight in kg
    private val maxWeight = 300  // Example: Maximum weight in kg

    private val viewModel by viewModels<MainViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !viewModel.isReady.value
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 500L
                zoomX.doOnEnd { screen.remove() }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.4f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // Enable the Up button (back arrow)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar()?.setDisplayShowHomeEnabled(true);
        }

        // Handle toolbar navigation click
        toolbar.setNavigationOnClickListener{onBackPressed()}

        initializeViews()
        setListeners()
    }

    // Initialize all UI components
    private fun initializeViews() {
        calculateBmiButton = findViewById(R.id.calculateBmiButton)
        maleButton = findViewById(R.id.maleButton)
        femaleButton = findViewById(R.id.femaleButton)
        heightAddButton = findViewById(R.id.heightAddButton)
        heightRemoveButton = findViewById(R.id.heightRemoveButton)
        weightAddButton = findViewById(R.id.weightAddButton)
        weightRemoveButton = findViewById(R.id.weightRemoveButton)
        ageAddButton = findViewById(R.id.ageAddButton)
        ageRemoveButton = findViewById(R.id.ageRemoveButton)
        heightEditText = findViewById(R.id.heightEditText)
        weightEditText = findViewById(R.id.weightEditText)
        ageEditText = findViewById(R.id.ageEditText)
    }

    // Set listeners for buttons and actions
    private fun setListeners() {
        maleButton.setOnClickListener {
            isMaleSelected = true
            updateGenderSelection()
        }

        femaleButton.setOnClickListener {
            isMaleSelected = false
            updateGenderSelection()
        }

        heightAddButton.setOnClickListener {
            incrementValue(heightEditText)
        }

        heightRemoveButton.setOnClickListener {
            decrementValue(heightEditText)
        }

        weightAddButton.setOnClickListener {
            incrementValue(weightEditText)
        }

        weightRemoveButton.setOnClickListener {
            decrementValue(weightEditText)
        }

        ageAddButton.setOnClickListener {
            incrementValue(ageEditText)
        }

        ageRemoveButton.setOnClickListener {
            decrementValue(ageEditText)
        }

        calculateBmiButton.setOnClickListener {
            if (validateInput()) {
                navigateToResultActivity()
            }
        }
    }

    // Update button backgrounds based on gender selection
    private fun updateGenderSelection() {
        if (isMaleSelected) {
            maleButton.setBackgroundResource(R.drawable.image_background_border)
            femaleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        } else {
            femaleButton.setBackgroundResource(R.drawable.image_background_border)
            maleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    // Increment value in EditText and set cursor position to the end
    private fun incrementValue(textBox: EditText?) {
        var value = textBox?.text.toString().toIntOrNull() ?: 0
        value++
        textBox?.setText(value.toString())
        textBox?.setSelection(textBox.text.length) // Set cursor to end
    }

    // Decrement value in EditText and set cursor position to the end
    private fun decrementValue(textBox: EditText?) {
        var value = textBox?.text.toString().toIntOrNull() ?: 0
        value--
        textBox?.setText(value.toString())
        textBox?.setSelection(textBox.text.length) // Set cursor to end
    }

    // Validate the user input for age, height, and weight
    private fun validateInput(): Boolean {
        val age = ageEditText.text.toString().toIntOrNull()
        val height = heightEditText.text.toString().toIntOrNull()
        val weight = weightEditText.text.toString().toIntOrNull()

        // Validate Age
        if (age == null || age < minAge || age > maxAge) {
            ageEditText.error = "Please enter a valid age between $minAge and $maxAge."
            return false
        }

        // Validate Height
        if (height == null || height < minHeight || height > maxHeight) {
            heightEditText.error = "Please enter a valid height between $minHeight cm and $maxHeight cm."
            return false
        }

        // Validate Weight
        if (weight == null || weight < minWeight || weight > maxWeight) {
            weightEditText.error = "Please enter a valid weight between $minWeight kg and $maxWeight kg."
            return false
        }

        return true // Return true if all validations pass
    }

    // Navigate to the Result activity with the input data
    private fun navigateToResultActivity() {
        val age = ageEditText.text.toString().toInt()
        val height = heightEditText.text.toString().toInt()
        val weight = weightEditText.text.toString().toInt()
        val gender = if (isMaleSelected) "Male" else "Female"

        // Create intent and pass the input data
        val intent = Intent(this, Result::class.java).apply {
            putExtra("AGE", age)
            putExtra("HEIGHT", height)
            putExtra("WEIGHT", weight)
            putExtra("GENDER", gender)
        }
        startActivity(intent)
    }
}
