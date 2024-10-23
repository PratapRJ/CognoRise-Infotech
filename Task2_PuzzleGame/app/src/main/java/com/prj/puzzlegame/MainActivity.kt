package com.prj.puzzlegame

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    // View elements
    private lateinit var linearLayouts: List<LinearLayout>
    private lateinit var labels: List<TextView>
    private lateinit var previewImageView: ImageView
    private lateinit var changeImageButton: AppCompatButton
    private lateinit var resetButton: AppCompatButton

    // Game state
    private var stepsCount: Int = 0
    private lateinit var currentImageParts: List<Bitmap>
    private val labelTexts = (1..8).map { it.toString() }

    // Constants for image size
    private val targetImageSizeDp = 300
    private val gridSize = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()

        // Initialize UI elements
        initializeViews()

        // Load and set a random image as the puzzle preview
        setRandomPuzzleImage()

        // Set up button actions
        setupButtons()

        // Initialize click listeners for puzzle blocks
        setupClickListeners()
    }

    private fun setupToolbar() {
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Puzzle Game"
    }

    private fun initializeViews() {
        // Initialize linear layouts and labels
        linearLayouts = listOf(
            findViewById(R.id.image1LL), findViewById(R.id.image2LL), findViewById(R.id.image3LL),
            findViewById(R.id.image4LL), findViewById(R.id.image5LL), findViewById(R.id.image6LL),
            findViewById(R.id.image7LL), findViewById(R.id.image8LL), findViewById(R.id.image9LL)
        )

        labels = listOf(
            findViewById(R.id.label1), findViewById(R.id.label2), findViewById(R.id.label3),
            findViewById(R.id.label4), findViewById(R.id.label5), findViewById(R.id.label6),
            findViewById(R.id.label7), findViewById(R.id.label8), findViewById(R.id.label9)
        )

        previewImageView = findViewById(R.id.previewOfImage)
        changeImageButton = findViewById(R.id.changeButton)
        resetButton = findViewById(R.id.resetButton)
    }

    private fun setRandomPuzzleImage() {
        val randomImageResource = getRandomImageResource()
        previewImageView.setImageResource(randomImageResource)

        val puzzleBitmap = BitmapFactory.decodeResource(resources, randomImageResource)
        currentImageParts = splitImageIntoParts(puzzleBitmap, gridSize)

        // Shuffle the puzzle parts and set them to the grid
        setShuffledImagesToGrid(currentImageParts.zip(labelTexts).shuffled())
    }

    private fun setupButtons() {
        changeImageButton.setOnClickListener {
            setRandomPuzzleImage()
            stepsCount = 0
        }

        resetButton.setOnClickListener {
            // Shuffle the current image without changing it
            setShuffledImagesToGrid(currentImageParts.zip(labelTexts).shuffled())
            stepsCount=0
        }
    }

    private fun setupClickListeners() {
        linearLayouts.forEachIndexed { index, layout ->
            layout.setOnClickListener { handlePuzzleTileClick(index) }
        }
    }

    private fun handlePuzzleTileClick(index: Int) {
        val clickedTile = linearLayouts[index]

        // If the clicked tile is empty, do nothing
        if (clickedTile.background == null) return

        // Find an empty neighboring tile
        val emptyNeighborIndex = findEmptyNeighbor(index)

        if (emptyNeighborIndex != -1) {
            // Swap the clicked tile with the empty neighbor
            swapTiles(index, emptyNeighborIndex)
            stepsCount++

            // Check if the puzzle is solved after each move
            if (isPuzzleSolved()) showPuzzleCompletionDialog()
        }
    }

    private fun setShuffledImagesToGrid(shuffledPairs: List<Pair<Bitmap, String>>) {
        for (i in 0 until 8) {
            linearLayouts[i].background = BitmapDrawable(resources, shuffledPairs[i].first)
            labels[i].text = shuffledPairs[i].second
        }

        // Leave the last (9th) tile empty
        linearLayouts[8].background = null
        labels[8].text = ""
    }

    private fun findEmptyNeighbor(index: Int): Int {
        val neighbors = when (index) {
            0 -> listOf(1, 3)
            1 -> listOf(0, 2, 4)
            2 -> listOf(1, 5)
            3 -> listOf(0, 4, 6)
            4 -> listOf(1, 3, 5, 7)
            5 -> listOf(2, 4, 8)
            6 -> listOf(3, 7)
            7 -> listOf(4, 6, 8)
            8 -> listOf(5, 7)
            else -> emptyList()
        }

        return neighbors.firstOrNull { linearLayouts[it].background == null } ?: -1
    }

    private fun swapTiles(fromIndex: Int, toIndex: Int) {
        linearLayouts[toIndex].background = linearLayouts[fromIndex].background
        labels[toIndex].text = labels[fromIndex].text

        linearLayouts[fromIndex].background = null
        labels[fromIndex].text = ""
    }

    private fun isPuzzleSolved(): Boolean {
        for (i in 0 until 8) {
            if (labels[i].text != (i + 1).toString()) return false
        }
        return true
    }

    private fun showPuzzleCompletionDialog() {
        val dialog = WinnerDialogBox(this, "You completed the puzzle in $stepsCount steps!")
        dialog.show()

        dialog.setOnDismissListener {
            setRandomPuzzleImage()
            stepsCount = 0 // Reset step counter after puzzle completion
        }
    }

    private fun getRandomImageResource(): Int {
        val drawableImages = listOf(
            R.drawable.puzzle_bear, R.drawable.puzzle_cat, R.drawable.puzzle_flower,
            R.drawable.puzzle_hanuman, R.drawable.puzzle_house, R.drawable.puzzle_leopord,
            R.drawable.puzzle_parot, R.drawable.puzzle_shiva, R.drawable.puzzle_tiger, R.drawable.puzzle_tree
        )
        return drawableImages.random()
    }

    private fun splitImageIntoParts(image: Bitmap, rows: Int): List<Bitmap> {
        val parts = mutableListOf<Bitmap>()
        val partWidth = image.width / rows
        val partHeight = image.height / rows

        for (row in 0 until rows) {
            for (col in 0 until rows) {
                if (row == rows - 1 && col == rows - 1) break // Leave the last piece empty
                val part = Bitmap.createBitmap(image, col * partWidth, row * partHeight, partWidth, partHeight)
                parts.add(part)
            }
        }
        return parts
    }

    private fun getScaledBitmap(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        // Calculate scaling factor while maintaining aspect ratio
        val scaleFactor = maxOf(targetWidth.toFloat() / originalWidth, targetHeight.toFloat() / originalHeight)

        // Calculate new scaled dimensions
        val scaledWidth = (originalWidth * scaleFactor).toInt()
        val scaledHeight = (originalHeight * scaleFactor).toInt()

        // Scale the bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)

        // Center crop the scaled image
        val xOffset = (scaledWidth - targetWidth) / 2
        val yOffset = (scaledHeight - targetHeight) / 2

        return Bitmap.createBitmap(scaledBitmap, xOffset, yOffset, targetWidth, targetHeight)
    }
}
