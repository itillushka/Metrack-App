package com.example.metrack

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import android.widget.ImageView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle

class MainActivity : AppCompatActivity() {

    lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val bodyView = findViewById<ImageView>(R.id.bodyView)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerlayout)
        val navView = findViewById<NavigationView>(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when(it.itemId){
                R.id.nav_recent_documents -> Toast.makeText(applicationContext, "Home", Toast.LENGTH_SHORT).show()
                R.id.nav_insurance -> Toast.makeText(applicationContext, "Login", Toast.LENGTH_SHORT).show()
                R.id.nav_prescriptions -> Toast.makeText(applicationContext, "Delete", Toast.LENGTH_SHORT).show()
                R.id.nav_notification -> Toast.makeText(applicationContext, "Notifications", Toast.LENGTH_SHORT).show()
            }
            true
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
