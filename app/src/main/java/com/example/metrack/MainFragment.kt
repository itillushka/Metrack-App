package com.example.metrack

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

   override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val bodyView = view.findViewById<ImageView>(R.id.bodyView)
    bodyView.setOnClickListener {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_up_body, null)
        dialog.setContentView(dialogView)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Set the background to transparent

        // Set onClickListeners for the buttons
        val button1 = dialogView.findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            navigateToLibraryFragment(button1.text.toString())
            dialog.dismiss()
        }

        val button2 = dialogView.findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            navigateToLibraryFragment(button2.text.toString())
            dialog.dismiss()
        }

        val button3 = dialogView.findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            navigateToLibraryFragment(button3.text.toString())
            dialog.dismiss()
        }

        val button4 = dialogView.findViewById<Button>(R.id.button4)
        button4.setOnClickListener {
            navigateToLibraryFragment(button4.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }
}

private fun navigateToLibraryFragment(buttonText: String) {
    val bundle = Bundle()
    bundle.putString("buttonText", buttonText)
    findNavController().navigate(R.id.nav_graph, bundle)
}

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}