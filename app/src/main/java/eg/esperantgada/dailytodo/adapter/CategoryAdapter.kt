package eg.esperantgada.dailytodo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import eg.esperantgada.dailytodo.databinding.CategoryListItemBinding
import eg.esperantgada.dailytodo.model.Category
import eg.esperantgada.dailytodo.model.Note

class CategoryAdapter(
    private val context : Context,
    private val listener : OnItemClickedListener
    ) : PagingDataAdapter<Category, CategoryAdapter.CategoryViewHolder>(DiffCallback) {


    inner class CategoryViewHolder(private val binding: CategoryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.apply {
                root.setOnClickListener {
                    //Gets the position of the View holder
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val category = getItem(position)
                        if (category != null) {
                            listener.onItemClicked(category)
                        }
                    }
                }


                root.setOnClickListener {
                    //Gets the position of the View holder
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val category = getItem(position)
                        if (category != null) {
                            listener.onDeleteCategoryClicked(category, true)
                        }
                    }
                }

            }
        }


        fun bind(category: Category) {
            binding.apply {
                categoryIcon.setImageDrawable(ContextCompat.getDrawable(context, category.image))
                categoryName.text = category.categoryName
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentCategory = getItem(position)
        if (currentCategory != null) {
            holder.bind(currentCategory)
        }

    }

    interface OnItemClickedListener {
        fun onItemClicked(category: Category)

        fun onDeleteCategoryClicked(category: Category, isClicked: Boolean)


    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(
                oldItem: Category,
                newItem: Category): Boolean {
                return oldItem.categoryName == newItem.categoryName
            }

            override fun areContentsTheSame(
                oldItem: Category,
                newItem: Category): Boolean {
                return oldItem == newItem
            }
        }
    }
}