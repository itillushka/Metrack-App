package com.example.metrack

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.metrack.firestore.FireStoreClass
import com.example.metrack.firestore.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class RegisterActivity : SnackActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var inputName: EditText? = null
    private var inputEmail: EditText? = null
    private var inputDoB: EditText? = null
    private var inputPassword: EditText? = null
    private var inputPasswordRepeat: EditText? = null

    @SuppressLint("UseSwitchCompatOrMaterialCode", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val registerButton: Button = findViewById(R.id.buttonRegister)
        registerButton.isEnabled = true

        inputName = findViewById(R.id.editTextName)
        inputEmail = findViewById(R.id.editTextEmailAddress)
        inputDoB = findViewById(R.id.editTextDate)
        inputPassword = findViewById(R.id.editTextPassword)
        inputPasswordRepeat = findViewById(R.id.editTextPasswordRepeat)

        registerButton.setOnClickListener {
            if (validateRegisterDetails()) {
                registerUser()
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button_register)
        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Handle sign-in error
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success, add user to Firestore and navigate to MainActivity
                    val fireStoreClass = FireStoreClass()
                    val user = User(auth.currentUser!!.uid, account.displayName!!, account.email!!)
                    fireStoreClass.registerUserFS(this, user)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Registration failure
                }
            }
    }

    private fun validateRegisterDetails(): Boolean {
        val password: String = inputPassword?.text.toString().trim { it <= ' ' }

        return when {
            TextUtils.isEmpty(inputEmail?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter email.", true)
                false
            }

            TextUtils.isEmpty(inputName?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter name.", true)
                false
            }

            TextUtils.isEmpty(inputDoB?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter date of birth.", true)
                false
            }

            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.", true)
                false
            }

            TextUtils.isEmpty(inputPasswordRepeat?.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar("Please enter repeat password.", true)
                false
            }

            password != inputPasswordRepeat?.text.toString().trim { it <= ' ' } -> {
                showErrorSnackBar("Password mismatch.", true)
                false
            }

            else -> {
                true
            }
        }
    }

    private fun registerUser() {
        val name: String = inputName?.text.toString().trim { it <= ' ' }
        val dateOfBirth: String = inputDoB?.text.toString().trim { it <= ' ' }
        val email: String = inputEmail?.text.toString().trim { it <= ' ' }
        val password: String = inputPassword?.text.toString().trim { it <= ' ' }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    showErrorSnackBar(
                        "You are registered successfully. Your user id is ${firebaseUser.uid}",
                        false
                    )

                    val user = User(firebaseUser.uid, name, dateOfBirth, true, email)
                    FireStoreClass().registerUserFS(this, user)

                    auth.signOut()
                    finish()
                } else {
                    showErrorSnackBar(task.exception!!.message.toString(), true)
                }
            }
    }

    fun userRegistrationSuccess() {
        Toast.makeText(
            this,
            "You are registered successfully. Your details are added to Firestore!",
            Toast.LENGTH_LONG
        ).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}