package com.example.myapplication

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Shop.Rarity
import com.example.myapplication.Shop.ShopItem

class PopupAdapter(private var resultItems: List<ShopItem>,private val isSinglePull: Boolean) :
    RecyclerView.Adapter<PopupAdapter.PopupViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupViewHolder {
        val layoutResId = if (isSinglePull) R.layout.item_single else R.layout.item_multi
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutResId, parent, false)
        return PopupViewHolder(view)
    }
    override fun onBindViewHolder(holder: PopupViewHolder, position: Int) {
        val item = resultItems[position]
        holder.bind(item)
    }
    override fun getItemCount(): Int {
        return resultItems.size
    }
    inner class PopupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemOccurrencesTextView: TextView = itemView.findViewById(R.id.itemOccurrencesTextView)
        private val itemViewbg: ImageView = itemView.findViewById(R.id.itemViewbg)
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemImageIconView: ImageView = itemView.findViewById(R.id.itemViewicon)
        @SuppressLint("SetTextI18n")
        fun bind(item: ShopItem) {
            val itemsArray = itemView.resources.obtainTypedArray(R.array.items_icon)
            val itemDrawableResId = itemsArray.getResourceId(item.index - 1, 0)
            itemsArray.recycle()
            itemImageIconView.setImageResource(itemDrawableResId)
            itemOccurrencesTextView.visibility= if (!isSinglePull) View.VISIBLE else View.GONE
            itemOccurrencesTextView.text = "1x"
            val rarityArray = itemView.resources.obtainTypedArray(R.array.items_bg)
            val rarityDrawableResId = rarityArray.getResourceId(item.rarity.ordinal, 0)
            rarityArray.recycle()
            itemViewbg.visibility=if (!isSinglePull) View.VISIBLE else View.GONE
            itemViewbg.setImageResource(rarityDrawableResId)
            val textColorResId = when (item.rarity) {
                Rarity.COMMON -> R.color.common
                Rarity.RARE -> R.color.rare
                Rarity.EPIC -> R.color.epic
            }
            itemNameTextView.visibility = if (isSinglePull) View.VISIBLE else View.GONE
            itemNameTextView.setTextColor(ContextCompat.getColor(itemView.context, textColorResId))
            itemNameTextView.text=item.name
        }

    }
}