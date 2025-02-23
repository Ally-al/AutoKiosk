package com.example.autokiosk.presentation.catalog.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.autokiosk.R
import com.example.autokiosk.domain.models.Product

class CatalogAdapter(
    private var products: List<Product> = emptyList(),
    private val onProductClick: (Product) -> Unit = {}
) : RecyclerView.Adapter<CatalogAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productName: TextView = itemView.findViewById(R.id.product_name)
        val productPrice: TextView = itemView.findViewById(R.id.product_price)
        val productWeight: TextView = itemView.findViewById(R.id.product_weight)
        val addToCartButton: View = itemView.findViewById(R.id.add_to_cart_button)

        fun bind(product: Product) {
            productName.text = product.productName
            productPrice.text = "${product.productPrice} ₽"
            productWeight.text = product.productWeight

            Glide.with(itemView.context)
                .load(product.imageUrls.firstOrNull())
                .into(productImage)

            itemView.setOnClickListener { onProductClick(product) }
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
