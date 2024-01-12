package com.example.metrack

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.example.metrack.firestore.FireStoreClass
import com.example.metrack.firestore.User
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


class LoginActivity : SnackActivity(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var loginButton: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        inputEmail = findViewById(R.id.editTextEmailAddressLogin)
        inputPassword = findViewById(R.id.editTextPasswordLogin)
        loginButton = findViewById(R.id.buttonLogin)

        loginButton?.setOnClickListener {
            logInRegisteredUser()
        }


    }





    override fun onClick(view: View?) {
        view?.let {
            when (it.id) {
                R.id.textViewRegister -> {
                    val intent = Intent(this, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        with(inputEmail?.text.toString().trim()) {
            return when {
                TextUtils.isEmpty(this) -> {
                    showErrorSnackBar("Please enter email.", true)
                    false
                }

                TextUtils.isEmpty(inputPassword?.text.toString().trim()) -> {
                    showErrorSnackBar("Please enter password.", true)
                    false
                }

                else -> {
                    showErrorSnackBar("Your details are valid.", false)
                    true
                }
            }
        }
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {
            val email = inputEmail?.text.toString().trim()
            val password = inputPassword?.text.toString().trim()

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showErrorSnackBar("You are logged in successfully.", false)
                        goToMainActivity()
                        finish()
                    } else {
                        showErrorSnackBar(task.exception?.message.toString(), true)
                    }
                }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}