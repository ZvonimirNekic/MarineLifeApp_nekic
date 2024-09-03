package org.unizd.rma.nekic.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.unizd.rma.nekic.R
import org.unizd.rma.nekic.models.MarineLife
import java.text.SimpleDateFormat
import java.util.Locale

class MarineLifeViewAdapter(
    private val deleteUpdateCallBack: (type: String, position: Int, marineLife: MarineLife) -> Unit
) : RecyclerView.Adapter<MarineLifeViewAdapter.ViewHolder>() {

    private val marineLifeList = arrayListOf<MarineLife>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val color: TextView = itemView.findViewById(R.id.color)
        val depth: TextView = itemView.findViewById(R.id.depth)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
        val delImg: ImageView = itemView.findViewById(R.id.deleteImg)
        val editImg: ImageView = itemView.findViewById(R.id.editImg)
        val marineImage: ImageView = itemView.findViewById(R.id.marineImage)
        val type: TextView = itemView.findViewById(R.id.type)
    }

    fun addAllMarineLife(newMarineLifeList: List<MarineLife>) {
        marineLifeList.clear()
        marineLifeList.addAll(newMarineLifeList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.view_marine_life_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val marineLife = marineLifeList[position]

        holder.color.text = marineLife.color
        holder.depth.text = marineLife.depth
        holder.type.text = marineLife.typeOfMarine

        // Load image from imageUri using Glide library
        Glide.with(holder.itemView.context)
            .load(marineLife.imageUri)
            .into(holder.marineImage)

        holder.delImg.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallBack("delete",holder.adapterPosition, marineLife)
            }
        }

        holder.editImg.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallBack("update",holder.adapterPosition, marineLife)
            }
        }


        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())
        holder.dateText.text = dateFormat.format(marineLife.date)
    }

    override fun getItemCount(): Int {
        return marineLifeList.size
    }
}
