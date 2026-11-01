package ru.kharlov.lab2

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class NavigationActivity : ComponentActivity() {
    private val detail = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            findViewById<TextView>(R.id.status).text = "Возврат из второго экрана выполнен"
            Toast.makeText(this, "Вы вернулись", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        findViewById<Button>(R.id.go).setOnClickListener {
            val text = findViewById<EditText>(R.id.message).text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            detail.launch(Intent(this, DetailActivity::class.java).putExtra(DetailActivity.EXTRA_MESSAGE, text))
        }
    }
}
