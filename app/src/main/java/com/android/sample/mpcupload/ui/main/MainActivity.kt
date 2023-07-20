package com.android.sample.mpcupload.ui.main

import android.R.layout.simple_spinner_item
import android.R.style.ThemeOverlay_Material_Dialog
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
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
import com.android.sample.mpcupload.ui.camera.CameraActivity
import dagger.hilt.android.AndroidEntryPoint

// entry point activity annotation for hilt
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    // launcher for catching results
    lateinit var launcher: ActivityResultLauncher<Intent>
    // globalized photo adapter foe updating and instantiation
    lateinit var photoAdapter: PhotoAdapter

    // photolist storage
    val allPhotoList = ArrayList<Photo>()

    // request code when checking permissions
    val CAMERA_REQUEST_CODE   = 101
    val FINE_LOC_REQUEST_CODE = 102

    // spinner storage
    var selectedArea = ""
    var selectedTask = ""
    var selectedEvent = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup view binding, view model and action bar
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.toolbar.title = "New Diary"
        viewModel  = ViewModelProvider(this)[MainViewModel::class.java]
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        // listeners and observers
        attachActions()
        subscribeData()

        // configure setup
        setupSpinnerTask()
        setupSpinnerArea()
        setupSpinnerEvents()
        setupLauncher()
        setupPhotoAdapter()
    }

    // handle actions by user
    private fun attachActions(){
        // handle next process
        binding.includeMain.btnNext.setOnClickListener { checkData() }

        // close app
        binding.ivCrossWhite.setOnClickListener { finish() }

        // capture from camera API or get from gallery or go to camera2 API page
        binding.includeMain.includePhoto.btnAddPhoto.setOnClickListener {
          // old camera API
            checkFineLocPermission()

          // option to TAKE from gallery photos
//            openGallery()

          // camera activity with Camera2 only works on certain devices (but no mine)
//            goToCamera()
        }

        // open date picker dialog, material themed
        binding.includeMain.includeDetails.tvDate.setOnClickListener {
            DatePickerDialog(this@MainActivity, ThemeOverlay_Material_Dialog,
                this, 2020,1, 1).show()
        }
    }

    // observe live datas
    private fun subscribeData(){
        // display loading progress for api
        viewModel.isLoading.observe(this, Observer { loading ->
            if (loading){ binding.includeMain.progLoading.visibility = View.VISIBLE
            } else { binding.includeMain.progLoading.visibility = View.GONE }
        })
    }

    // transfer to camera2 api page
    private fun goToCamera(){
        val userIntent = Intent(this@MainActivity , CameraActivity::class.java)
        startActivity(userIntent)
    }

    // check inputs for simple validation
    private fun checkData(){
        var isValid = true

        if (binding.includeMain.includeComments.etComments.text.toString() == ""){ isValid = false }
//
        if (binding.includeMain.includeDetails.etTags.text.toString() == ""){ isValid = false }
//
        if (binding.includeMain.includeDetails.tvDate.text.toString() == "Select Date"){ isValid = false }

        // display toast for validation information or call api if valid
//        if (isValid){ viewModel.apiGetCall()
        if (isValid){
            viewModel.apiPostCall(
                selectedEvent,
                selectedTask,
                selectedArea,
                binding.includeMain.includeComments.etComments.text.toString(),
                binding.includeMain.includeDetails.etTags.text.toString(),
                binding.includeMain.includeDetails.tvDate.text.toString(),
                allPhotoList)
        } else { Toast.makeText(this@MainActivity, "Please complete details", Toast.LENGTH_LONG).show() }
    }

    // choose between photos and gallery files
    private fun openGallery(){
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        launcher.launch(galleryIntent)
    }

    // handle old camera api or galler results
    private fun setupLauncher(){
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            Log.e("DIALOG", "result.resultCode ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                // for old camera api
                val intentResult = result.data!!.extras!!.get("data")
                allPhotoList.add(Photo(intentResult as Bitmap))

                // for gallery data result
//                val intentResult = result.data!!.data
//                allPhotoList.add(Photo(intentResult))

                // update photo recycler
                photoAdapter.updateList(allPhotoList)
            }
        }
    }

    // check camera permission if granted
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

    // check access fine location permission if granted
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


    // open old camera api
    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        launcher.launch(cameraIntent)
    }


    // setup photo adapter for recycler
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

    // setup data for events spinner
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
                selectedEvent = type
            }
        }
    }

    // setup data for area spinner
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
                selectedArea = type
            }
        }
    }

    // setup data for task spinner
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
                selectedTask = type
            }
        }
    }

    // handle date set listener feedback
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val newDate = "$year-$month-$dayOfMonth"
        Log.e("Cal", "$newDate")
        binding.includeMain.includeDetails.tvDate.text = newDate
    }

    // handle result from requesting permissions
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