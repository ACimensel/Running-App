package com.example.artun_cimensel_myruns4.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.Util
import java.io.File

class UserProfileActivity : AppCompatActivity() {
    companion object {
        private const val KEY_NAME: String = "_name"
        private const val KEY_EMAIL: String = "_email"
        private const val KEY_PHONE: String = "_phone"
        private const val KEY_GENDER_ID: String = "_genderID"
        private const val KEY_CLASS: String = "_class"
        private const val KEY_MAJOR: String = "_major"
        private const val KEY_IMAGE_URI: String = "_imageURI"
    }


    private var tookSuccessfulPicture: Boolean = false
    private lateinit var sPrefs: SharedPreferences // var is mutable, val is immutable
    private lateinit var profilePic: ImageView
    private lateinit var pathToTempImg: File
    private lateinit var pathToProfilePic: File
    private lateinit var tempImgURI: Uri
    private lateinit var permImgURI: Uri
    private var tempGalleryImgURI: Uri? = null
    private var permGalleryImgURI: Uri? = null
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        assignLateInitVars()
        if(savedInstanceState == null)  loadProfile()
        else restoreSavedInstance(savedInstanceState)

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK) {
                profilePic.setImageBitmap(Util.getBitmap(this, tempImgURI))
                tookSuccessfulPicture = true
                tempGalleryImgURI = null
            }
        }

        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK && result.data != null) {
                tempGalleryImgURI = result.data!!.data!!
                profilePic.setImageBitmap(Util.getBitmap(this, tempGalleryImgURI!!))
                tookSuccessfulPicture = false
            }
        }
    }

    private fun assignLateInitVars() {
        profilePic = findViewById(R.id.img_profilePic)
        sPrefs = applicationContext.getSharedPreferences("UserProfilePrefs", MODE_PRIVATE)

        // Create paths to both a temp image and a profile picture
        pathToTempImg = File(getExternalFilesDir(null), "temp_img.jpg")
        pathToProfilePic = File(getExternalFilesDir(null), "profile_pic.jpg")

        // Get a URI for the path to both images
        tempImgURI = FileProvider.getUriForFile(this, "com.example.artun_cimensel_myruns4", pathToTempImg)
        permImgURI = FileProvider.getUriForFile(this, "com.example.artun_cimensel_myruns4", pathToProfilePic)
    }

    private fun loadProfile() {
        // If a profile picture exists, load it in the profile pic ImageView
        if(pathToProfilePic.exists())
            profilePic.setImageBitmap(Util.getBitmap(this, permImgURI))

        // load name
        val nameField = findViewById<EditText>(R.id.editText_name)
        nameField.setText(sPrefs.getString(KEY_NAME, ""))

        // load email
        val emailField = findViewById<EditText>(R.id.editText_email)
        emailField.setText(sPrefs.getString(KEY_EMAIL, ""))

        // load phone
        val phoneField = findViewById<EditText>(R.id.editText_phone)
        phoneField.setText(sPrefs.getString(KEY_PHONE, ""))

        // load gender radio button selection
        val genderGroup = findViewById<RadioGroup>(R.id.radioGroup_gender)
        when (sPrefs.getInt(KEY_GENDER_ID, -1)) {
            R.id.radioBut_female -> genderGroup.check(R.id.radioBut_female)
            R.id.radioBut_male -> genderGroup.check(R.id.radioBut_male)
            else -> genderGroup.clearCheck()
        }

        // load class
        val classField = findViewById<EditText>(R.id.editText_class)
        val classVal = sPrefs.getInt(KEY_CLASS, -1)
        if(classVal == -1)  classField.setText("")
        else                classField.setText(classVal.toString())

        // load major
        val majorField = findViewById<EditText>(R.id.editText_major)
        majorField.setText(sPrefs.getString(KEY_MAJOR, ""))
    }

    private fun restoreSavedInstance(savedInstanceState: Bundle) {
        tookSuccessfulPicture = savedInstanceState.getBoolean("camera_success", false)
        tempGalleryImgURI = savedInstanceState.getString("tempGalleryURI", null)?.toUri()

        // If a temp image exists, load it in the profile pic ImageView, otherwise load prof pic
        if (tempGalleryImgURI != null)
            profilePic.setImageBitmap(Util.getBitmap(this, tempGalleryImgURI!!))
        else if (tookSuccessfulPicture && pathToTempImg.exists())
            profilePic.setImageBitmap(Util.getBitmap(this, tempImgURI))
        else if (pathToProfilePic.exists())
            profilePic.setImageBitmap(Util.getBitmap(this, permImgURI))

        // load name
        val nameField = findViewById<EditText>(R.id.editText_name)
        nameField.setText(savedInstanceState.getString(KEY_NAME, ""))

        // load email
        val emailField = findViewById<EditText>(R.id.editText_email)
        emailField.setText(savedInstanceState.getString(KEY_EMAIL, ""))

        // load phone
        val phoneField = findViewById<EditText>(R.id.editText_phone)
        phoneField.setText(savedInstanceState.getString(KEY_PHONE, ""))

        // load gender radio button selection
        val genderGroup = findViewById<RadioGroup>(R.id.radioGroup_gender)
        when (savedInstanceState.getInt(KEY_GENDER_ID, -1)) {
            R.id.radioBut_female -> genderGroup.check(R.id.radioBut_female)
            R.id.radioBut_male -> genderGroup.check(R.id.radioBut_male)
            else -> genderGroup.clearCheck()
        }

        // load class
        val classField = findViewById<EditText>(R.id.editText_class)
        val classVal = savedInstanceState.getInt(KEY_CLASS, -1)
        if (classVal == -1) classField.setText("")
        else classField.setText(classVal.toString())

        // load major
        val majorField = findViewById<EditText>(R.id.editText_major)
        majorField.setText(savedInstanceState.getString(KEY_MAJOR, ""))
    }

    fun onChangePhotoClicked(view: View) {
        val pictureRetrievalTypes = arrayOf("Open Camera", "Select from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Profile Picture").setItems(pictureRetrievalTypes){ _, position ->
            when (position) {
                0 -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgURI)
                    cameraResult.launch(intent)
                }
                1 -> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    galleryResult.launch(intent)
                }
            }
        }.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if(tempGalleryImgURI != null) outState.putString("tempGalleryURI", tempGalleryImgURI.toString())
        outState.putBoolean("camera_success", tookSuccessfulPicture)

        outState.putString(KEY_NAME, findViewById<EditText>(R.id.editText_name).text.toString())
        outState.putString(KEY_EMAIL, findViewById<EditText>(R.id.editText_email).text.toString())
        outState.putString(KEY_PHONE, findViewById<EditText>(R.id.editText_phone).text.toString())
        outState.putInt(KEY_GENDER_ID, findViewById<RadioGroup>(R.id.radioGroup_gender).checkedRadioButtonId)
        outState.putString(KEY_MAJOR, findViewById<EditText>(R.id.editText_major).text.toString())

        val strClass = findViewById<EditText>(R.id.editText_class).text.toString()
        if(strClass != "") outState.putInt(KEY_CLASS, Integer.parseInt(strClass))
    }

    fun saveProfile(view: View) {
        if(tookSuccessfulPicture && pathToTempImg.exists())
            pathToTempImg.renameTo(pathToProfilePic)

        val editor: SharedPreferences.Editor = sPrefs.edit()

        editor.putString(KEY_NAME, findViewById<EditText>(R.id.editText_name).text.toString())
        editor.putString(KEY_EMAIL, findViewById<EditText>(R.id.editText_email).text.toString())
        editor.putString(KEY_PHONE, findViewById<EditText>(R.id.editText_phone).text.toString())
        editor.putInt(KEY_GENDER_ID, findViewById<RadioGroup>(R.id.radioGroup_gender).checkedRadioButtonId)
        editor.putString(KEY_MAJOR, findViewById<EditText>(R.id.editText_major).text.toString())

        val strClass = findViewById<EditText>(R.id.editText_class).text.toString()
        if(strClass != "") editor.putInt(KEY_CLASS, Integer.parseInt(strClass))

        editor.apply()
        Toast.makeText(applicationContext, "Saved", Toast.LENGTH_LONG).show()

        finish()
    }

    fun onClickCancel(view: View) {
        finish()
    }
}