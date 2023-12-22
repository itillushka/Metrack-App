package com.example.metrack


import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.metrack.R
import com.google.android.material.snackbar.Snackbar

open class SnackActivity : AppCompatActivity() {

    fun showErrorSnackBar(message: String, isError: Boolean) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view

        if (!isError) {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@SnackActivity,
                    R.color.snackBarSuccessful
                )
            )
        } else {
            snackbarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@SnackActivity,
                    R.color.snackBarError
                )
            )
        }
        snackbar.show()
    }

}