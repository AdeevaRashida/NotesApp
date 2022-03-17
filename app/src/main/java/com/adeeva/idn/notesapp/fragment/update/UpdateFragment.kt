package com.adeeva.idn.notesapp.fragment.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.room.Update
import com.adeeva.idn.notesapp.R
import com.adeeva.idn.notesapp.data.model.NoteData
import com.adeeva.idn.notesapp.data.viewModelData.NotesViewModel
import com.adeeva.idn.notesapp.databinding.FragmentUpdateBinding
import com.adeeva.idn.notesapp.fragment.SharedViewModels
import java.util.zip.Inflater

class UpdateFragment : Fragment() {

    private val args by navArgs<UpdateFragmentArgs>()
    private val mSharedViewModels : SharedViewModels by viewModels()
    private val mNotesViewModel : NotesViewModel by viewModels()

    private var _binding : FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        binding.args = args
        binding.spUpdate.onItemSelectedListener = mSharedViewModels.listener
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_save -> updateItem()
            R.id.menu_delete -> confirmItemRemoval()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmItemRemoval() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete `${args.currentItem.title}` ?")
            .setMessage("Are you sure you want to remove `${args.currentItem.title}` ?")
            .setPositiveButton("Yes"){_, _ ->
                mNotesViewModel.deleteData(args.currentItem)
                Toast.makeText(
                    requireContext(), "Succesfully Removed : `${args.currentItem.title}`",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
            .setNegativeButton("No"){_, _ ->}
            .create()
            .show()
    }

    private fun updateItem() {
        val title = binding.etUptitle.text.toString()
        val description = binding.etDescUpdate.text.toString()
        val getPriority = binding.spUpdate.selectedItem.toString()

        val validation = mSharedViewModels.verifyDataFomUser(title, description)
        if (validation){
            val updateitem = NoteData(
                args.currentItem.id,
                title,
                mSharedViewModels.parsePriority(getPriority),
                description
            )
            mNotesViewModel.updateData(updateitem)
            Toast.makeText(requireContext(), "Succesfully Updated", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(), "Please fill in all the blanks", Toast.LENGTH_SHORT)
                .show()
        }
    }

}