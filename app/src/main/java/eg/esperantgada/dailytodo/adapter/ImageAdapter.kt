package eg.esperantgada.dailytodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.esperantgada.dailytodo.databinding.CategoryBackgroundItemListBinding

class ImageAdapter(
    private val images : IntArray,
    ) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var adapterListener : OnItemClickedListener? = null


    inner class ImageViewHolder(
            private val binding: CategoryBackgroundItemListBinding
            ) : RecyclerView.ViewHolder(binding.root){


                fun bind(imageId : Int){
                    Picasso
                        .get()
                        .load(imageId)
                        .centerCrop()
                        .resize(76, 76)
                        .into(binding.roundBackground)

                    binding.root.setOnClickListener {
                        val image = images[position]
                        adapterListener?.onItemClicked(image)
                    }

                }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layout =
            CategoryBackgroundItemListBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return ImageViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentImage = images[position]
        holder.bind(currentImage)
    }

    override fun getItemCount() = images.size

    interface OnItemClickedListener {

        fun onItemClicked(imageId : Int)

    }

}