package com.mahidol.dietplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_main.*

class AuthActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Login Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        //Check login
        if (mAuth!!.currentUser != null && !mAuth!!.currentUser!!.isAnonymous){
            startActivity(Intent(this@AuthActivity, ResultActivity::class.java))
            finish()
        }

        auth_btn_login.setOnClickListener{
            val email = auth_form_email.text.toString().trim{it<=' '}
            val password = auth_form_password.text.toString().trim{it<=' '}

            if (email.isEmpty()){
                Log.d(TAG, "Email was empty")
                return@setOnClickListener
            }
            if (password.isEmpty()){
                Log.d(TAG, "Password was empty")
                return@setOnClickListener
            }

            mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if(!task.isSuccessful){
                    if (password.length<6){
                        auth_form_password.error = "Please check your password. Password must more than 6 characters."
                        Log.d(TAG, "Enter password less than 6 characters.")
                    } else{
                        Log.d(TAG, "Authentication Failed: " + task.exception)
                    }
                } else{
                    Log.d(TAG, "Sign in successfully!")
                    startActivity(Intent(this@AuthActivity, ResultActivity::class.java))
                    finish()
                }
            }
        }

        auth_imgview_close.setOnClickListener { finish() }
        auth_btn_register.setOnClickListener{
            startActivity(Intent(this@AuthActivity, RegisterActivity::class.java))
            finish()
        }
    }
}
