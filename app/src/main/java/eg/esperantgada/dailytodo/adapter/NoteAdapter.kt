package eg.esperantgada.dailytodo.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import eg.esperantgada.dailytodo.databinding.NoteItemBinding
import eg.esperantgada.dailytodo.model.Note
import eg.esperantgada.dailytodo.utils.ColorPicker

class NoteAdapter(
    private val context: Context,
    private val listener : OnNoteClickedListener
    ) : PagingDataAdapter<Note, NoteAdapter.NoteViewHolder> (DiffCallback){


    inner class NoteViewHolder(
        private val binding: NoteItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

         val gridLayout = binding.gridLayout

        init {
            binding.apply {
                root.setOnClickListener {
                    //Gets the position of the View holder
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val note = getItem(position)
                        if (note != null) {
                            listener.onNoteClicked(note)
                        }
                    }
                }


                deleteNoteImage.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val note = getItem(position)
                        if (note != null){
                            listener.onDeleteNoteClicked(note, true)
                        }
                    }
                }


                shareImage.setOnClickListener {
                    val position = absoluteAdapterPosition
                    if (position != RecyclerView.NO_POSITION){
                        val note = getItem(position)
                        if (note != null){
                            listener.onShareNoteClicked(note, true)
                        }
                    }
                }
            }
        }


         fun bind(note: Note){
            binding.apply {
                noteTitle.text = note.title
                noteDescription.text = note.description
                noteCreatedDate.text = note.dateFormatted
                gridLayout.setBackgroundColor(Color.parseColor(ColorPicker.getColors()))

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflatedLayout = NoteItemBinding.
        inflate(LayoutInflater.from(parent.context), parent, false)

        return NoteViewHolder(inflatedLayout)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote = getItem(position)
        if (currentNote != null){
            holder.bind(currentNote)
        }
    }


    interface OnNoteClickedListener{

        fun onNoteClicked(note: Note)

        fun onShareNoteClicked(note: Note, isClicked : Boolean)

        fun onDeleteNoteClicked(note: Note, isClicked: Boolean)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean = oldItem.noteId == newItem.noteId

        override fun areContentsTheSame(
            oldItem: Note,
            newItem: Note
        ): Boolean = oldItem == newItem

    }
}