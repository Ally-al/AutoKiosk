package com.example.autokiosk.presentation.card.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.autokiosk.R
import com.example.autokiosk.domain.models.Card

class CardAdapter(private val onDelete: (String) -> Unit) :
    ListAdapter<Card, CardAdapter.CardViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = getItem(position)
        holder.bind(card)
    }

    inner class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardNumber: TextView = view.findViewById(R.id.tvCardNumber)
        private val deleteIcon: ImageView = view.findViewById(R.id.btnDeleteCard)

        fun bind(card: Card) {
            cardNumber.text = "**** ${card.lastFourDigits}"
            deleteIcon.setOnClickListener {
                onDelete(card.id)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Card>() {
            override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
                return oldItem == newItem
            }
        }
    }
}
