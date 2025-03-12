package com.example.autokiosk.presentation.cart.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.autokiosk.databinding.ItemCartBinding
import com.example.autokiosk.domain.models.CartItem

class CartAdapter(
    private val cartItems: List<CartItem>,
    private val onRemoveClick: (CartItem) -> Unit,
    private val onIncrementQuantity: (CartItem) -> Unit,
    private val onDecrementQuantity: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            with(binding) {
                textProductName.text = cartItem.name
                textProductPrice.text = String.format("%.2f ₽", cartItem.price * cartItem.quantity)
                textQuantityCart.text = cartItem.quantity.toString()

                Glide.with(imageProduct.context)
                    .load(cartItem.imageUrl)
                    .into(imageProduct)

                buttonRemoveCart.setOnClickListener { onRemoveClick(cartItem) }
                buttonPlusCart.setOnClickListener { onIncrementQuantity(cartItem) }
                buttonMinusCart.setOnClickListener {
                    if (cartItem.quantity == 1) onRemoveClick(cartItem) else onDecrementQuantity(cartItem)
                }
            }
        }
    }
}
