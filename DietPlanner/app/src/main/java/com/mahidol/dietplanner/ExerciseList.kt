package com.mahidol.dietplanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_exercise_list.*

class ExerciseList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_list)

        exerciseclose.setOnClickListener {
            startActivity(Intent(this@ExerciseList, ResultActivity::class.java))
        }

    }
}
