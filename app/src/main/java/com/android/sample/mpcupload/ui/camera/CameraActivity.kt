package com.android.sample.mpcupload.ui.camera

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.sample.mpcupload.databinding.ActivityCameraBinding

// Page for Camera2 API
class CameraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCameraBinding

    // declare camera variables
    private val CAMERA_REQUEST_CODE = 101
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var cameraDevice: CameraDevice
    private lateinit var captureRequest: CaptureRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setup view binding, view model and action bar
        binding = ActivityCameraBinding.inflate(layoutInflater)
        binding.toolbar.title = "Camera"
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        attachActions()
        setupTexture()
    }

    // get camera manager and add texture listener
    private fun setupTexture(){
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

        binding.textureCamera.surfaceTextureListener = object: TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

            }

        }
    }

    // open camera2 api
    private fun openCamera(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@CameraActivity,
                arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            return
        } else {
            cameraManager.openCamera(cameraManager.cameraIdList[0], object:CameraDevice.StateCallback(){
                override fun onOpened(cam: CameraDevice) {
                    cameraDevice = cam

                    val surfaceTexture = binding.textureCamera.surfaceTexture
                    val surface        = Surface(surfaceTexture)

                    val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    captureRequest.addTarget(surface)

                }

                override fun onDisconnected(camera: CameraDevice) {

                }

                override fun onError(camera: CameraDevice, error: Int) {

                }

            }, Handler(Looper.getMainLooper()))
        }
    }


    // handle actions done by users
    private fun attachActions(){
        binding.ivCrossWhite.setOnClickListener { finish() }
    }

    // handle result from requesting permissions
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.e("PERM", "CAMERA Permission has been denied by user")
                } else {
                    Log.e("PERM", "CAMERA Permission has been granted by user")
                    openCamera()
                }
            }
        }
    }
}