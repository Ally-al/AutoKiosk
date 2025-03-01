package com.example.autokiosk.presentation.profile.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.autokiosk.R
import com.example.autokiosk.databinding.FragmentProfileBinding
import com.example.autokiosk.presentation.profile.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

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
        binding.textPurchaseHistory.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_purchase_history)
        }
        binding.textBankCard.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_bank_card)

        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Вы уверены, что хотите выйти?")
            .setCancelable(false)
            .setPositiveButton("Да") { dialog, id ->
                logout()
            }
            .setNegativeButton("Отмена") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        findNavController().navigate(R.id.action_profile_to_login)
    }
}
