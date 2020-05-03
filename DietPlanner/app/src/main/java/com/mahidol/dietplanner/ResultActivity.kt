package com.mahidol.dietplanner

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_result.*
import java.lang.Math.log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class ResultActivity : AppCompatActivity() {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Result Activity"
    //abstract
    private val KEY_USERNAME: String = "username"
    private val KEY_AGE: String = "age"
    private val KEY_GENDER: String = "gender"
    private val KEY_HEIGHT: String = "height"
    private val KEY_WEIGHT: String = "weight"
    private val KEY_TARGET: String = "target weight"
    private val KEY_DURATION: String = "duration"
    private val KEY_foodlist1: String = "foodlist1"
    private val KEY_IMAGE: String = "imageUrl"
    var button_date: Button? = null
    var textview_date: TextView? = null
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        //
        val position = intent.getStringExtra("latitude")
        Toast.makeText(this@ResultActivity, ""+ position,Toast.LENGTH_SHORT).show()

        val test: TextView = findViewById(R.id.txt) as TextView
        test.text = position
        //////////////////////////////////////////
        val user = mAuth!!.currentUser
//        result_text_duration.text = user!!.duration
//        result_text_target.text = user.target_weight

        // get the references from layout file
        textview_date = this.text_view_date_1
        button_date = this.button_date_1
        textview_date!!.text = "--/--/----"

    // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        button_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@ResultActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users==null){
                startActivity(Intent(this@ResultActivity, MainActivity::class.java))
                finish()
            }
        }


        val docRef = db.collection("users").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document  ->
                if (document != null) {
                    username.text = document.getString(KEY_USERNAME)
                    Glide.with(getApplicationContext()).load(document.getString(KEY_IMAGE)).into(imageProfile);

                    if(KEY_foodlist1 != null)
                    {
                        foodlist1.text = document.getString(KEY_foodlist1)
                    }
                    var bmr = 0.0
                    var age = document.getString(KEY_AGE).toString()
                    var weight  = document.getString(KEY_WEIGHT).toString()
                    var height = document.getString(KEY_HEIGHT).toString()
                    var goalweight = document.getString(KEY_TARGET).toString()
                    var duration = document.getString(KEY_DURATION).toString()
                    var foodList1 = document.getString(KEY_foodlist1).toString()

                    if(document.getString(KEY_GENDER).toString() == "male")
                    {
                        bmr = 66 + (13.7 * weight.toDouble()) + (5 * height.toDouble()) - (6.8 * age.toDouble())
                    }
                    else if (document.getString(KEY_GENDER).toString() == "female")
                    {
                        bmr = 665 + (9.6 * weight.toDouble()) + (1.8 * height.toDouble()) - (4.7 * age.toDouble())
                    }
                    var tdee = 0.0
                    var call = 0.0
                    tdee = bmr * 1.2
                    call = tdee - (7700 * (weight.toDouble() - goalweight.toDouble())/duration.toDouble())
                    var show = "%.2f".format(call).toDouble()
                    ShowCal.text = show.toString()


                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }



//        if(user.isAnonymous){
//            result_btn_home.visibility = View.VISIBLE
//            result_btn_logout.visibility = View.INVISIBLE
//        }else{
//            result_btn_home.visibility = View.INVISIBLE
//            result_btn_logout.visibility = View.VISIBLE
//        }
//
//        result_btn_home.setOnClickListener{
//            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
//        }

        //CHECK if anonymous
        if(user.isAnonymous){
            finish()
            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
        }

        test_location.setOnClickListener{
            startActivity(Intent(this@ResultActivity, LocationActivity::class.java))
        }

        result_btn_logout.setOnClickListener{
            mAuth!!.signOut()
            Log.d(TAG, "Signed out!")
            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            finish()
        }

        AddFood.setOnClickListener{
            startActivity(Intent(this@ResultActivity, FoodList::class.java))
        }

        AddEx.setOnClickListener{
            startActivity(Intent(this@ResultActivity, ExerciseList::class.java))
        }
        editprofile.setOnClickListener{
            startActivity(Intent(this@ResultActivity, Profile::class.java))
        }

    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
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
