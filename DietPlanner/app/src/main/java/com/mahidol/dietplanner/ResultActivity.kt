package com.mahidol.dietplanner

import android.R.string
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_result.*


class ResultActivity : AppCompatActivity() {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Result Activity"
    //abstract
    private val KEY_GENDER: String = "gender"
    private val KEY_AGE: String = "age"
    private val KEY_HEIGHT: String = "height"
    private val KEY_WEIGHT: String = "weight"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

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

        val docRef = db.collection("users").document(user.uid)
        docRef.get()
            .addOnSuccessListener { document  ->
                if (document?.getString(KEY_AGE) != null && document.getString(KEY_GENDER) != null) {
                    result_text_gender.text = "Gender: "+document.getString(KEY_GENDER)
                    result_text_age.text = "Age: "+document.getString(KEY_AGE)
                    result_text_height.text = "Height: "+document.getString(KEY_HEIGHT)
                    result_text_weight.text = "Weight: "+document.getString(KEY_WEIGHT)

                } else {
                    Log.d(TAG, "No such document")
                    startActivity(Intent(this@ResultActivity, DataActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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
