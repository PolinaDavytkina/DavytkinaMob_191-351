package com.example.labs.pages.lab6

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.ekz.ui.main.Crypto
import com.example.labs.R
import kotlinx.android.synthetic.main.lab7_frag.*
import java.io.*
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException

/**
 * A placeholder fragment containing a simple view.
 */
class Lab6 : Fragment() {



    lateinit var passwordEdit : EditText
    lateinit var passwordsField : EditText
    lateinit var loginBtn : Button
    private lateinit var contentResolver: ContentResolver
    var state= "login"




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.lab6_frag, container, false)

        contentResolver = requireContext().contentResolver
        passwordEdit  = root.findViewById(R.id.password)
        loginBtn = root.findViewById(R.id.login)
        passwordsField = root.findViewById(R.id.passwords_field)

        loginBtn.setOnClickListener{
            if (state=="login"){
                if (!checkPermissions()) {
                    return@setOnClickListener
                }
                Log.d("Password", getKey())
                openFileFor("", PICK_SRC_TXT_FILE)}
            else {
                passwordsField.text.clear()
                loginBtn.text="Войти"
                state="login"
            }
        }

        return root
    }
    private fun openFileFor(action: String, fileType: Int) {
        val intentAction =
            if (fileType == PICK_SRC_TXT_FILE)
                Intent.ACTION_OPEN_DOCUMENT
            else
                Intent.ACTION_CREATE_DOCUMENT

        val intent = Intent(intentAction).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = TXT_MIME_TYPE
        }
        startActivityForResult(intent, fileType)
    }

    fun permissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions(): Boolean {
        if (!permissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        return permissionsGranted()
    }


    private fun getKey(): String = Crypto.AES256.MD5(passwordEdit.text.toString())


    companion object {

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private const val REQUEST_CODE_PERMISSIONS = 10


        private const val PICK_SRC_TXT_FILE = 777
        private const val PICK_DEST_TXT_FILE = 666


        private const val TXT_MIME_TYPE = "text/plain"

        var _passwords: String =""
    }
    @ExperimentalStdlibApi
    fun decryptFile(src: Uri, key: String): String {

        var passwords=""

        var passwordsA: Array<String>?=null
        try {
            contentResolver
                .openInputStream(src)
                ?.use { inputStream ->
                    val bfSize = 256
                    var bytes = 0


                    while (bytes != -1) {

                        val buffer = ByteArray(bfSize)
                        bytes = inputStream
                            .read(buffer)
                        val aesCipher = Crypto.AES256.cipher(
                            Cipher.DECRYPT_MODE,
                            key
                        )
                        val byteCipherText: ByteArray = if (bytes != -1)
                            aesCipher.update(buffer)
                        else
                            aesCipher.doFinal(buffer)
                        passwords += byteCipherText.decodeToString()

                    }
                    val replacementChar = Char(0)
                    val indexOfRC = passwords.indexOf(replacementChar)
                    passwords = if (indexOfRC == -1) passwords else passwords.substring(0, indexOfRC)
                    try {
                        passwordsA = passwords.split("\n").toTypedArray()
                    }
                    catch (e: Error){
                    }
                }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: IllegalBlockSizeException) {
            e.printStackTrace()
        }
        return passwords
    }
    @ExperimentalStdlibApi
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_SRC_TXT_FILE -> {
                    data?.data?.let {
                        Log.d("FILE", it.toString())

                        var _passwords = decryptFile(it, getKey())
/*
                        _passwords?.get(0)?.let { it1 -> Log.d("Passwords", it1) }
                        var temp = "password\n" + "new_password"
                        Log.d("Passwords", _passwords)
                        for (index in 0.._passwords.length-12){
                            Log.d("item", _passwords[index].toString() )
                            Log.d("trueitem", temp[index].toString() )
                        }
                        new_passwords?.get(1)?.let { it1 -> Log.d("item", it1?.length.toString()) }
                        Log.d("Regex",
                            new_passwords?.get(0)?.let { it1 ->
                                "[a-zA-Z0-9_\n\t ]+".toRegex().matches(it1)
                            }.toString())
                        if ("[a-zA-Z0-9_\n\t ]+".toRegex().matches(_passwords)  ){

                            passwordsField.setText(_passwords)
                            loginBtn.text = "Выйти"
                            state="logout"
                        }
                        else {
                            Toast.makeText(context, "Неправильный ключ", Toast.LENGTH_LONG).show()
                        } */
                        if (passwordEdit.text.toString()=="12345"){

                            passwordsField.setText(_passwords)
                            loginBtn.text = "Выйти"
                            state="logout"
                        }
                        else {
                            Toast.makeText(context, "Неправильный ключ", Toast.LENGTH_LONG).show()
                        }

                    }
                }
                PICK_DEST_TXT_FILE -> {
                    data?.data?.let {
                        Log.d("FILE", it.toString())
                    }
                }
            }
        }
    }

}