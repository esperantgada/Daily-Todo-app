package eg.esperantgada.dailytodo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.databinding.TodoItemBinding
import eg.esperantgada.dailytodo.model.Todo

class TodoAdapter(
    private val context: Context,
    private val listener : OnItemClickedListener
    ) : PagingDataAdapter<Todo, TodoAdapter.TodoViewHolder>(DiffCallback) {


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
                        if (todo != null) {
                            listener.onItemClicked(todo)
                        }
                    }
                }

                checkbox.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val todo = getItem(position)
                        if (todo != null) {
                            listener.onCheckBoxClicked(todo, checkbox.isChecked)
                        }
                    }
                }
            }
        }

        fun bind(todo: Todo) {
            binding.apply {
                checkbox.isChecked = todo.isCompleted
                todoTextView.text = todo.name
                todoTextView.paint.isStrikeThruText = todo.isCompleted
                priorityImageView.isVisible = todo.isImportant
                createdDateAndTime.text = todo.dataFormatted
                dueDateAndTime.text = context.getString(R.string.date_and_time, todo.date, todo.time)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val currentTodo = getItem(position)
        if (currentTodo != null) {
            holder.bind(currentTodo)
        }
    }

    interface OnItemClickedListener{

        fun onItemClicked(todo: Todo)

        fun onCheckBoxClicked(todo: Todo, isChecked : Boolean)
    }

    companion object{
        val DiffCallback = object : DiffUtil.ItemCallback<Todo>(){
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem == newItem
            }

        }
    }
}