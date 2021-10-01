package com.example.labs.pages.lab7

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.labs.R

class Lab7 : Fragment() {

    lateinit var passwords : EditText




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.lab7_frag, container, false)
        passwords = root.findViewById(R.id.passwords)


        return root
    }




    companion object {
        private const val WEB_SOCKET_URL = "wss://chatbot-ws.herokuapp.com/"
    }


}