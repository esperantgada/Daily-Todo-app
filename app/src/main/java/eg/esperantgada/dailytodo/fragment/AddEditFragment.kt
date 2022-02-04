package eg.esperantgada.dailytodo.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eg.esperantgada.dailytodo.R
import eg.esperantgada.dailytodo.databinding.FragmentAddEditBinding

class AddEditFragment : Fragment() {

    private var _binding : FragmentAddEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}