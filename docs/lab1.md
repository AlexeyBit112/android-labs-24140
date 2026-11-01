# Лабораторная работа 1

Харлов Алексей, группа 24140.

## Задача

Создать приложение на Kotlin с надписью Hello World.

## Реализация

Экран описан в XML. В `MainActivity` метод `onCreate` вызывает `setContentView` и загружает эту разметку. Приветствие выводится в обычном `TextView`.

```kotlin
package ru.kharlov.lab1

import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
```

Исходники: [MainActivity](../lab1/src/main/java/ru/kharlov/lab1/MainActivity.kt), [XML-макет](../lab1/src/main/res/layout/activity_main.xml).

Запуск в Android Studio: [код и приложение в эмуляторе](screenshots/studio/lab1.png).

## Результат

При запуске появляется экран с приветствием и подписью студента. Тест проверяет текст Hello World.

<img src="screenshots/lab1/01-hello-world.png" width="320" alt="Экран приложения Hello World">

## Вывод

Разметка экрана хранится в XML, а `setContentView` подключает её в Activity.
