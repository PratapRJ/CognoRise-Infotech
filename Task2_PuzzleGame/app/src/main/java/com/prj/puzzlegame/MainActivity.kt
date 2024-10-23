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
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var image1LL: LinearLayout
    private lateinit var image2LL: LinearLayout
    private lateinit var image3LL: LinearLayout
    private lateinit var image4LL: LinearLayout
    private lateinit var image5LL: LinearLayout
    private lateinit var image6LL: LinearLayout
    private lateinit var image7LL: LinearLayout
    private lateinit var image8LL: LinearLayout
    private lateinit var image9LL: LinearLayout

    private lateinit var label1: TextView
    private lateinit var label2: TextView
    private lateinit var label3: TextView
    private lateinit var label4: TextView
    private lateinit var label5: TextView
    private lateinit var label6: TextView
    private lateinit var label7: TextView
    private lateinit var label8: TextView
    private lateinit var label9: TextView

    private lateinit var previewOfImage: ImageView
    private lateinit var changeButton: AppCompatButton
    private lateinit var resetButton: AppCompatButton

    private lateinit var linearLayouts: List<LinearLayout>
    private lateinit var labels: List<TextView>

    private var steps: Int = 0 // Steps counter
    private lateinit var currentImageParts: List<Bitmap> // Store the current image parts for reset

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)




        // Set the title for the toolbar (optional, since we already set it in XML)
        supportActionBar?.title = "Puzzle Game"
        // Initialize layouts and labels
        initializeViews()

        // Get a random drawable image from the list
        val drawableImages = listOf(
            R.drawable.puzzle_bear,
            R.drawable.puzzle_cat,
            R.drawable.puzzle_flower,
            R.drawable.puzzle_hanuman,
            R.drawable.puzzle_house,
            R.drawable.puzzle_leopord,
            R.drawable.puzzle_parot,
            R.drawable.puzzle_shiva,
            R.drawable.puzzle_tiger,
            R.drawable.puzzle_tree
        )
        var randomImage = drawableImages.random()
        previewOfImage.setImageResource(randomImage)

        // Load the random puzzle image and split into parts
        val puzzleBitmap = BitmapFactory.decodeResource(resources, randomImage)
        currentImageParts = splitImage(puzzleBitmap, 3, 3) // Store the current image parts
        val labels = listOf("1", "2", "3", "4", "5", "6", "7", "8")
        var imageLabelPairs = currentImageParts.zip(labels).shuffled()

        // Assign images and labels to layouts
        setShuffledImages(imageLabelPairs)

        changeButton.setOnClickListener {
            // Change to a new random image and update all blocks
            randomImage = drawableImages.random()

            // Convert dp to pixels (since the size is 300dp x 300dp)
            val targetWidth = (300 * resources.displayMetrics.density).toInt()
            val targetHeight = (300 * resources.displayMetrics.density).toInt()

            val originalBitmap = BitmapFactory.decodeResource(resources, randomImage)

            // Scale and crop the bitmap to fit into the 300x300 area, center-cropped
            val centerCroppedBitmap = getCenterCroppedBitmap(originalBitmap, targetWidth, targetHeight)

            // Set the center-cropped image as preview
            previewOfImage.setImageBitmap(centerCroppedBitmap)

            // Use the original bitmap for puzzle pieces (without scaling or cropping)
            currentImageParts = splitImage(originalBitmap, 3, 3)
            val imageLabelPairs = currentImageParts.zip(labels).shuffled()

            // Assign images and labels to layouts
            setShuffledImages(imageLabelPairs)
        }


        resetButton.setOnClickListener {
            // Shuffle the current image without changing it
            imageLabelPairs = currentImageParts.zip(labels).shuffled()
            setShuffledImages(imageLabelPairs)
        }

        // Set click listeners for puzzle blocks
        setClickListeners()
    }

    private fun initializeViews() {
        image1LL = findViewById(R.id.image1LL)
        image2LL = findViewById(R.id.image2LL)
        image3LL = findViewById(R.id.image3LL)
        image4LL = findViewById(R.id.image4LL)
        image5LL = findViewById(R.id.image5LL)
        image6LL = findViewById(R.id.image6LL)
        image7LL = findViewById(R.id.image7LL)
        image8LL = findViewById(R.id.image8LL)
        image9LL = findViewById(R.id.image9LL)

        label1 = findViewById(R.id.label1)
        label2 = findViewById(R.id.label2)
        label3 = findViewById(R.id.label3)
        label4 = findViewById(R.id.label4)
        label5 = findViewById(R.id.label5)
        label6 = findViewById(R.id.label6)
        label7 = findViewById(R.id.label7)
        label8 = findViewById(R.id.label8)
        label9 = findViewById(R.id.label9)

        previewOfImage = findViewById(R.id.previewOfImage)
        changeButton = findViewById(R.id.changeButton)
        resetButton = findViewById(R.id.resetButton)

        linearLayouts = listOf(
            image1LL, image2LL, image3LL,
            image4LL, image5LL, image6LL,
            image7LL, image8LL, image9LL
        )

        labels = listOf(
            label1, label2, label3,
            label4, label5, label6,
            label7, label8, label9
        )
    }

    private fun setShuffledImages(shuffledPairs: List<Pair<Bitmap, String>>) {
        for (i in 0 until 8) {
            linearLayouts[i].background = BitmapDrawable(resources, shuffledPairs[i].first)
            labels[i].text = shuffledPairs[i].second
        }

        // Leave the 9th layout empty
        linearLayouts[8].background = null
        labels[8].text = ""
    }

    private fun setClickListeners() {
        for (i in linearLayouts.indices) {
            linearLayouts[i].setOnClickListener {
                puzzleGame(i)
            }
        }
    }

    private fun puzzleGame(index: Int) {
        val clickedLayout = linearLayouts[index]

        // Step 1: If clicked block has no background, do nothing
        if (clickedLayout.background == null) return

        // Step 2: Check if any neighbor is empty
        val emptyNeighborIndex = getEmptyNeighbor(index)

        if (emptyNeighborIndex != -1) {
            // Step 3: Swap the clicked block with the empty neighbor
            swapBlocks(index, emptyNeighborIndex)

            // Increment steps
            steps++

            // Step 4: Check if the puzzle is solved after every move
            if (isPuzzleSolved()) {
                showWinnerDialog()
            }
        }
    }

    private fun getEmptyNeighbor(index: Int): Int {
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

        for (neighborIndex in neighbors) {
            if (linearLayouts[neighborIndex].background == null) {
                return neighborIndex
            }
        }

        return -1
    }

    private fun swapBlocks(clickedIndex: Int, emptyIndex: Int) {
        linearLayouts[emptyIndex].background = linearLayouts[clickedIndex].background
        labels[emptyIndex].text = labels[clickedIndex].text

        linearLayouts[clickedIndex].background = null
        labels[clickedIndex].text = ""
    }

    private fun isPuzzleSolved(): Boolean {
        for (i in 0 until 8) {
            if (labels[i].text != (i + 1).toString()) {
                return false
            }
        }
        return true
    }

    private fun showWinnerDialog() {
        // Create an instance of your custom WinnerDialogBox and pass the context and steps
        val winnerDialog = WinnerDialogBox(this, "You completed the puzzle in $steps steps!")

        // Show the custom dialog
        winnerDialog.show()

        // Shuffle pieces again with a new random image after the dialog is dismissed
        winnerDialog.setOnDismissListener {
            // Reset and shuffle the puzzle after winning
            val drawableImages = listOf(
                R.drawable.puzzle_bear,
                R.drawable.puzzle_cat,
                R.drawable.puzzle_flower,
                R.drawable.puzzle_hanuman,
                R.drawable.puzzle_house,
                R.drawable.puzzle_leopord,
                R.drawable.puzzle_parot,
                R.drawable.puzzle_shiva,
                R.drawable.puzzle_tiger,
                R.drawable.puzzle_tree
            )
            val randomImage = drawableImages.random()
            val puzzleBitmap = BitmapFactory.decodeResource(resources, randomImage)
            currentImageParts =  splitImage(puzzleBitmap, 3, 3)
            val labels = listOf("1", "2", "3", "4", "5", "6", "7", "8")
            val imageLabelPairs = currentImageParts.zip(labels).shuffled()

            setShuffledImages(imageLabelPairs)
            previewOfImage.setImageResource(randomImage)
            steps = 0 // Reset steps counter
        }
    }

    private fun getCenterCroppedBitmap(original: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val originalWidth = original.width
        val originalHeight = original.height

        // Calculate the scaling factor, maintaining the aspect ratio
        val scale = maxOf(targetWidth.toFloat() / originalWidth, targetHeight.toFloat() / originalHeight)

        // Calculate the new dimensions after scaling
        val scaledWidth = (scale * originalWidth).toInt()
        val scaledHeight = (scale * originalHeight).toInt()

        // Scale the bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true)

        // Calculate the top-left coordinates for cropping the center of the image
        val xOffset = (scaledWidth - targetWidth) / 2
        val yOffset = (scaledHeight - targetHeight) / 2

        // Crop the scaled bitmap to the target size from the center
        return Bitmap.createBitmap(scaledBitmap, xOffset, yOffset, targetWidth, targetHeight)
    }


    private fun splitImage(image: Bitmap, rows: Int, cols: Int): List<Bitmap> {
        val pieces = mutableListOf<Bitmap>()
        val pieceWidth = image.width / cols
        val pieceHeight = image.height / rows

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                if (row == rows - 1 && col == cols - 1) break // Leave the last piece as empty
                val piece = Bitmap.createBitmap(image, col * pieceWidth, row * pieceHeight, pieceWidth, pieceHeight)
                pieces.add(piece)
            }
        }
        return pieces
    }
}
