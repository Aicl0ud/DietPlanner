package com.mahidol.dietplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_food_list.*
import kotlinx.android.synthetic.main.activity_result.*

class FoodList : AppCompatActivity() {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance() //call firebase
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private val TAG: String = "Data Activity"
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)

        foodclose.setOnClickListener {
            startActivity(Intent(this@FoodList, ResultActivity::class.java))
        }

        basil.setOnClickListener{
            val x = "basil"
            val user = hashMapOf(
                "foodlist1" to x

            )
            val userID = mAuth!!.currentUser?.uid

            db.collection("users").document(userID.toString())
                .update(user as Map<String, Any>)
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            startActivity(Intent(this@FoodList, ResultActivity::class.java))
            finish()
        }
    }
}