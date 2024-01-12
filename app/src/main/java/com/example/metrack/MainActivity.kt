package com.example.metrack

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val bodyView = findViewById<ImageView>(R.id.bodyView)
        val userView = findViewById<ImageView>(R.id.userView)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)


        userView.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }

        bodyView.setOnClickListener {
            val dialog = Dialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.pop_up_body, null)
            dialog.setContentView(view)

            val window = dialog.window
            window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            dialog.show()
        }
    }
}