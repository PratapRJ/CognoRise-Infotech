package com.prj.puzzlegame

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.prj.puzzlegame.R

class WinnerDialogBox(context: Context, winnerName: String) : Dialog(context) {

    init {
        // Set the layout for the dialog
        setContentView(R.layout.activity_winner_dialog_box)

        // Set window properties
        window?.setBackgroundDrawableResource(R.drawable.winner_dialog_background) // Set custom background if desired
        setCancelable(false) // Prevent dismissal by clicking outside

        // Set the winner's name
        val winnerNameTextView: TextView = findViewById(R.id.winner_name)
        winnerNameTextView.text = winnerName

        // Set the listener for the OK button
        val okButton: Button = findViewById(R.id.ok_button)
        okButton.setOnClickListener {
            dismiss() // Close the dialog
        }
    }
}
