# Лабораторная работа 2

Харлов Алексей, группа 24140.

## Задание 1 Форма

На экране размещены поля имени и возраста, согласие с правилами и кнопка отправки. Элементы выстроены вертикально в `LinearLayout`. `ScrollView` позволяет прокручивать форму при небольшой высоте экрана или открытой клавиатуре.

Имя обрезается по краям через `trim()`. Возраст переводится в число через `toIntOrNull()` и проверяется по диапазону 0–150. Пустое имя, неподходящий возраст и отсутствие согласия прерывают отправку. Сообщение выводится в Toast и дублируется в текстовом блоке, чтобы его можно было прочитать после исчезновения Toast.

Обработка кнопки в [MainActivity](../lab2/src/main/java/ru/kharlov/lab2/MainActivity.kt):

```kotlin
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
```

Возраст использует `inputType="number"`. Тест отдельно подставляет нечисловое значение программно, чтобы проверить саму обработку, а не только ограничение клавиатуры.

| Сценарий | Ожидаемый результат |
|---|---|
| Пустое имя | Введите имя |
| Пустой возраст, 151, переполнение Int или буквы | Введите возраст от 0 до 150 |
| Корректные поля без согласия | Примите правила |
| Алексей, 20, согласие отмечено | Toast с введёнными данными |

<img src="screenshots/lab2/01-form.png" width="280" alt="Форма">
<img src="screenshots/lab2/03-invalid-age.png" width="280" alt="Ошибка возраста">
<img src="screenshots/lab2/05-success.png" width="280" alt="Правильно заполненная форма">

Остальные снимки ошибок: [пустое имя](screenshots/lab2/02-empty-name.png), [согласие](screenshots/lab2/04-consent-required.png).

## Задание 2 Навигация

Схема экранов:

```text
MainActivity (форма)
    → NavigationActivity (ввод сообщения)
        → Intent.putExtra(EXTRA_MESSAGE, text)
        → DetailActivity (получение и показ текста)
        → finish()
    ← подтверждение возврата в NavigationActivity
```

Для результата применяется Activity Result API. Он заменяет устаревший `startActivityForResult`. Ключ дополнительных данных объявлен константой в `DetailActivity`.

[Передача текста](../lab2/src/main/java/ru/kharlov/lab2/NavigationActivity.kt):

```kotlin
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
```

[Получение текста и возврат](../lab2/src/main/java/ru/kharlov/lab2/DetailActivity.kt):

```kotlin
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
```

<img src="screenshots/lab2/06-navigation-input.png" width="280" alt="Ввод сообщения">
<img src="screenshots/lab2/07-detail.png" width="280" alt="Полученное сообщение">
<img src="screenshots/lab2/08-return.png" width="280" alt="Возврат на экран ввода">

## Вывод

Проверка ввода нужна даже при числовой клавиатуре: пустая строка и слишком большое число остаются возможными. Intent передаёт данные по ключу, а `finish()` закрывает текущую Activity и возвращает управление предыдущей.
