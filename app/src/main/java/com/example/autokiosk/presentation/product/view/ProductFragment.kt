package com.example.autokiosk.presentation.product.view

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.autokiosk.R
import com.example.autokiosk.databinding.FragmentProductDetailsBinding
import com.example.autokiosk.presentation.cart.viewmodel.CartViewModel
import com.example.autokiosk.presentation.product.viewmodel.ProductViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.fragment_product_details) {
    private val viewModel: ProductViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private lateinit var binding: FragmentProductDetailsBinding

    private val args: ProductFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProductDetailsBinding.bind(view)

        val productId = args.productId

        viewModel.loadProduct(productId)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.product.collect { product ->
                    product?.let {
                        binding.productName.text = it.productName
                        binding.productWeight.text = it.productWeight
                        binding.productPrice.text = "${it.productPrice} ₽"
                        binding.productContents.text = it.productContents

                        binding.calories.text = it.calories.toString()
                        binding.proteins.text = it.proteins.toString()
                        binding.fats.text = it.fats.toString()
                        binding.carbs.text = it.carbs.toString()

                        binding.productShelfLife.text = it.productShelfLife
                        binding.productBrand.text = it.productBrand

                        setupImageGallery(it.imageUrls)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.getCartItemQuantity(productId).collect { quantity ->
                    val sectionInfoParams = binding.sectionInfo.layoutParams as ConstraintLayout.LayoutParams
                    if (quantity > 0) {
                        binding.btnAddToCart.visibility = View.GONE
                        binding.quantityBlockProductDetails.visibility = View.VISIBLE
                        binding.textQuantityProductDetails.text = quantity.toString()

                        sectionInfoParams.topToBottom = binding.quantityBlockProductDetails.id
                    } else {
                        binding.btnAddToCart.visibility = View.VISIBLE
                        binding.quantityBlockProductDetails.visibility = View.GONE

                        sectionInfoParams.topToBottom = binding.btnAddToCart.id
                    }
                    binding.sectionInfo.layoutParams = sectionInfoParams
                }
            }
        }

        binding.btnAddToCart.setOnClickListener {
            cartViewModel.addToCart(productId, 1)
        }

        binding.buttonPlusProductDetails.setOnClickListener {
            cartViewModel.incrementQuantity(productId)
        }

        binding.buttonMinusProductDetails.setOnClickListener {
            cartViewModel.decrementQuantity(productId)
        }
    }

    private fun setupImageGallery(imageUrls: List<String>) {
        if (imageUrls.isNotEmpty()) {
            val adapter = ImageGalleryAdapter(requireContext(), imageUrls)
            binding.productImageGalleryViewpager.adapter = adapter

            TabLayoutMediator(binding.tabLayout, binding.productImageGalleryViewpager) { _, _ -> }.attach()
        }
    }
}
