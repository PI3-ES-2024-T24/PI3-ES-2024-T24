package com.puc.pi3_es_2024_t24.utils

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.puc.pi3_es_2024_t24.databinding.FragmentCameraBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var imageCapture: ImageCapture
    private var savedUri: Uri? = null
    private var savedUri1: Uri? = null
    private var clicked: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = CameraFragmentArgs.fromBundle(requireArguments())
        val argAccess = args.accessNum
        cameraExecutor = Executors.newSingleThreadExecutor()
        barcodeScanner = BarcodeScanning.getClient()
        checkCameraPermissions()

        if (argAccess > 0) {
            binding.btnTakePhoto.show()
        } else {
            binding.btnTakePhoto.hide()
        }

        binding.btnTakePhoto.setOnClickListener {
            if (argAccess == 1) {
                takePhoto { uri ->
                    savedUri = uri
                    val action = CameraFragmentDirections.actionCameraFragmentToClientPhotoFragment(uri.toString())
                    findNavController().navigate(action)
                    Toast.makeText(requireContext(), "Photo taken", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (!clicked) {
                    takePhoto { uri ->
                        savedUri1 = uri
                        clicked = true
                        Toast.makeText(requireContext(), "Por favor tire foto do outro cliente", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    takePhoto { uri ->
                        clicked = false
                        val action = CameraFragmentDirections.actionCameraFragmentToClientPhotoFragment(uri.toString(), savedUri1.toString())
                        findNavController().navigate(action)
                    }
                }
            }
        }
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

        cameraPermissionRequest.launch(Manifest.permission.CAMERA)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            imageCapture = ImageCapture.Builder().build()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        scanBarcode(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto(onImageSaved: (Uri) -> Unit) {
        val photoFile = File(requireContext().externalMediaDirs.firstOrNull(), "FOTO_JPEG${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val uri = Uri.fromFile(photoFile)
                    onImageSaved(uri)
                    Toast.makeText(requireContext(), "Photo saved: $uri", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                    Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show()
                }
            })
    }

    @OptIn(ExperimentalGetImage::class)
    private fun scanBarcode(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val qrCodeValue = barcode.rawValue
                        Log.d(TAG, "Barcode value: $qrCodeValue")
                        navigationQrCode(qrCodeValue)
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Barcode scanning failed", e)
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun navigationQrCode(qr: String?) {
        val action = CameraFragmentDirections.actionCameraFragmentToQrCodeReadFragment(qrCodeInfo = qr)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraFragment"
    }
}