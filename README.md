# Учебный проект: Менеджер задач

## Описание
Учебный проект, который представляет собой менеджер задач. Реализована функциональность получения, добавления, удаления задач, а также получения истории просмотров задач посредством Http запросов.

## Работа с задачами
Получение и удаление задач производится через передачу в параметры запроса типа и идентификатора задачи, добавление – через передачу объекта в теле запроса в формате JSON.

## Архитектура
Запросы приходят в класс `HttpTaskServer`, запущенном на порте 8080, который, в зависимости от метода запроса и параметров, вызывает методы интерфейса `TaskManager`. Реализацию `TaskManager` представляет класс `HttpTaskManager`, который с помощью клиента `KVTaskClient` сохраняет задачи на сервере `KVServer`, который запущен на 8078 порте.

## Сохранение задач
Также добавлена возможность сохранения задач в файл с помощью класса `FileBackedTasksManager`.

## Тестирование
Реализованы unit-тесты.

## Системные требования
- **Язык программирования:** Java 11 или более новая версия.
- **Среда разработки:** IntelliJ IDEA или любая другая среда разработки Java.
- **Библиотеки и расширения:** 
    - apiguardian-api-1.1.2.jar
    - gson-2.9.0.jar
    - jsr305-3.0.2.jar
    - junit-jupiter-api-5.8.1.jar
    - junit-jupiter-engine-5.8.1.jar
    - junit-jupiter-params-5.8.1.jar
    - junit-platform-commons-1.8.1.jar
    - junit-platform-engine-1.8.1.jar 
    - opentest4j-1.2.0.jar 
    - spotbugs-annotations-4.0.1.jar

## Общие шаги по развертыванию
1. Клонируйте репозиторий с GitHub.
2. Установите JDK 11 или более новую версию, если она еще не установлена.
3. Добавьте все перечисленные выше JAR файлы в папку 'lib'.

## Инструкции по развертыванию в среде разработки
1. Откройте проект в среде разработки.
2. Добавьте JAR файлы к classpath проекта. Это можно сделать через настройки проекта в вашей среде разработки.
3. Запустите проект, используя инструменты среды разработки.

## Инструкции по развертыванию через консоль (для Windows)
1. Перейдите в каталог проекта с помощью команды `cd`.
2. Скомпилируйте все файлы Java в проекте, введя `javac -cp .;lib/* src/**/*.java`. Эта команда скомпилирует все файлы Java в папке `src` и всех ее подкаталогах.
3. Запустите проект, введя `java -cp .;lib/* Main`.

