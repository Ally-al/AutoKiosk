package com.example.autokiosk.presentation.card.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.autokiosk.R
import com.example.autokiosk.databinding.FragmentCardBindingBinding
import com.example.autokiosk.presentation.card.viewmodel.CardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
    class CardBindingFragment : Fragment(R.layout.fragment_card_binding) {

    private lateinit var binding: FragmentCardBindingBinding
    private val viewModel: CardViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCardBindingBinding.bind(view)

        binding.btnBindCard.setOnClickListener {
            val cardNumber = binding.etCardNumber.text.toString()
            val expiryDate = binding.etExpiryDate.text.toString()

            if (cardNumber.length == 16 && expiryDate.matches(Regex("^(0[1-9]|1[0-2])/\\d{2}$"))) {
                viewModel.addCard(cardNumber, expiryDate)
                Toast.makeText(requireContext(), "Карта привязана!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(requireContext(), "Некорректные данные карты", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
