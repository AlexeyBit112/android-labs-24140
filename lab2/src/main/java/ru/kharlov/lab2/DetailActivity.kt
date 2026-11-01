package ru.kharlov.lab2

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity

class DetailActivity : ComponentActivity() {
    companion object { const val EXTRA_MESSAGE = "EXTRA_MESSAGE" }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Нет данных"
        findViewById<TextView>(R.id.received).text = "Получено: $message"
        // Одинаковый результат для кнопки и системного возврата.
        setResult(RESULT_OK)
        findViewById<Button>(R.id.back).setOnClickListener { finish() }
    }
}
