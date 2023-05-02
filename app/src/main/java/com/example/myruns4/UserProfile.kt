package com.example.myruns4

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class UserProfile : AppCompatActivity() {
    private val NAME_KEY = "NAME_KEY"
    private val EMAIL_KEY = "EMAIL_KEY"
    private val PHONE_KEY = "PHONE_KEY"
    private val RADIO_KEY = "RADIO_KEY"
    private val CLASS_KEY = "CLASS_KEY"
    private val MAJOR_KEY = "MAJOR_KEY"
    private val SHARED_PREFS = "sharedPrefs"
    private val toast = "Saved"


    private lateinit var changeDialog: ChangeDialog
    private lateinit var imageUri: Uri
    private lateinit var ImgUri: Uri
    private lateinit var temp_ImgUri: Uri
    private lateinit var ImgFile: File
    private lateinit var temp_ImgFile: File
    private val tempImg = "temp.jpg"
    private val camImg = "cam_image.jpg"
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<Intent>
    private lateinit var profile_image: ImageView
    private val authority = "com.example.myruns4"
    private lateinit var myViewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userprofile)
        profile_image = findViewById(R.id.profileImage)

        Util.checkPermissions(this)

        //File Provider, View Model and LiveData learned and implemented From Professor Xing-Dong Yang lab tutorial

        temp_ImgFile = File(this.cacheDir,tempImg)
        temp_ImgUri = FileProvider.getUriForFile(this,authority, temp_ImgFile)

        ImgFile = File(getExternalFilesDir(null), camImg)
        ImgUri = FileProvider.getUriForFile(this, authority, ImgFile)



        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                changeDialog.dismiss()
                val bitmap = Util.getBitmap(this, temp_ImgUri)
                myViewModel.profileImage.value = bitmap
            }
        }


        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data!!
                changeDialog.dismiss()
                val bitmap = Util.getBitmap(this, imageUri)

                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos)
                val bitmapdata = bos.toByteArray()

                //write the bytes in file
                var fos = FileOutputStream(temp_ImgFile)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()

                myViewModel.profileImage.value = bitmap
            }
        }

        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        myViewModel.profileImage.observe(this, { it ->
            profile_image.setImageBitmap(it)
        })

        loadProfile()

    }

    fun saveProfile(view: View){
        val editName:EditText = findViewById(R.id.edit_Name)
        val newName = editName.text.toString()
        val editEmail:EditText = findViewById(R.id.edit_Email)
        val newEmail = editEmail.text.toString()
        val editPhone:EditText = findViewById(R.id.edit_Phone)
        val newPhone = editPhone.text.toString()
        val radioGroup:RadioGroup = findViewById(R.id.Radio_buttons)
        val selected: Int = radioGroup.getCheckedRadioButtonId()
        val editClass:EditText = findViewById(R.id.edit_Class)
        val newClass = editClass.text.toString()
        val editMajor:EditText = findViewById(R.id.edit_Major)
        val newMajor = editMajor.text.toString()

        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.apply{
            putString(NAME_KEY,newName)
            putString(EMAIL_KEY,newEmail)
            putString(PHONE_KEY,newPhone)
            putInt(RADIO_KEY,selected)
            putString(CLASS_KEY,newClass)
            putString(MAJOR_KEY,newMajor)
        }.apply()

        if (temp_ImgFile.exists()) {
            temp_ImgFile.copyTo(ImgFile,true)
        }

        Toast.makeText(this,toast,Toast.LENGTH_SHORT).show()
        finish()
        System.out.close()
    }

    fun loadProfile(){
        val sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        val savedName = sharedPreferences.getString(NAME_KEY,null)
        val savedEmail = sharedPreferences.getString(EMAIL_KEY,null)
        val savedPhone = sharedPreferences.getString(PHONE_KEY,null)
        val savedGender = sharedPreferences.getInt(RADIO_KEY,0)
        val savedClass = sharedPreferences.getString(CLASS_KEY,null)
        val savedMajor = sharedPreferences.getString(MAJOR_KEY,null)

        val editName:EditText = findViewById(R.id.edit_Name)
        editName.setText(savedName)
        val editEmail:EditText = findViewById(R.id.edit_Email)
        editEmail.setText(savedEmail)
        val editPhone:EditText = findViewById(R.id.edit_Phone)
        editPhone.setText(savedPhone)
        val radioGroup:RadioGroup = findViewById(R.id.Radio_buttons)
        radioGroup.check(savedGender)
        val editClass:EditText = findViewById(R.id.edit_Class)
        editClass.setText(savedClass)
        val editMajor:EditText = findViewById(R.id.edit_Major)
        editMajor.setText(savedMajor)

        if (ImgFile.exists()) {
            val bitmap = Util.getBitmap(this, ImgUri)
            profile_image.setImageBitmap(bitmap)
        }

    }

    fun exitApp(view: View) {
        finish()
        System.out.close()
    }

    fun onChangePhotoClicked(view: View) {

            changeDialog = ChangeDialog()
            changeDialog.show(supportFragmentManager,"tag")

    }

    fun OpenCamera(view: View) {

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            Util.checkPermissions(this)
        }
        else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, temp_ImgUri)
            cameraResult.launch(intent)
        }
    }

    fun SelectFromGallery(view: View) {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            Util.checkPermissions(this)
        }
        else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryResult.launch(intent)
        }
    }


}