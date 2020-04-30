package com.mahidol.dietplanner

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_data.*
import java.text.SimpleDateFormat
import java.util.*


class DataActivity : AppCompatActivity() {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    var btbirth: Button? = null
    val c = Calendar.getInstance()
    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)
    private val TAG: String = "Data Activity"
    var simpledateformat: SimpleDateFormat = SimpleDateFormat("dd/mm/yyyy")
    //Today's date
    var date = simpledateformat.format(Calendar.getInstance().time)

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

        btbirth?.setOnClickListener{{
            var datePickerDialog: Date
        }}

        confirm_btn_data.setOnClickListener{
            var gender = "NONE"
            if(male.isChecked){
                gender = male.text.toString()
            } else if(female.isChecked){
                gender = female.text.toString()
            } else {return@setOnClickListener}

            //val gender = data_form_gender.text.toString().trim{it<=' '}
            val age = data_form_age.text.toString().trim{it<=' '}
            val height = data_form_height.text.toString().trim{it<=' '}
            val weight = data_form_weight.text.toString().trim{it<=' '}
            val user = hashMapOf(
                "gender" to gender,
                "age" to age,
                "height" to height,
                "weight" to weight
            )
            //check if empty
            if( age.isEmpty() || height.isEmpty() || weight.isEmpty()){ return@setOnClickListener }

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
