package eg.esperantgada.dailytodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eg.esperantgada.dailytodo.databinding.TodoItemBinding
import eg.esperantgada.dailytodo.model.TodoEntity

class TodoAdapter(
    private val listener : OnItemClickedListener
    ) : ListAdapter<TodoEntity, TodoAdapter.TodoViewHolder>(DiffCallback) {

   inner class TodoViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

       //Sets clickListener on Item in the recycler view
        init {
            binding.apply {
                root.setOnClickListener {
                    //Gets the position of the View holder
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val todo = getItem(position)
                        listener.onItemClicked(todo)
                    }
                }

                checkbox.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val todo = getItem(position)
                        listener.onCheckBoxClicked(todo, checkbox.isChecked)
                    }
                }
            }
        }

        fun bind(todo: TodoEntity) {
            binding.apply {
                checkbox.isChecked = todo.isCompleted
                todoTextView.text = todo.name
                todoTextView.paint.isStrikeThruText = todo.isCompleted
                priorityImageView.isVisible = todo.isImportant

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = getItem(position)
        holder.bind(currentTodo)
    }

    interface OnItemClickedListener{

        fun onItemClicked(todo: TodoEntity)

        fun onCheckBoxClicked(todo: TodoEntity, isChecked : Boolean)
    }

    companion object{
        val DiffCallback = object : DiffUtil.ItemCallback<TodoEntity>(){
            override fun areItemsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean {
                return oldItem == newItem
            }

        }
    }
}