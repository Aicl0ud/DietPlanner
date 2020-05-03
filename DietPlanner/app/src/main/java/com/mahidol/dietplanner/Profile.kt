package com.mahidol.dietplanner

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.IOException
import java.util.*


class Profile : AppCompatActivity() {

    var db: FirebaseFirestore = FirebaseFirestore.getInstance() //call firebase
    var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val TAG: String = "Data Activity"

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private val KEY_IMAGE: String = "imageUrl"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val user = mAuth!!.currentUser
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val users = firebaseAuth.currentUser
            if (users==null){
                startActivity(Intent(this@Profile, MainActivity::class.java))
                finish()
            }
        }

        firebaseStore = FirebaseStorage.getInstance()

        storageReference = FirebaseStorage.getInstance().reference
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


        val docRef = db.collection("users").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Glide.with(getApplicationContext()).load(document.getString(KEY_IMAGE))
                        .into(imageProfile);

                }
            }

        btn_choose_image.setOnClickListener {
            launchGallery() }
        btn_upload_image.setOnClickListener {
            uploadImage() }
    }


    private fun launchGallery() {

        val intent = Intent()

        intent.type = "image/*"

        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)

                imageProfile.setImageBitmap(bitmap)

            } catch (e: IOException) {

                e.printStackTrace()
            }
        }

    }



    private fun addUploadRecordToDb(uri: String){
        val userID = mAuth!!.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val data = HashMap<String, Any>()
        data["imageUrl"] = uri
        db.collection("users")
            .document(userID.toString())
            .update(data)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Saved to DB", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
    }

    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)
            val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    addUploadRecordToDb(downloadUri.toString())

                } else {
                    // Handle failures
                }
            }?.addOnFailureListener{
            }

        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }




}
