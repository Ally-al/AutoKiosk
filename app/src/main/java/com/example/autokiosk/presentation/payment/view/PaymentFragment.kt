package com.example.autokiosk.presentation.payment.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.autokiosk.R
import com.example.autokiosk.databinding.FragmentPaymentBinding
import com.example.autokiosk.domain.models.Card
import com.example.autokiosk.presentation.card.viewmodel.CardViewModel
import com.example.autokiosk.presentation.cart.viewmodel.CartViewModel
import com.example.autokiosk.presentation.payment.viewmodel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PaymentFragment : Fragment(R.layout.fragment_payment) {

    private lateinit var binding: FragmentPaymentBinding
    private val paymentViewModel: PaymentViewModel by viewModels()
    private val cardViewModel: CardViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPaymentBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            cardViewModel.cards.collect { cards ->
                if (cards.isNotEmpty()) {
                    if (paymentViewModel.selectedCard.value == null) {
                        paymentViewModel.selectCard(cards.last())
                    }
                    binding.btnSelectCard.visibility = View.VISIBLE
                } else {
                    binding.selectedCard.text = "Нет привязанных карт"
                    binding.btnSelectCard.visibility = View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            paymentViewModel.selectedCard.collect { selectedCard ->
                selectedCard?.let {
                    binding.selectedCard.text = "Выбрана карта: **** ${it.lastFourDigits}"
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            paymentViewModel.paymentStatus.collect { status ->
                binding.paymentStatus.text = status
                binding.paymentStatus.visibility = if (status.isNotEmpty()) View.VISIBLE else View.GONE

                binding.btnRetry.visibility = if (status == "Ошибка оплаты") View.VISIBLE else View.GONE
                binding.btnPay.visibility = if (status != "Ошибка оплаты" && status != "Оплата прошла успешно!") View.VISIBLE else View.GONE
                binding.btnReturnToCatalog.visibility = if (status == "Оплата прошла успешно!") View.VISIBLE else View.GONE

                if (status == "Оплата прошла успешно!") {
                    cartViewModel.clearCart()
                }
            }
        }

        binding.btnReturnToCatalog.setOnClickListener {
            findNavController().navigate(R.id.action_payment_to_catalog)
        }

        binding.btnSelectCard.setOnClickListener {
            cardViewModel.cards.value.let { cards ->
                if (cards.isNotEmpty()) {
                    showCardSelectionDialog(cards)
                }
            }
        }

        binding.btnPay.setOnClickListener {
            if (paymentViewModel.selectedCard.value == null) {
                Toast.makeText(requireContext(), "Сначала выберите карту!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            paymentViewModel.processPayment()
        }

        binding.btnRetry.setOnClickListener {
            paymentViewModel.processPayment()
        }
    }

    private fun showCardSelectionDialog(cards: List<Card>) {
        val cardNumbers = cards.map { "**** ${it.lastFourDigits}" }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Выберите карту")
            .setItems(cardNumbers) { _, which ->
                paymentViewModel.selectCard(cards[which])
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
