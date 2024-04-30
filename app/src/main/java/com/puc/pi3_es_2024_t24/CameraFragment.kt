package com.puc.pi3_es_2024_t24

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.puc.pi3_es_2024_t24.databinding.FragmentCameraBinding

class CameraFragment : Fragment() {
    private lateinit var imageCapture: ImageCapture
    private lateinit var camera: Camera
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraProvider:ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector
    private var lensFacing = CameraSelector.LENS_FACING_BACK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCameraPermissions()

    }

    private fun checkCameraPermissions() {
        val cameraPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Camera permission granted", Toast.LENGTH_SHORT).show()
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        //lança as instruções de permissão
        cameraPermissionRequest.launch(Manifest.permission.CAMERA)
    }
private fun startCamera(){
    val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
    cameraProviderFuture.addListener({
        cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                this, cameraSelector, preview)

        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }

    }, ContextCompat.getMainExecutor(requireContext()))
}

}