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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignUpActivity : AppCompatActivity(), View.OnClickListener {
    private var mGoogleClient: GoogleSignInClient? = null
    var progressBar: ProgressBar? = null
    var editTextEmail: EditText? = null
    var editTextPassword: TextInputEditText? = null
    var editTextPhone: TextInputEditText? = null
    var editTextName: TextInputEditText? = null
    var editTextEndereco: TextInputEditText? = null
    private var mAuth: FirebaseAuth? = null
    private val RC_GOOGLE_SIGNIN = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        editTextEmail = findViewById<View>(R.id.editTextEmail) as EditText
        editTextPassword = findViewById<View>(R.id.editTextPassword) as TextInputEditText
        editTextPhone = findViewById<View>(R.id.editTextPhone) as TextInputEditText
        editTextEndereco = findViewById<View>(R.id.editTextEndereco) as TextInputEditText
        editTextName = findViewById<View>(R.id.editTextName) as TextInputEditText
        progressBar = findViewById(R.id.progressbar)
        mAuth = FirebaseAuth.getInstance()
        findViewById<View>(R.id.buttonSignUp).setOnClickListener(this)
        findViewById<View>(R.id.textViewLogin).setOnClickListener(this)
        findViewById<View>(R.id.sign_in_button).setOnClickListener(this)
        setupGoogleClient()
        supportActionBar?.hide()

        findViewById<View>(R.id.textView_forgotPass)?.setOnClickListener(View.OnClickListener {
            val intentResetPass = Intent(this, ResetPassActivity::class.java)
            intentResetPass.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intentResetPass)
            finish()
        })
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
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
            concluir_registro(account.email, "googleauth")
            finish()
            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            Toast.makeText(this@SignUpActivity, "Ocorreu algum erro na tentativa de login.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupGoogleClient() {
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) //.requestIdToken(WEB_API_KEY)
                .requestEmail()
                .build()
        mGoogleClient = GoogleSignIn.getClient(this, signInOptions)
        Toast.makeText(this@SignUpActivity, "Verifique seu email para login", Toast.LENGTH_LONG).show()
    }

    fun onGoogleButtonClick() {
        val intent = mGoogleClient!!.signInIntent
        startActivityForResult(intent, RC_GOOGLE_SIGNIN)
    }

    private fun registerUser() {
        val email = editTextEmail!!.text.toString().trim { it <= ' ' }
        val password = editTextPassword!!.text.toString().trim { it <= ' ' }
        val phone = editTextPassword!!.text.toString().trim { it <= ' ' }
        val name = editTextPassword!!.text.toString().trim { it <= ' ' }
        val endereco = editTextPassword!!.text.toString().trim { it <= ' ' }

        if (name.isEmpty()) {
            editTextName!!.error = "Nome é necessário"
            editTextName!!.requestFocus()
            return
        }
        if (endereco.isEmpty()) {
            editTextEndereco!!.error = "Endereço é necessário"
            editTextEndereco!!.requestFocus()
            return
        }
        if (phone.isEmpty()) {
            editTextPhone!!.error = "Número de telefone é necessário"
            editTextPhone!!.requestFocus()
            return
        }
        if (email.isEmpty()) {
            editTextEmail!!.error = "Email é necessário"
            editTextEmail!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail!!.error = "Por favor digite um email válido"
            editTextEmail!!.requestFocus()
            return
        }
        if (password.isEmpty()) {
            editTextPassword!!.error = "Senha é necessária"
            editTextPassword!!.requestFocus()
            return
        }
        if (password.length < 6 || password.length > 15) {
            editTextPassword!!.error = "A senha deve possuir entre 6 e 15 caracteres"
            editTextPassword!!.requestFocus()
            return
        }

        concluir_registro(email, password)
    }

    private fun concluir_registro(email: String?, password: String) {
        progressBar?.visibility = View.VISIBLE
        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            progressBar?.visibility = View.GONE
            if (task.isSuccessful) {
                if (password !== "googleauth") {
                    val phone = editTextPassword!!.text.toString().trim { it <= ' ' }
                    val name = editTextPassword!!.text.toString().trim { it <= ' ' }
                    val endereco = editTextPassword!!.text.toString().trim { it <= ' ' }
                    sendEmailVerification(phone,name,endereco)
                }
            } else {
                if (task.exception is FirebaseAuthUserCollisionException) {
                    editTextPassword!!.setText("")
                    editTextName!!.setText("")
                    editTextEndereco!!.setText("")
                    editTextPhone!!.setText("")
                    Toast.makeText(applicationContext, "Você já está registrado(a)!", Toast.LENGTH_SHORT).show()
                } else {
                    editTextPassword!!.setText("")
                    editTextName!!.setText("")
                    editTextEndereco!!.setText("")
                    editTextPhone!!.setText("")
                    Toast.makeText(this@SignUpActivity, task.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendEmailVerification(phone: String, name: String, endereco: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@SignUpActivity, "Valide seu cadastro através do link enviado pro seu email.", Toast.LENGTH_LONG).show()
                //TODO não está sendo feito nada com o Nome, endereço e celular --> mandar para o banco

                FirebaseAuth.getInstance().signOut()
                editTextEmail!!.setText("")
                editTextPassword!!.setText("")
                editTextName!!.setText("")
                editTextEndereco!!.setText("")
                editTextPhone!!.setText("")
            } else {
                Toast.makeText(this@SignUpActivity, task.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.buttonSignUp -> registerUser()
            R.id.sign_in_button -> onGoogleButtonClick()
            R.id.textViewLogin -> {
                val intentLogin = Intent(this, LoginActivity::class.java)
                intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intentLogin)
                finish()
            }
        }
    }
}