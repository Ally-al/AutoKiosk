package com.example.autokiosk.presentation.catalog.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.autokiosk.R
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

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productWeight: TextView = itemView.findViewById(R.id.product_weight)
        val addToCartButton: View = itemView.findViewById(R.id.add_to_cart_button)
        private val quantityText: TextView = itemView.findViewById(R.id.text_quantity_catalog)
        private val minusButton: ImageButton = itemView.findViewById(R.id.button_minus_catalog)
        private val plusButton: ImageButton = itemView.findViewById(R.id.button_plus_catalog)
        private val quantityBlock: View = itemView.findViewById(R.id.quantity_block)

        fun bind(product: Product) {
            productName.text = product.productName
            productPrice.text = "${product.productPrice} ₽"
            productWeight.text = product.productWeight

            Glide.with(itemView.context)
                .load(product.imageUrls.firstOrNull())
                .into(productImage)

            itemView.setOnClickListener { onProductClick(product) }

            lifecycleOwner.lifecycleScope.launch {
                cartViewModel.getCartItemQuantity(product.id).collectLatest { quantity ->
                    val layoutParams = productPrice.layoutParams as ConstraintLayout.LayoutParams
                    if (quantity > 0) {
                        addToCartButton.visibility = View.GONE
                        quantityBlock.visibility = View.VISIBLE
                        quantityText.text = quantity.toString()

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

            plusButton.setOnClickListener {
                cartViewModel.incrementQuantity(product.id)
            }

            minusButton.setOnClickListener {
                cartViewModel.decrementQuantity(product.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
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
