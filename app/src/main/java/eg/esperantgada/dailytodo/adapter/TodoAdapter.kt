package eg.esperantgada.dailytodo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eg.esperantgada.dailytodo.databinding.TodoItemBinding
import eg.esperantgada.dailytodo.model.TodoEntity

class TodoAdapter : ListAdapter<TodoEntity, TodoAdapter.TodoViewHolder>(DiffCallback) {

    class TodoViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

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