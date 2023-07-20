package com.android.sample.mpcupload.ui.main

import android.R.layout.simple_spinner_item
import android.R.style.ThemeOverlay_Material_Dialog
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.sample.mpcupload.ui.adapters.PhotoAdapter
import com.android.sample.mpcupload.databinding.ActivityMainBinding
import com.android.sample.mpcupload.model.Photo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var photoAdapter: PhotoAdapter
    val allPhotoList = ArrayList<Photo>()

    val CAMERA_REQUEST_CODE   = 101
    val FINE_LOC_REQUEST_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.toolbar.title = "New Diary"

        viewModel  = ViewModelProvider(this)[MainViewModel::class.java]

        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)
        attachActions()
        subscribeData()

        setupSpinnerTask()
        setupSpinnerArea()
        setupSpinnerEvents()
        setupLauncher()

        setupPhotoAdapter()
    }

    private fun attachActions(){
        binding.includeMain.btnNext.setOnClickListener { viewModel.apiCall() }

        binding.ivCrossWhite.setOnClickListener { finish() }

        binding.includeMain.includePhoto.btnAddPhoto.setOnClickListener {
//            checkFineLocPermission()
            openGallery()
        }

        binding.includeMain.includeDetails.tvDate.setOnClickListener {
            DatePickerDialog(this@MainActivity, ThemeOverlay_Material_Dialog,
                this, 2020,1, 1).show()
        }
    }

    private fun subscribeData(){
        viewModel.isLoading.observe(this, Observer { loading ->
            if (loading){
                binding.includeMain.progLoading.visibility = View.VISIBLE
            } else {
                binding.includeMain.progLoading.visibility = View.GONE
            }
        })
    }

    private fun openGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        launcher.launch(galleryIntent)
    }
    private fun setupLauncher(){
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e("DIALOG", "result.resultCode ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                val intentResult = result.data!!.data

//                val intentResult = result.data!!.extras!!.get("data")
//                allPhotoList.add(Photo(intentResult as Bitmap))

                allPhotoList.add(Photo(intentResult))
                photoAdapter.updateList(allPhotoList)
            }
        }
    }

    private fun checkCameraPermission(){
        val cameraPermission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA)

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            capturePhoto()
        }
    }

    private fun checkFineLocPermission(){
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOC_REQUEST_CODE)
        } else {
            checkCameraPermission()
        }
    }


    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(cameraIntent)
    }

    private fun setupPhotoAdapter(){
        photoAdapter  = PhotoAdapter( allPhotoList){ photoResult ->
            Log.e("Photo", "$photoResult")
            allPhotoList.remove(photoResult)
            photoAdapter.updateList(allPhotoList)

        }
        binding.includeMain.includePhoto.rvPhotos.apply {
            adapter       = photoAdapter
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false)
        }
    }

    private fun setupSpinnerEvents(){
        val spnEvent      = binding.includeMain.includeLink.spnEvent
        val events        = mutableListOf("Select Event","Team Building", "Christmas Party", "Foundation Day")
        val adapterSample = ArrayAdapter( applicationContext, simple_spinner_item, events)

        spnEvent.adapter = adapterSample
        spnEvent.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
            }
        }
    }

    private fun setupSpinnerArea(){
        val spnArea       = binding.includeMain.includeDetails.spnArea
        val areas         = mutableListOf("Select Area","Calabarzon", "NCR", "Central Luzon")
        val adapterSample = ArrayAdapter( applicationContext, simple_spinner_item, areas)

        spnArea.adapter = adapterSample
        spnArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
            }
        }
    }

    private fun setupSpinnerTask(){
        val spnTask        = binding.includeMain.includeDetails.spnTask
        val tasks          = mutableListOf("Task Category","Operations", "Construction")
        val adapterSample  = ArrayAdapter( applicationContext, simple_spinner_item, tasks)

        spnTask.adapter = adapterSample
        spnTask.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val newDate = "$year-$month-$dayOfMonth"
        Log.e("Cal", "$newDate")
        binding.includeMain.includeDetails.tvDate.text = newDate
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FINE_LOC_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e("PERM", "FINE LOC Permission has been denied by user")
                } else {
                    Log.e("PERM", "FINE LOC Permission has been granted by user")
                    checkCameraPermission()
                }
            }
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e("PERM", "CAMERA Permission has been denied by user")
                } else {
                    Log.e("PERM", "CAMERA Permission has been granted by user")
                    capturePhoto()
                }
            }

        }
    }

}