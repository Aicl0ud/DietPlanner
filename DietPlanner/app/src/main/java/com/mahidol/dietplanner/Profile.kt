package com.mahidol.dietplanner

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*


class Profile : AppCompatActivity() {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance() //call firebase
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Data Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
//            val users = firebaseAuth.currentUser
//            if (users==null){
//                startActivity(Intent(this@DataActivity, MainActivity::class.java))
//                finish()
//            }
//        }

        confirm_btn_data.setOnClickListener{
            val username = data_form_username.text.toString().trim{it<=' '}
            val target_weight = data_form_target.text.toString().trim{it<=' '}
            val duration = data_form_duration.text.toString().trim{it<=' '}
            val user = hashMapOf(
                "username" to username,
                "target weight" to target_weight,
                "duration" to duration
            )
            val userID = mAuth!!.currentUser?.uid

            db.collection("users").document(userID.toString())
                .update(user as Map<String, Any>)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            startActivity(Intent(this@Profile, ResultActivity::class.java))
            finish()
        }
    }
}
