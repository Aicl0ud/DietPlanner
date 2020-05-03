package com.mahidol.dietplanner

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    private val TAG: String = "Main Activity"

    private val introSliderAdapter = IntroSliderAdapter(
        listOf(
            IntroSlide(
                "Mahidol University","This is a wireless project.",R.drawable.img_mahidol
            ),
            IntroSlide("Pitchapa Lapnimitranan","6088074",R.drawable.member_3),
            IntroSlide("Nakorn Santisoontornkul","6088086",R.drawable.member_2),
            IntroSlide("Teerasit Wongpa","6088224",R.drawable.member_1)
        )
    )

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        introSliderViewPager.adapter = introSliderAdapter
        setupIndicators()
        setCurrentIndicator(0)
        introSliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                if (position == 1){
                    window.decorView.setBackgroundColor(Color.parseColor("#f39c12"))
                } else if (position == 2){
                    window.decorView.setBackgroundColor(Color.parseColor("#e74c3c"))
                } else if (position == 3){
                    window.decorView.setBackgroundColor(Color.parseColor("#3498db"))
                } else{
                    window.decorView.setBackgroundColor(Color.parseColor("#087f7b"))
                }
            }
        })

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

//        if (mAuth!!.currentUser != null && !mAuth!!.currentUser!!.isAnonymous){
//            startActivity(Intent(this@MainActivity, ResultActivity::class.java))
//            finish()
//        }

        main_btn_login.setOnClickListener{
            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
        }

//        main_btn_skip.setOnClickListener{
//            mAuth!!.signInAnonymously()
//                .addOnCompleteListener(this){ task ->
//                if(task.isSuccessful){
//                    Log.d(TAG, "signInAnonymously:success")
//                    startActivity(Intent(this@MainActivity, ResultActivity::class.java))
//                    finish()
//                } else {
//                    Toast.makeText(this@MainActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
//                    Log.w(TAG, "signInAnonymously:failure", task.exception)
//                }
//
//            }
//        }

        main_btn_regis.setOnClickListener{startActivity(Intent(this@MainActivity, RegisterActivity::class.java))}

    }

    private fun setupIndicators(){
        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for(i in indicators.indices){
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            indicatorsContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int){
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount){
            val imageView = indicatorsContainer[i] as ImageView
            if (i==index){
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    //
                    )
                )
            } else{
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}
