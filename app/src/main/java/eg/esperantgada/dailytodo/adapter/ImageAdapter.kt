package eg.esperantgada.dailytodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import eg.esperantgada.dailytodo.databinding.CategoryBackgroundItemListBinding

class ImageAdapter(
    private val images : IntArray,
    ) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var adapterListener : OnImageItemClickedListener? = null


    inner class ImageViewHolder(
            private val binding: CategoryBackgroundItemListBinding
            ) : RecyclerView.ViewHolder(binding.root){


                fun bind(imageId : Int, adapterListener: OnImageItemClickedListener){
                    Picasso
                        .get()
                        .load(imageId)
                        .centerCrop()
                        .resize(76, 76)
                        .into(binding.roundBackground)

                    binding.root.setOnClickListener {
                        val image = images[absoluteAdapterPosition]
                        adapterListener.onItemClicked(image)
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
        adapterListener?.let { holder.bind(currentImage, it) }
    }

    override fun getItemCount() = images.size

    interface OnImageItemClickedListener {

        fun onItemClicked(imageId : Int)

    }

}