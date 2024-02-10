## Notes

Приложение-клон оригинальных iOS заметок

#### Технологический стек

- Kotlin
- Jetpack Compose
- Room database
- Dagger/Hilt
- Flow
- Navigation Compose
- WorkManager

#### Архитектура

Используется укороченная версия MVVM архитектуры, где ViewModel имеет прямую связь к базе данных.
Для внедрения зависимостей используется Dagger/Hilt, где часть функций взята из Dagger и часть из Hilt

Само приложение разделено по папкам:
- domain - хранятся классы с общим кодом, который может вызываться из любой части приложения
- ui - хранится UI/UX приложения, ViewModel и все что связано с Presenter частью
- db - база данных, а также модели и DAO
- di - хранится модуль для генерации зависимостей и приложение(Application)

### Фото


![photo_2024-02-10_16-11-24](https://github.com/akurbanoff/Notes/assets/113118862/a0539686-2feb-4a85-9ebc-193d4a41f48b)
![photo_2024-02-10_16-11-25](https://github.com/akurbanoff/Notes/assets/113118862/f4ae662a-69b9-4544-85c2-1b23049d29bf)
![photo_2024-02-10_16-11-26](https://github.com/akurbanoff/Notes/assets/113118862/b8756483-c8cd-4094-ac8a-d7c5872288ae)
![photo_2024-02-10_16-11-28](https://github.com/akurbanoff/Notes/assets/113118862/7232233b-284f-4f02-912c-ce44c9a1ddc8)
