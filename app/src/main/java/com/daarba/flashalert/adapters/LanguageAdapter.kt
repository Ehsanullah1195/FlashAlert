package com.daarba.flashalert.adapters

import com.daarba.flashalert.data.Language
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import com.daarba.flashalert.R
import com.daarba.flashalert.databinding.ItemLangaugeCardBinding

class LanguageAdapter(private val languages: List<Language>, private val onLanguageClick: (Language) -> Unit) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var selectedPosition = languages.indexOfFirst { it.name == "English" }

    class LanguageViewHolder(val binding: ItemLangaugeCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLangaugeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        with(holder.binding) {
            tvLanguageName.text = language.name
            ivLanguageFlag.setImageResource(language.flagResId)

            if (position == selectedPosition) {
                tvLanguageName.setTextColor(ContextCompat.getColor(root.context, R.color.active_color))
                cardLanguage.isSelected = true
            } else {
                tvLanguageName.setTextColor(ContextCompat.getColor(root.context, R.color.in_active_color))
                cardLanguage.isSelected = false
            }

            root.setOnClickListener {
                selectedPosition = holder.adapterPosition
                notifyDataSetChanged()
                onLanguageClick(language)
            }
        }
    }

    override fun getItemCount(): Int = languages.size
}
