# Лабораторные работы по Android

Харлов Алексей, группа 24140.

Каждая лабораторная находится в отдельном модуле и устанавливается как отдельное приложение. Отчёты размещены здесь же, в Markdown: их можно читать прямо на GitHub.

| Работа | Реализация | Отчёт |
|---|---|---|
| 1 | Hello World, Kotlin и XML | [Лаба 1](docs/lab1.md) |
| 2 | Форма с проверкой ввода, переход между Activity и Intent | [Лаба 2](docs/lab2.md) |
| 3 | SharedPreferences, приватный файл и RecyclerView с удалением | [Лаба 3](docs/lab3.md) |
| 4, часть 1 | SQLiteOpenHelper, CRUD и RecyclerView | [Лаба 4](docs/lab4.md) |

## Готовые APK

[Скачать четыре debug APK](https://github.com/AlexeyBit112/android-labs-24140/releases/tag/v1.0). Контрольные суммы — в [apk-sha256.txt](docs/apk-sha256.txt). Для чтения исходников и отчётов установка APK не нужна.

## Сборка и запуск

Нужны JDK 17 и Android SDK: платформа 34, Build Tools 34.0.0, Platform Tools. Минимальная версия устройства — Android 7.0 (API 24). Версии сборки закреплены: Gradle 8.7, Android Gradle Plugin 8.5.2, Kotlin 1.9.24 и RecyclerView 1.3.2.

Открыть корневую папку в Android Studio, дождаться синхронизации, выбрать модуль `lab1`, `lab2`, `lab3` или `lab4` и нажать Run. Для командной строки задать `ANDROID_HOME` или записать путь к SDK в локальный `local.properties`:

```properties
sdk.dir=/path/to/Android/sdk
```

```bash
./gradlew assembleDebug lintDebug
./gradlew connectedDebugAndroidTest
./scripts/collect-screenshots.sh
python3 scripts/check-persistence.py
```

Вторая команда требует работающего эмулятора или подключённого устройства. Тестовые приложения остаются установленными для сбора скриншотов и проверки перезапуска. APK появляются в `<модуль>/build/outputs/apk/debug/`. Для установки, например:

```bash
adb install -r lab4/build/outputs/apk/debug/lab4-debug.apk
```

## Что проверяется

В каждом модуле есть инструментальный тест `LabScenarioTest`. Он выполняет основные действия в интерфейсе, проверяет результат и сохраняет настоящие снимки экрана в каталог приложения `files/screenshots` на внешнем хранилище. Снимки для отчётов находятся в `docs/screenshots/`. Сведения о выполненных проверках — в [журнале проверки](docs/verification.md).

## Основания для работ

Лабы 2–4 соответствуют предоставленным заданиям `Zadanie_na_labaratornuyu_2.docx`, `Labaratornaya_3.docx` и `Labaratornaya_4_chast_1.docx`. В документе лабы 3 встречается пример имени проекта `Lab4_Storage`; здесь нумерация взята из имени самого задания.

Для лабы 1 предоставлена ссылка на [введение в Kotlin](https://developer.android.com/courses/pathways/android-basics-compose-unit-1-pathway-1) и уточнение про Hello World. Поэтому этот модуль оформлен как базовое приложение с приветствием. Отдельного задания для лабы 1 нет. Темы практических работ в присланном фрагменте переписки не перечислены.
