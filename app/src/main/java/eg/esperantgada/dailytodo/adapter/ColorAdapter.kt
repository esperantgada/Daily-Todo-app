package eg.esperantgada.dailytodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eg.esperantgada.dailytodo.databinding.CategoryBackgroundItemListBinding


class ColorAdapter(
    private val colors : IntArray,
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    var colorAdapterListener : OnColorItemClickedListener? = null


    inner class ColorViewHolder(
        private val binding: CategoryBackgroundItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

/*
        init {
            binding.root.setOnClickListener {
                val color = colors[absoluteAdapterPosition]
                adapterListener?.onItemClicked(color)
            }
        }
*/


        fun bind(color: Int, adapterListener: OnColorItemClickedListener) {
            binding.roundBackground.setImageResource(color)
            binding.root.setOnClickListener {
                val colorId = colors[absoluteAdapterPosition]
                adapterListener.onItemClicked(colorId)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val layout =
            CategoryBackgroundItemListBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return ColorViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val currentColor = colors[position]
        colorAdapterListener?.let { holder.bind(currentColor, it) }
    }

    override fun getItemCount() = colors.size

    interface OnColorItemClickedListener {

        fun onItemClicked(color: Int)

    }
}