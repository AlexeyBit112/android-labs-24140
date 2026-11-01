package ru.kharlov.lab2

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import android.content.Intent

class MainActivity : ComponentActivity() {
    private var feedback: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val name = findViewById<EditText>(R.id.name)
        val age = findViewById<EditText>(R.id.age)
        val agree = findViewById<CheckBox>(R.id.agree)
        findViewById<Button>(R.id.submit).setOnClickListener {
            val enteredName = name.text.toString().trim()
            val enteredAge = age.text.toString().trim().toIntOrNull()
            val message = when {
                enteredName.isEmpty() -> "Введите имя"
                enteredAge == null || enteredAge !in 0..150 -> "Введите возраст от 0 до 150"
                !agree.isChecked -> "Примите правила"
                else -> "Имя: $enteredName, Возраст: $enteredAge, Правила: Да"
            }
            findViewById<TextView>(R.id.result).text = message
            feedback?.cancel()
            feedback = Toast.makeText(this, message, Toast.LENGTH_LONG).also { it.show() }
        }
        findViewById<Button>(R.id.navigation).setOnClickListener {
            startActivity(Intent(this, NavigationActivity::class.java))
        }
    }
    override fun onStop() {
        feedback?.cancel()
        super.onStop()
    }
}
