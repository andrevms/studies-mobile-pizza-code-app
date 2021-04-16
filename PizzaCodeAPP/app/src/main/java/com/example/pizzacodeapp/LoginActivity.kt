package com.example.pizzacodeapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*

class LoginActivity : AppCompatActivity() {

    private var mGoogleClient: GoogleSignInClient? = null
    private var mAuth: FirebaseAuth? = null
    var editTextEmail: EditText? = null
    var editTextPassword: TextInputEditText? = null
    var progressBar: ProgressBar? = null
    private val RC_GOOGLE_SIGNIN = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editTextEmail = findViewById<View>(R.id.editTextEmail) as EditText
        editTextPassword = findViewById<View>(R.id.editTextPassword) as TextInputEditText
        progressBar = findViewById(R.id.progressbar)
        mAuth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        findViewById<View>(R.id.textViewSignup)?.setOnClickListener(View.OnClickListener {
            val intentSignup = Intent(this, SignUpActivity::class.java)
            intentSignup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentSignup)
            finish()
        })

        findViewById<View>(R.id.buttonSignUp)?.setOnClickListener(View.OnClickListener {
            userLogin()
        })

        findViewById<View>(R.id.textView_forgotPass)?.setOnClickListener(View.OnClickListener {
            val intentResetPass = Intent(this, ResetPassActivity::class.java)
            intentResetPass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentResetPass)
            finish()
        })

        findViewById<View>(R.id.sign_in_button)?.setOnClickListener(View.OnClickListener {
            onGoogleButtonClick()
        })

        setupGoogleClient()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGNIN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            //updateUI(account);
            firebaseAuthWithGoogle(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI(null)
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct!!.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = mAuth!!.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LoginActivity, "Erro na autenticação!", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            finish()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            Toast.makeText(
                this@LoginActivity,
                "Ocorreu algum erro na tentativa de login.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupGoogleClient() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleClient = GoogleSignIn.getClient(this, signInOptions)
    }

    fun onGoogleButtonClick() {
        val intent = mGoogleClient!!.signInIntent
        startActivityForResult(intent, RC_GOOGLE_SIGNIN)
    }

    private fun userLogin() {
        val email = editTextEmail!!.text.toString().trim { it <= ' ' }
        val password = editTextPassword!!.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            editTextEmail!!.error = "Preencha seu email"
            editTextEmail!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = "Preencha um email válido"
            editTextEmail!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editTextPassword!!.error = "Digite uma senha"
            editTextPassword!!.requestFocus()
            return
        }
        if (password.length < 6 || password.length > 15) {
            editTextPassword!!.error = "A senha deve possuir entre 6-15 caracteres"
            editTextPassword!!.requestFocus()
            return
        }
        progressBar?.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progressBar?.visibility = View.GONE
            if (task.isSuccessful) {
                if (mAuth!!.currentUser.isEmailVerified) {
                    finish()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    editTextPassword!!.setText("")
                    Toast.makeText(
                        this@LoginActivity,
                        "Verifique seu email para login",
                        Toast.LENGTH_LONG
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                }
            } else {
                Toast.makeText(applicationContext, task.exception!!.message, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (mAuth!!.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}