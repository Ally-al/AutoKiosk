package com.example.autokiosk.presentation.catalog.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.autokiosk.R
import com.example.autokiosk.databinding.ItemProductBinding
import com.example.autokiosk.domain.models.Product
import com.example.autokiosk.presentation.cart.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CatalogAdapter(
    private var products: List<Product> = emptyList(),
    private val cartViewModel: CartViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onProductClick: (Product) -> Unit = {}
) : RecyclerView.Adapter<CatalogAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            with(binding) {
                productName.text = product.productName
                productPrice.text = "${product.productPrice} ₽"
                productWeight.text = product.productWeight

                Glide.with(root.context)
                    .load(product.imageUrls.firstOrNull())
                    .into(productImage)

                root.setOnClickListener { onProductClick(product) }

                lifecycleOwner.lifecycleScope.launch {
                    cartViewModel.getCartItemQuantity(product.id).collectLatest { quantity ->
                        val layoutParams = productPrice.layoutParams as ConstraintLayout.LayoutParams
                        if (quantity > 0) {
                            addToCartButton.visibility = View.GONE
                            quantityBlock.visibility = View.VISIBLE
                            textQuantityCatalog.text = quantity.toString()

                            layoutParams.bottomToTop = R.id.quantity_block
                            layoutParams.bottomMargin = 25
                        } else {
                            addToCartButton.visibility = View.VISIBLE
                            quantityBlock.visibility = View.GONE

                            layoutParams.bottomToTop = R.id.add_to_cart_button
                            layoutParams.bottomMargin = 9
                        }
                        productPrice.layoutParams = layoutParams
                    }
                }

                addToCartButton.setOnClickListener {
                    cartViewModel.addToCart(product.id, 1)
                }

                buttonPlusCatalog.setOnClickListener {
                    cartViewModel.incrementQuantity(product.id)
                }

                buttonMinusCatalog.setOnClickListener {
                    cartViewModel.decrementQuantity(product.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun submitList(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
