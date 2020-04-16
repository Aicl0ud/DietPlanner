package com.mahidol.dietplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Register Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth!!.currentUser != null && !mAuth!!.currentUser!!.isAnonymous){
            startActivity(Intent(this@RegisterActivity, ResultActivity::class.java))
            finish()
        }

        register_btn_register.setOnClickListener{
            val email = register_form_email.text.toString().trim{it<=' '}
            val password = register_form_password.text.toString().trim{it<=' '}
            val password2 = register_form_password2.text.toString().trim{it<=' '}

            //CHECK
            if (email.isEmpty()){
                Log.d(TAG, "Email was empty")
                return@setOnClickListener
            }
            if (password.isEmpty()){
                Log.d(TAG, "Password was empty")
                return@setOnClickListener
            }
            if (password2.isEmpty()){
                Log.d(TAG, "Re-Password was empty")
                return@setOnClickListener
            }
            if (password.length<6){
                register_form_password.error = "Please check your password. Password must more than 6 characters."
                Log.d(TAG, "Enter password less than 6 characters.")
            } else if (password2.length<6) {
                register_form_password2.error = "Please check your password. Password must more than 6 characters."
                Log.d(TAG, "Enter password less than 6 characters.")
            }else if (password != password2){
                Toast.makeText(this@RegisterActivity, "Authentication failed: Passwords was not matched", Toast.LENGTH_SHORT).show()
            } else{
                mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if(!task.isSuccessful){
                        Log.d(TAG, "Authentication Failed: " + task.exception)
                    } else{
                        Log.d(TAG, "Create account successfully!")
                        startActivity(Intent(this@RegisterActivity, ResultActivity::class.java))
                        finish()
                    }
                }
            }
        }

        register_imgview_close.setOnClickListener { finish() }
    }
}
