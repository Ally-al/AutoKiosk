package com.example.autokiosk.presentation.barcode.view

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.autokiosk.R
import com.example.autokiosk.databinding.DialogProductFoundBinding
import com.example.autokiosk.databinding.FragmentBarcodeScannerBinding
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.presentation.barcode.viewmodel.BarcodeScannerViewModel
import com.example.autokiosk.presentation.cart.viewmodel.CartViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class BarcodeScannerFragment : Fragment(R.layout.fragment_barcode_scanner) {

    private val viewModel: BarcodeScannerViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var binding: FragmentBarcodeScannerBinding
    private lateinit var cameraExecutor: ExecutorService
    private var isScanning = false
    private var isDialogShown = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Камера не доступна", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBarcodeScannerBinding.bind(view)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        observeViewModel()

        binding.btnManualInput.setOnClickListener {
            showManualInputDialog()
        }
    }

    private fun showManualInputDialog() {
        val editText = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Введите код"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Ввод кода товара")
            .setView(editText)
            .setPositiveButton("Найти товар") { dialog, _ ->
                val barcode = editText.text.toString().trim()
                if (barcode.isNotEmpty()) {
                    isScanning = true
                    viewModel.searchProductByBarcode(barcode)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка запуска камеры", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processImageProxy(imageProxy: ImageProxy) {
        if (isScanning) {
            imageProxy.close()
            return
        }

        try {
            val yBuffer = imageProxy.planes[0].buffer
            val uBuffer = imageProxy.planes[1].buffer
            val vBuffer = imageProxy.planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()
            val nv21 = ByteArray(ySize + uSize + vSize)

            yBuffer.get(nv21, 0, ySize)
            uBuffer.get(nv21, ySize, uSize)
            vBuffer.get(nv21, ySize + uSize, vSize)

            val inputImage = try {
                InputImage.fromByteArray(
                    nv21,
                    imageProxy.width,
                    imageProxy.height,
                    imageProxy.imageInfo.rotationDegrees,
                    InputImage.IMAGE_FORMAT_NV21
                )
            } catch (e: IllegalArgumentException) {
                imageProxy.close()
                return
            }

            val scanner: BarcodeScanner = BarcodeScanning.getClient()

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val barcode = barcodes[0]
                        barcode.displayValue?.let { barcodeValue ->
                            isScanning = true
                            viewModel.searchProductByBarcode(barcodeValue)
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } catch (e: Exception) {
            imageProxy.close()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.productFlow.collect { product ->
                if (!isDialogShown) {
                    isDialogShown = true
                    if (product != null) {
                        showProductFound(product)
                    } else {
                        showProductNotFound()
                    }
                }
            }
        }
    }

    private fun showProductFound(product: Product) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_product_found, null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)

        val bindingDialog = DialogProductFoundBinding.bind(dialogView)

        bindingDialog.textProductName.text = product.productName

        lifecycleScope.launch {
            cartViewModel.isProductInCart(product.id).collect { isInCart ->
                bindingDialog.buttonAddToCart.setOnClickListener {
                    if (isInCart) {
                        cartViewModel.incrementQuantity(product.id)
                    } else {
                        cartViewModel.addToCart(product.id, 1)
                    }
                    dialog.dismiss()
                    resetScanning()
                }
            }
        }

        dialog.setOnDismissListener {
            resetScanning()
        }

        dialog.show()
    }

    private fun showProductNotFound() {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Товар не найден")
        dialogBuilder.setPositiveButton("Попробовать еще раз") { dialog, _ ->
            dialog.dismiss()
            resetScanning()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.setOnDismissListener {
            resetScanning()
        }

        alertDialog.show()
    }

    private fun resetScanning() {
        isScanning = false
        isDialogShown = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}
