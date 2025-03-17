package com.example.autokiosk.presentation.profile.view

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.autokiosk.R
import com.example.autokiosk.databinding.FragmentProfileBinding
import com.example.autokiosk.presentation.profile.viewmodel.ProfileViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)

        viewModel.loadUserEmail()

        viewModel.email.observe(viewLifecycleOwner) { email ->
            binding.textEmail.text = email
        }

        binding.textLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.textBankCard.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_bank_card_list)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hasLinkedCards.collect { hasCards ->
                    binding.qrcodeButton.alpha = if (hasCards) 1.0f else 0.5f
                }
            }
        }

        binding.qrcodeButton.setOnClickListener {
            val hasCards = viewModel.hasLinkedCards.value

            if (hasCards) {
                generateAndShowQrCode()
            } else {
                showNoCardsDialog()
            }
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Вы уверены, что хотите выйти?")
            .setCancelable(false)
            .setPositiveButton("Да") { _, _ ->
                logout()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun logout() {
        viewModel.logout()
        findNavController().navigate(R.id.action_profile_to_login)
    }

    private fun showNoCardsDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Сначала добавьте способ оплаты")
            .setPositiveButton("ОК") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun generateAndShowQrCode() {
        val userId = viewModel.getUserId()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Ошибка: не удалось получить ID пользователя", Toast.LENGTH_SHORT).show()
            return
        }

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(System.currentTimeMillis())

        val qrData = "$userId|$currentTime"

        val qrBitmap = generateQrCode(qrData)
        if (qrBitmap != null) {
            showQrCodeDialog(qrBitmap)
        } else {
            Toast.makeText(requireContext(), "Ошибка генерации QR-кода", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateQrCode(text: String): Bitmap? {
        return try {
            val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 500, 500)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    private fun showQrCodeDialog(qrBitmap: Bitmap) {
        val imageView = ImageView(requireContext()).apply {
            setImageBitmap(qrBitmap)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Ваш QR-код")
            .setView(imageView)
            .setPositiveButton("Закрыть") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
