package com.example.pizzacodeapp

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth

class ResetPassActivity : AppCompatActivity(), View.OnClickListener {

    private var mAuth: FirebaseAuth? = null
    var editTextEmail: EditText? = null
    var progressBar: ProgressBar? = null
    private var mEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)
        editTextEmail = findViewById<View>(R.id.editTextEmail) as EditText
        progressBar = findViewById(R.id.progressbar)
        mAuth = FirebaseAuth.getInstance()
        findViewById<View>(R.id.buttonReset).setOnClickListener(this)
        findViewById<View>(R.id.textViewBack).setOnClickListener(this)
        supportActionBar?.hide()
    }

    private fun passReset() {
        mEmail = editTextEmail!!.text.toString().trim { it <= ' ' }
        if (mEmail!!.isEmpty()) {
            editTextEmail!!.error = "Email is required"
            editTextEmail!!.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            editTextEmail!!.error = "Please enter a valid email"
            editTextEmail!!.requestFocus()
            return
        }
        progressBar?.visibility = View.VISIBLE
        FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail).addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                editTextEmail!!.setText("")
                progressBar?.visibility = View.GONE
                Toast.makeText(this@ResetPassActivity, "Password reset link sent to your email. Please check your email inbox.", Toast.LENGTH_SHORT).show()
            } else {
                progressBar!!.visibility = View.GONE
                Toast.makeText(this@ResetPassActivity, task.exception!!.message, Toast.LENGTH_SHORT).show()
                return@OnCompleteListener
            }
        })
                .addOnFailureListener(OnFailureListener { e ->
                    progressBar!!.visibility = View.GONE
                    Toast.makeText(this@ResetPassActivity, e.message, Toast.LENGTH_SHORT).show()
                    return@OnFailureListener
                })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.buttonReset -> passReset()
            R.id.textViewBack -> {
                val intentLogin = Intent(this, LoginActivity::class.java)
                intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intentLogin)
                finish()
            }
        }
    }
}