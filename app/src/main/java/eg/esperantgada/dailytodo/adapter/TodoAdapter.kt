package eg.esperantgada.dailytodo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.databinding.TodoItemBinding
import eg.esperantgada.dailytodo.fragment.todo.TodoFragment
import eg.esperantgada.dailytodo.model.Todo
import eg.esperantgada.dailytodo.utils.ColorPicker
import eg.esperantgada.dailytodo.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

const val TAG = "TodoAdapter"

class TodoAdapter(
    private val context: Context,
    private val listener: OnItemClickedListener
) : PagingDataAdapter<Todo, TodoAdapter.TodoViewHolder>(DiffCallback) {

    inner class TodoViewHolder(private val binding: TodoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        init {
            binding.apply {
                root.setOnClickListener {
                    //Gets the position of the View holder
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val todo = getItem(position)
                        if (todo != null) {
                            listener.onItemClicked(todo)
                        }
                    }
                }

                checkbox.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
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
                checkbox.isChecked = todo.completed
                todoTextView.text = todo.name
                todoTextView.paint.isStrikeThruText = todo.completed
                priorityImageView.isVisible = todo.important
                durationTextView.text = todo.duration
            }
            binding.createdDateAndTime.text = todo.createdAt
            binding.dueDateAndTime.text =
                context.getString(R.string.date_and_time, todo.date, todo.time)

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

    interface OnItemClickedListener {

        fun onItemClicked(todo: Todo)

        fun onCheckBoxClicked(todo: Todo, isChecked: Boolean)
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
                return oldItem == newItem
            }
        }
    }
}