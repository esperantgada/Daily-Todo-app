package eg.esperantgada.dailytodo.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.viewmodel.AlertDialogViewModel

@AndroidEntryPoint
class AlertDialogFragment : DialogFragment() {

    private val viewModel : AlertDialogViewModel by viewModels ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog = AlertDialog.Builder(requireContext())
        .setTitle(getString(R.string.confirm_deletion_message))
        .setMessage(getString(R.string.alert_dialog_message))
        .setNegativeButton(getString(R.string.cancel), null)
        .setPositiveButton(getString(R.string.yes)){ _, _ ->
            viewModel.onConfirmDeleteTodo()

            Toast.makeText(requireContext(), getString(R.string.successful_deletion_message),
                Toast.LENGTH_LONG)
                .show()
        }
        .create()

}