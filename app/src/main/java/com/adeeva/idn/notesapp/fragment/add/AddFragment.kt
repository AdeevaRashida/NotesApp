package com.adeeva.idn.notesapp.fragment.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.adeeva.idn.notesapp.R
import com.adeeva.idn.notesapp.data.model.NoteData
import com.adeeva.idn.notesapp.data.viewModelData.NotesViewModel
import com.adeeva.idn.notesapp.databinding.FragmentAddBinding
import com.adeeva.idn.notesapp.fragment.SharedViewModels

class AddFragment : Fragment() {

    private var _addBinding : FragmentAddBinding? = null
    private val addBinding get() = _addBinding!!

    private  val notesViewModel : NotesViewModel by viewModels()
    private  val sharedViewModel : SharedViewModels by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _addBinding = FragmentAddBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        addBinding.spPriorities.onItemSelectedListener = sharedViewModel.listener

        return addBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add){
            insertDataToDataBase()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDataBase() {
        val mTitle = addBinding.etTitle.text.toString()
        val mPriority = addBinding.spPriorities.selectedItem.toString()
        val mDesc = addBinding.etDesc.text.toString()

        val validation = sharedViewModel.verifyDataFomUser(mTitle, mDesc)
        if(validation){
            val newData = NoteData(
                0,
                mTitle,
                sharedViewModel.parsePriority(mPriority),
                mDesc
            )
            notesViewModel.insertData(newData)
            Toast.makeText(requireContext(), "Successfully added", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_addFragment_to_listFragment3)
        }else {
            Toast.makeText(requireContext(), "Please fill in the blanks", Toast.LENGTH_SHORT).show()
        }
    }

}