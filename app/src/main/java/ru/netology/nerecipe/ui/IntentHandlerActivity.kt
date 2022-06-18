package ru.netology.nerecipe.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.netology.nerecipe.databinding.IntentHandlerActivityBinding

class IntentHandlerActivity : AppCompatActivity() {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val binding =
            ru.netology.nerecipe.databinding.IntentHandlerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent ?: return
        if (intent.action != Intent.ACTION_SEND) return

        val text = intent.getStringExtra(Intent.EXTRA_TEXT)
        if (text.isNullOrBlank()) return

        Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok) { finish() }
            .show()
    }
}