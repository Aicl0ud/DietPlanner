package com.mahidol.dietplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Result Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        val user = mAuth!!.currentUser

        result_text_email.text = user!!.email
        result_text_uid.text = user.uid

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users==null){
                startActivity(Intent(this@ResultActivity, MainActivity::class.java))
                finish()
            }
        }

        if(user.isAnonymous){
            result_btn_home.visibility = View.VISIBLE
            result_btn_logout.visibility = View.INVISIBLE
        }else{
            result_btn_home.visibility = View.INVISIBLE
            result_btn_logout.visibility = View.VISIBLE
        }

        result_btn_home.setOnClickListener{
            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
        }
        result_btn_logout.setOnClickListener{
            mAuth!!.signOut()
            Log.d(TAG, "Signed out!")
            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener { mAuthListener }
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) { mAuth!!.removeAuthStateListener { mAuthListener }}
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK){moveTaskToBack(true)}
        return super.onKeyDown(keyCode, event)
    }
}
