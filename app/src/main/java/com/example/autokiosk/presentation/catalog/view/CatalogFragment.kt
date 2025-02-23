package com.example.autokiosk.presentation.catalog.view

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.autokiosk.R
import com.example.autokiosk.presentation.catalog.viewmodel.CatalogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CatalogFragment : Fragment(R.layout.fragment_catalog) {

    private val viewModel: CatalogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val filterButton: ImageButton = view.findViewById(R.id.filter_button)
        val recyclerCatalog: RecyclerView = view.findViewById(R.id.recycler_catalog)
        val searchInput: EditText = view.findViewById(R.id.search_input)

        val adapter = CatalogAdapter()
        recyclerCatalog.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { products ->
                adapter.submitList(products)
            }
        }

        filterButton.setOnClickListener {
            showCategorySelectionDialog()
        }

        setupSearch(searchInput)
    }

    private fun showCategorySelectionDialog() {
        val categories = viewModel.categories.value
        if (categories.isEmpty()) return

        var expandedCategory: String? = null
        val items = mutableListOf("Все товары")
        val categoryIndexes = mutableMapOf<Int, String>()

        val listView = ListView(requireContext())
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        listView.adapter = adapter

        listView.divider = null

        lateinit var dialog: AlertDialog

        fun updateDialog() {
            items.clear()
            items.add("Все товары")

            categoryIndexes.clear()

            categories.forEach { category ->
                val isExpanded = category.name == expandedCategory
                items.add("${category.name} \u25BC")
                categoryIndexes[items.lastIndex] = category.name

                if (isExpanded) {
                    items.addAll(category.subcategories.map { "    $it" })
                }
            }

            adapter.notifyDataSetChanged()
        }

        listView.setOnItemClickListener { _, _, which, _ ->
            val selectedItem = items[which].trim()

            when {
                which == 0 -> {
                    viewModel.resetCategoryFilter()
                    dialog.dismiss()
                }
                categoryIndexes.containsKey(which) -> {
                    val categoryName = categoryIndexes[which]!!
                    if (expandedCategory == categoryName) {
                        viewModel.filterProductsByCategory(categoryName)
                        dialog.dismiss()
                    } else {
                        expandedCategory = categoryName
                        updateDialog()
                    }
                }
                else -> {
                    viewModel.filterProductsByCategory(selectedItem)
                    dialog.dismiss()
                }
            }
        }

        dialog = AlertDialog.Builder(requireContext())
            .setView(listView)
            .setNegativeButton("Отмена", null)
            .create()

        dialog.setOnShowListener {
            updateDialog()
        }

        dialog.show()
    }

    private fun setupSearch(searchInput: EditText) {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                viewModel.searchProducts(query)
                searchInput.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_search),
                    null,
                    if (query.isEmpty()) null else ContextCompat.getDrawable(requireContext(), R.drawable.ic_clear),
                    null
                )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        searchInput.setOnTouchListener { v, event ->
            val drawableRight = searchInput.compoundDrawables[2]
            val drawableRightWidth = drawableRight?.intrinsicWidth ?: 0

            if (event.action == MotionEvent.ACTION_UP && event.x >= (searchInput.width - searchInput.paddingRight - drawableRightWidth)) {
                searchInput.text.clear()
                searchInput.clearFocus()
                return@setOnTouchListener true
            }
            v.performClick()
            false
        }
    }
}
