package com.example.myapplication.Achievement

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.SharedViewModel

class AchievementAdapter(private var achievements: List<Achievement>,
                         private val viewModel: SharedViewModel
) :
    RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }
    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.bind(achievement)
        holder.rewardButton.setOnClickListener {
            onRewardButtonClick(holder,achievement)
        }
    }
    private fun onRewardButtonClick(holder: AchievementViewHolder,achievement: Achievement) {
            viewModel.increaseCurrency(achievement.reward)
            holder.rewardButton.visibility = View.GONE

    }
    override fun getItemCount(): Int {
        return achievements.size
    }
    fun updateData(newData: List<Achievement>) {
        achievements = newData.filter { it.isCompleted }

        notifyDataSetChanged()
    }
    inner class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.achievementNameTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.achievementDescriptionTextView)
        val rewardButton: Button = itemView.findViewById(R.id.rewardButton)
        @SuppressLint("SetTextI18n")
        fun bind(achievement: Achievement) {
            nameTextView.text = achievement.name
            descriptionTextView.text = "${achievement.description} (${achievement.progress}/${achievement.targetProgress}) "
            if (achievement.isCompleted) {
                itemView.visibility = View.VISIBLE
            } else {
                itemView.visibility = View.GONE
            }
            if(achievement.isCollect){
                rewardButton.visibility = View.VISIBLE
                achievement.isCollect = false
            }else{
                rewardButton.visibility = View.GONE
            }
            rewardButton.text = "${achievement.reward}"
        }
    }
}
