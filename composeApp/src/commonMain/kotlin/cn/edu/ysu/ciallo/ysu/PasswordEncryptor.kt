package cn.edu.ysu.ciallo.ysu

import korlibs.crypto.AES
import korlibs.crypto.Padding
import korlibs.crypto.SecureRandom
import korlibs.crypto.encoding.Base64

object PasswordEncryptor {
    private const val AES_CHARS = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678"
    private const val AES_CHARS_LENGTH = AES_CHARS.length

    private fun randomString(length: Int): String {
        val sb = StringBuilder(length)
        repeat(length) {
            val idx = SecureRandom.nextInt(AES_CHARS_LENGTH)
            sb.append(AES_CHARS[idx])
        }
        return sb.toString()
    }

    fun encryptPassword(password: String, salt: String): String {
        val prefix = randomString(64)
        val iv = randomString(16)
        val plain = (prefix + password).encodeToByteArray()
        val key = salt.trim().encodeToByteArray()
        val ivBytes = iv.encodeToByteArray()
        val encrypted = AES.encryptAesCbc(plain, key, ivBytes, Padding.PKCS7Padding)
        return Base64.encode(encrypted)
    }
}

