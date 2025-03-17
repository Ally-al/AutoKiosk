package com.example.autokiosk.presentation.card.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.autokiosk.R
import com.example.autokiosk.databinding.FragmentCardListBinding
import com.example.autokiosk.presentation.card.viewmodel.CardViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardListFragment : Fragment(R.layout.fragment_card_list) {

    private lateinit var binding: FragmentCardListBinding
    private val viewModel: CardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCardListBinding.bind(view)

        val adapter = CardAdapter { cardId ->
            showDeleteConfirmationDialog(cardId)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cards.collectLatest { cards ->
                adapter.submitList(cards)
            }
        }

        binding.btnAddCard.setOnClickListener {
            findNavController().navigate(R.id.action_card_list_to_card_binding)
        }
    }

    private fun showDeleteConfirmationDialog(cardId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление карты")
            .setMessage("Вы точно хотите удалить карту?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.removeCard(cardId)
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
