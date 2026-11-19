package com.example.uturn.data

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Stores the JWT encrypted with an AES-GCM key that lives in the Android Keystore
 * (the key never leaves secure hardware/OS storage). A copy is kept in memory so
 * the OkHttp interceptor can read it instantly.
 */
object TokenStore {
    private const val KEY_ALIAS = "uturn_token_key"
    private const val PREFS_NAME = "uturn_secure"
    private const val PREF_TOKEN = "token"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12

    private var prefs: SharedPreferences? = null

    @Volatile
    private var cached: String? = null

    val token: String? get() = cached
    val hasToken: Boolean get() = !cached.isNullOrEmpty()

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = p
        cached = p.getString(PREF_TOKEN, null)?.let { decrypt(it) }
    }

    fun save(token: String) {
        cached = token
        val editor = prefs?.edit() ?: return
        val encrypted = encrypt(token)
        if (encrypted != null) editor.putString(PREF_TOKEN, encrypted) else editor.remove(PREF_TOKEN)
        editor.apply()
    }

    fun clear() {
        cached = null
        prefs?.edit()?.remove(PREF_TOKEN)?.apply()
    }

    private fun secretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    private fun encrypt(plain: String): String? = try {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val iv = cipher.iv
        val data = cipher.doFinal(plain.toByteArray(Charsets.UTF_8))
        Base64.encodeToString(iv + data, Base64.NO_WRAP)
    } catch (e: Exception) {
        null
    }

    private fun decrypt(encoded: String): String? = try {
        val bytes = Base64.decode(encoded, Base64.NO_WRAP)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            secretKey(),
            GCMParameterSpec(128, bytes.copyOfRange(0, IV_SIZE))
        )
        String(cipher.doFinal(bytes.copyOfRange(IV_SIZE, bytes.size)), Charsets.UTF_8)
    } catch (e: Exception) {
        null
    }
}