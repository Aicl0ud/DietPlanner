package com.mahidol.dietplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_data.*


class DataActivity : AppCompatActivity() {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Data Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users==null){
                startActivity(Intent(this@DataActivity, MainActivity::class.java))
                finish()
            }
        }

        confirm_btn_data.setOnClickListener{
            val gender = data_form_gender.text.toString().trim{it<=' '}
            val age = data_form_age.text.toString().trim{it<=' '}
            val height = data_form_height.text.toString().trim{it<=' '}
            val weight = data_form_weight.text.toString().trim{it<=' '}
            val user = hashMapOf(
                "gender" to gender,
                "age" to age,
                "height" to height,
                "weight" to weight
            )
            val userID = mAuth!!.currentUser?.uid

            db.collection("users").document(userID.toString())
                .set(user as Map<String, Any>)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            startActivity(Intent(this@DataActivity, ResultActivity::class.java))
            finish()
        }
    }
}
