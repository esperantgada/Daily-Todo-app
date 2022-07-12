package eg.esperantgada.dailytodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eg.esperantgada.dailytodo.databinding.CategoryBackgroundItemListBinding

class ColorAdapter(
    private val colors : IntArray,
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    var adapterListener : ImageAdapter.OnItemClickedListener? = null


    inner class ColorViewHolder(
        private val binding: CategoryBackgroundItemListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val color = colors[position]
                adapterListener?.onItemClicked(color)
            }
        }


        fun bind(color: Int) {
            binding.roundBackground.setImageResource(color)
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
        holder.bind(currentColor)
    }

    override fun getItemCount() = colors.size

    interface OnItemClickedListener {

        fun onItemClicked(color: Int)

    }
}