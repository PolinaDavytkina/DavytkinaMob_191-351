package com.example.ekz.ui.main

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

import java.math.BigInteger
import java.security.MessageDigest

class Crypto {

    object ChCrypto{
        @JvmStatic fun aesEncrypt(v:String, secretKey:String) = AES256.encrypt(v, secretKey)
        @JvmStatic fun aesDecrypt(v:String, secretKey:String) = AES256.decrypt(v, secretKey)
    }

    object AES256{
        fun MD5(string: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(string.toByteArray()))
                .toString(16)
                .padStart(32, '0')
        }


        private val encorder =  { bArray: ByteArray -> Base64.encode(bArray,Base64.DEFAULT)}
        private val decorder = {bArray: ByteArray -> Base64.decode(bArray,Base64.DEFAULT)}

        fun cipher(opmode:Int, secretKey:String):Cipher{
            if(secretKey.length != 32) throw RuntimeException("SecretKey length is not 32 chars")

            val c = Cipher.getInstance("AES/CBC/NoPadding")

            val sk = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), "AES")

            val iv = IvParameterSpec(secretKey.substring(0, 16).toByteArray(Charsets.UTF_8))

            c.init(opmode, sk, iv)
            return c
        }

        fun encrypt(str:String, secretKey:String):String{
            val encrypted = cipher(Cipher.ENCRYPT_MODE, secretKey).doFinal(str.toByteArray(Charsets.UTF_8))
            return String(encorder(encrypted))
        }

        fun decrypt(str:String, secretKey:String):String{
            val byteStr = decorder(str.toByteArray(Charsets.UTF_8))
            return String(cipher(Cipher.DECRYPT_MODE, secretKey).doFinal(byteStr))
        }

    }


}