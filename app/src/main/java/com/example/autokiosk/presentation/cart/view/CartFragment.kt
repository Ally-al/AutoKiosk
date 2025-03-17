package com.example.autokiosk.presentation.cart.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.autokiosk.R
import com.example.autokiosk.databinding.FragmentCartBinding
import com.example.autokiosk.presentation.cart.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var cartAdapter: CartAdapter
    private val cartViewModel: CartViewModel by activityViewModels()

    private lateinit var binding: FragmentCartBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCartBinding.bind(view)

        binding.recyclerCart.layoutManager = LinearLayoutManager(requireContext())

        cartViewModel.cartItems.onEach { cartItems ->
            val total = cartItems.sumOf { it.price * it.quantity }

            binding.textTotal.text = String.format("Итого: %.2f ₽", total)

            cartAdapter = CartAdapter(
                cartItems = cartItems,
                onRemoveClick = { cartItem ->
                    cartViewModel.removeFromCart(cartItem.id)
                },
                onIncrementQuantity = { cartItem ->
                    cartViewModel.incrementQuantity(cartItem.id)
                },
                onDecrementQuantity = { cartItem ->
                    cartViewModel.decrementQuantity(cartItem.id)
                }
            )
            binding.recyclerCart.adapter = cartAdapter
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.btnOrder.setOnClickListener {
            findNavController().navigate(R.id.action_cart_to_payment)
        }
    }
}
