package eg.esperantgada.dailytodo.fragment.todo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.anurag.multiselectionspinner.MultiSelectionSpinnerDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.databinding.FragmentAddEditTodoBinding
import eg.esperantgada.dailytodo.event.AddEditTodoEvent
import eg.esperantgada.dailytodo.notification.Ringtone
import eg.esperantgada.dailytodo.sharepreference.TodoSharePreference
import eg.esperantgada.dailytodo.utils.*
import eg.esperantgada.dailytodo.viewmodel.AddEditTodoViewModel
import java.util.*

const val TAG1 = "AddEditTodoFragment"

@Suppress("IMPLICIT_CAST_TO_ANY", "DEPRECATION")
@AndroidEntryPoint
class AddEditTodoFragment : Fragment(),
    MultiSelectionSpinnerDialog.OnMultiSpinnerSelectionListener {

    private var _binding: FragmentAddEditTodoBinding? = null
    private val binding get() = _binding!!

    private var isSwitchChecked: Boolean = false


    private val viewModel: AddEditTodoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddEditTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            todoName.setText(viewModel.todoName)
            importantTodo.isChecked = viewModel.isImportant
            importantTodo.jumpDrawablesToCurrentState()
            todoDate.getDatePicker(requireContext(), "dd/MM/yyyy", Date())
            todoTime.getTimePicker(requireContext(), "h:mm a")
            todoTime.setText(viewModel.todoTime)
            todoDate.setText(viewModel.todoDate)
            todoDuration.setText(viewModel.todoDuration)
            repeatSwitch.isChecked = viewModel.isSwitchOn
            repeatIntervalNeumor.isVisible = binding.repeatSwitch.isChecked
            multiSelectionSpinner.isVisible = binding.repeatSwitch.isChecked
            frequencyTextNeumor.isVisible = binding.repeatSwitch.isChecked
            frequencyText.text = viewModel.days.toString().replace("[","").replace("]", "")

            //Sets item's name in the viewModel to the name of edited item and save it SaveStateHandle
            todoName.addTextChangedListener {
                viewModel.todoName = it.toString()
            }

            //Sets item's importance in the viewModel to the importance of edited item and save it SaveStateHandle
            importantTodo.setOnCheckedChangeListener { _, isChecked ->
                viewModel.isImportant = isChecked
            }

            repeatSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.isSwitchOn = isChecked
                repeatIntervalNeumor.isVisible = isChecked
                multiSelectionSpinner.isVisible = isChecked
                frequencyTextNeumor.isVisible =  isChecked
            }

            todoDate.addTextChangedListener {
                viewModel.todoDate = it.toString()
            }

            todoTime.addTextChangedListener {
                viewModel.todoTime = it.toString()
            }


            saveFloatingButton.setOnClickListener {
                viewModel.onSaveClick(requireActivity())
            }

            ringtoneButton.setOnClickListener {
                val ringtone = Ringtone(requireContext())
                startActivityForResult(ringtone.setNotificationRingtone(), 23)
            }

            todoDuration.addTextChangedListener {
                viewModel.todoDuration = it.toString()
            }


            val uri = viewModel.todoRingtoneUri
            val ringtone = RingtoneManager.getRingtone(requireContext(), uri.toUri())
            binding.ringtoneButton.text = ringtone.getTitle(requireContext())



            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.addEditTodoEvent.collect { event ->
                    when (event) {
                        is AddEditTodoEvent.GoBackWithResult -> {
                            binding.todoName.clearFocus()

                            //Navigates back to TodoFragment with the result of adding or editing action
                            setFragmentResult(
                                REQUEST_KEY,
                                bundleOf(ADD_EDIT_RESULT_KEY to event.result)
                            )
                            findNavController().popBackStack()
                        }

                        is AddEditTodoEvent.ShowInvalidInputMessage -> {
                            Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG)
                                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                                .show()
                        }
                    }.exhaustive
                }
            }

            //Creates an array of string
            val frequencyList =
                ArrayList(listOf(*resources.getStringArray(R.array.repeat_interval)))

            //Sets multi selection spinner
            binding.multiSelectionSpinner.setAdapterWithOutImage(requireContext(),
                frequencyList,
                this@AddEditTodoFragment)

            binding.multiSelectionSpinner
                .initMultiSpinner(requireContext(), binding.multiSelectionSpinner)

        }
    }


    @Deprecated("Deprecated in Java")
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 23 && resultCode == Activity.RESULT_OK) {
            val todoUri =
                data!!.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            viewModel.todoRingtoneUri = todoUri.toString()


            if (isWriteSettingsPermissionGranted()) {
                val uri = viewModel.todoRingtoneUri.toUri()
                val ringtone = RingtoneManager.getRingtone(requireContext(), uri)
                binding.ringtoneButton.text = ringtone.getTitle(requireContext())
                Log.d(TAG1, "NOTIFICATION SOUND : ${ringtone.getTitle(requireContext())}")
            }
        }
    }


    //Set permission before being able to access ringtone title
    @SuppressLint("NewApi")
    private fun isWriteSettingsPermissionGranted(): Boolean {
        var permission = Settings.System.canWrite(requireContext())

        if (!permission) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package: ${requireContext().packageName}")
            activity?.startActivityFromFragment(this@AddEditTodoFragment, intent, 12)

            permission = true
        }


        return permission
    }


    override fun OnMultiSpinnerItemSelected(chosenItems: MutableList<String>?) {
        val daysList: MutableList<String> = arrayListOf()
        val todoSharePreference = TodoSharePreference(requireContext())

        if (chosenItems != null) {
            for (i in chosenItems.indices) {
                daysList.add(chosenItems[i])
            }
        }
        todoSharePreference.saveSpinnerSelectedItems("days", daysList)
        isSwitchChecked = binding.repeatSwitch.isChecked

        if (isSwitchChecked) {
            viewModel.days = daysList
        }
        Log.d(TAG1, "SELECTED ITEMS LIST : $daysList")
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}