package com.example.myapplication.Shop

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R



class ShopAdapter(
    private var items: List<ShopItem>,
    private val itemClickListener: (ShopItem) -> Unit,
    private val equippedItemListener: (ShopItem) -> Unit,
    private var isInEquipState: Boolean = false
    ) : RecyclerView.Adapter<ShopAdapter.GachaViewHolder>() {
    private var equippedItem: ShopItem? = null
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GachaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gacha, parent, false)
        return GachaViewHolder(view)
    }
    override fun onBindViewHolder(holder: GachaViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            itemClickListener(item)
        }
    }
    override fun getItemCount(): Int {
        return items.size
    }
    fun getItems(): List<ShopItem> {
        return items
    }
    fun getVisibleItemCount(): Int {
        return items.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<ShopItem>) {
        items = newData.filter { it.shouldItemBeVisible() }

        notifyDataSetChanged()
    }
    fun setEquippedItem(item: ShopItem?) {
        equippedItem = item
        isInEquipState = false
        handler.post { notifyDataSetChanged() }
    }
    inner class GachaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemLevelTextView: TextView = itemView.findViewById(R.id.itemLevelTextView)
        private val itemOccurrencesProgressBar: ProgressBar = itemView.findViewById(R.id.itemOccurrenceProgressBar)
        private val itemOccurrencesTextView: TextView = itemView.findViewById(R.id.itemOccurrencesTextView)
        private val equipButton: Button = itemView.findViewById(R.id.equipButton)
        private val levelUpButton: Button = itemView.findViewById(R.id.levelUpButton)
        @SuppressLint("SetTextI18n")
        fun bind(item: ShopItem) {
            val itemsArray = itemView.resources.obtainTypedArray(R.array.items)
            val itemDrawableResId = itemsArray.getResourceId(item.index - 1, 0)
            itemsArray.recycle()
            val itemImageView: ImageView = itemView.findViewById(R.id.itemView)
            itemImageView.setImageResource(itemDrawableResId)

            itemLevelTextView.text = "LVL\n${item.level}"
            itemOccurrencesTextView.text = "${item.count}/5"
            if (isInEquipState) {
                equipButton.isEnabled = true
                equipButton.text = "Unequip"
            } else {
                equipButton.isEnabled = !item.isEquipped
                equipButton.text = if (item.isEquipped) {
                    "Unequip"
                } else {
                    "Equip"
                }
            }
            equipButton.setOnClickListener {
                equippedItemListener(item)
            }
            itemView.setOnClickListener {
                itemClickListener(item)
            }
            levelUpButton.visibility = if (item.isCountingUp) {
                View.VISIBLE
            } else {
                View.GONE
            }
            levelUpButton.setOnClickListener {
                item.level++
                item.power += 5
                item.count -= 5
                item.isCountingUp = false
                notifyItemChanged(adapterPosition)
            }
            itemOccurrencesProgressBar.progress = item.count * 20
        }
    }
}