# Samokat API Autotests

[![tests](https://github.com/TatyanaKovalewa/samokat-api-autotests/actions/workflows/tests.yml/badge.svg)](https://github.com/TatyanaKovalewa/samokat-api-autotests/actions/workflows/tests.yml)
[![Java](https://img.shields.io/badge/Java-11-orange)](https://openjdk.org/projects/jdk/11/)
[![REST Assured](https://img.shields.io/badge/REST%20Assured-5DA5DA)](https://rest-assured.io/)
[![Allure](https://img.shields.io/badge/Allure-report-FF6A00)](https://tatyanakovalewa.github.io/samokat-api-autotests/)

Автотесты API для сервиса аренды самокатов [qa-scooter.praktikum-services.ru](https://qa-scooter.praktikum-services.ru/)

**35 тестов.** Прогоняются в CI при каждом пуше, Allure-отчёт публикуется автоматически:

**👉 [Открыть Allure-отчёт](https://tatyanakovalewa.github.io/samokat-api-autotests/)**

⚠️ **5 тестов стабильно падают — и это найденные дефекты стенда, а не ошибки в тестах.** Подробности ниже.

---

## 📋 О проекте

Проект содержит автоматизированные API-тесты для сервиса "Самокат" — проверка функциональности курьеров и заказов.

Реализованы проверки позитивных и негативных сценариев работы с API.

### **Что тестируется:**
- ✅ Создание, авторизация и удаление курьера
- ✅ Создание заказа с разными вариантами цвета (BLACK / GREY)
- ✅ Получение заказа по трек-номеру
- ✅ Получение списка заказов
- ✅ Принятие заказа курьером
- ✅ Отмена заказа
- ✅ Негативные сценарии (пустые поля, невалидные данные, дубликаты)

---

## 🛠️ Стек технологий

| Технология | Версия |
|------------|--------|
| Java | 11 |
| JUnit | 4.13.2 |
| RestAssured | 4.4.0 |
| Allure | 2.15.0 |
| Lombok | 1.18.30 |
| Maven | - |

---

## 🧪 Postman-коллекция

В репозитории также доступна Postman-коллекция для ручного тестирования API Самоката. Она содержит базовые запросы по всем ключевым эндпоинтам и может использоваться для быстрой проверки API до запуска автотестов.

📄 [Скачать коллекцию](./postman/Yandex.Samokat.postman_collection.json)

### Как использовать:
1. Открой Postman → **Import** → выбери файл `Yandex.Samocat.postman_collection.json`
2. Установи переменную окружения `{{server_url}}` = `https://qa-scooter.praktikum-services.ru`
3. Отправляй запросы в любой последовательности

---

## 📁 Структура проекта
```
src/
├── main/
│ └── java/
│ ├── models/
│ │ ├── Courier.java # Модель курьера
│ │ ├── Order.java # Модель заказа
│ │ ├── CancelOrderRequest.java # Запрос на отмену заказа
│ │ └── CourierLoginRequest.java # Запрос на авторизацию
│ └── steps/
│   ├── CourierSteps.java # Шаги для работы с курьерами
│   ├── OrderSteps.java  # Шаги для работы с заказами
│   └── OrderAcceptSteps.java # Шаги для принятия заказа
│
└── test/
  └── java/
    ├── Config.java # Конфигурация (базовый URL)
    ├── CourierCreateTest.java # Тесты создания курьера
    ├── CourierLoginTest.java # Тесты авторизации курьера
    ├── CourierDeleteTest.java # Тесты удаления курьера
    ├── OrderCreateTest.java # Параметризованные тесты создания заказа
    ├── OrderGetTest.java # Тесты получения заказа по треку
    ├── OrdersListTest.java # Тесты получения списка заказов
    └── OrderAcceptTest.java # Тесты принятия заказа курьером
```

---

## 🚀 Запуск тестов

**Все тесты**

```bash
mvn clean test
```

Конкретный класс

```bash
mvn test -Dtest=CourierCreateTest
```

Генерация Allure-отчета

```bash
mvn allure:report
```

---

## 🧪 Тестовые сценарии

### 1. Курьеры (CourierCreateTest / CourierLoginTest / CourierDeleteTest)

- ✅ Создание курьера со всеми обязательными полями
- ✅ Ошибка при создании дубликата
- ✅ Ошибка при отсутствии логина/пароля (null и пустая строка)
- ✅ Успешная авторизация с валидными данными
- ✅ Ошибка при авторизации с неверным логином/паролем
- ✅ Успешное удаление курьера
- ✅ Ошибка при удалении с несуществующим id

### 2. Заказы (OrderCreateTest)

- ✅ Создание заказа с цветом BLACK
- ✅ Создание заказа с цветом GREY
- ✅ Создание заказа с обоими цветами
- ✅ Создание заказа без указания цвета

### 3. Получение заказов (OrderGetTest / OrdersListTest)

- ✅ Получение заказа по трек-номеру
- ✅ Ошибка при запросе без номера заказа
- ✅ Ошибка при несуществующем трек-номере
- ✅ Получение списка всех заказов

### 4. Принятие заказа (OrderAcceptTest)

- ✅ Успешное принятие заказа курьером
- ✅ Ошибка при отсутствии id курьера
- ✅ Ошибка при неверном id курьера
- ✅ Ошибка при отсутствии номера заказа
- ✅ Ошибка при неверном номере заказа

---

## 📊 Allure-отчеты
После запуска тестов отчет доступен в директории:

``` 
target/site/allure-maven-plugin/index.html
```

---

## 🐞 Найденные дефекты стенда

Тесты написаны по спецификации API. Пять из них стабильно падают, потому что сервер ведёт себя не так, как описано в документации. Это результат тестирования, а не сломанные тесты — поэтому они оставлены в наборе и продолжают прогоняться.

| Тест | Ожидается | Фактически |
|------|-----------|------------|
| `CourierLoginTest#loginRequiresPassword` | `400 Bad Request` | висит 60 с, затем `504` |
| `CourierLoginTest#loginWithEmptyBodyReturnsError` | `400 Bad Request` | висит 60 с, затем `504` |
| `CourierLoginTest#loginWithoutPasswordFieldReturnsError` | `400 Bad Request` | висит 60 с, затем `504` |
| `CourierDeleteTest#cannotDeleteCourierWithoutId` | `400 Bad Request` | `404 Not Found` |
| `OrderAcceptTest#cannotAcceptOrderWithoutOrderId` | `400 Bad Request` | `404 Not Found` |

**Первые три — самый серьёзный дефект:** запрос авторизации без пароля не отбивается валидацией, а уходит вглубь и подвешивает обработчик на минуту. Это не только несоответствие спецификации, но и риск исчерпания пула соединений.

Оформленные баг-репорты со всеми шагами и контрольными запросами:
[BUG1 — авторизация без пароля](https://github.com/TatyanaKovalewa/TatyanaKovalewa/blob/main/bug-reports/BUG1-login-bez-parolya-504.md) ·
[BUG2 — удаление курьера без id](https://github.com/TatyanaKovalewa/TatyanaKovalewa/blob/main/bug-reports/BUG2-udalenie-kuriera-bez-id.md) ·
[BUG3 — принятие заказа без id](https://github.com/TatyanaKovalewa/TatyanaKovalewa/blob/main/bug-reports/BUG3-prinyatie-zakaza-bez-id.md)

**Как это учтено в CI.** Список зафиксирован в [`.github/known-stand-defects.txt`](.github/known-stand-defects.txt), а скрипт [`.github/check-known-failures.py`](.github/check-known-failures.py) сверяет с ним каждый прогон:

- падение из списка — ожидаемо, сборка проходит;
- падение вне списка — сборка падает, нужен разбор;
- тест из списка начал проходить — значит стенд починили, строку пора удалить.

Так зелёный бейдж означает «новых проблем нет», а не «всё идеально» — и при этом ни один найденный дефект не спрятан.

Полный прогон занимает около 10 минут — три теста на логин без пароля висят по минуте каждый, и это часть самого дефекта.

Нестабильные падения на учебном стенде отсеиваются перезапуском (`rerunFailingTestsCount=2` в surefire): тест, прошедший со второй попытки, помечается как flaky и не роняет сборку.

---

## 📝 Примечания

- После каждого теста выполняется автоматическая очистка созданных данных

- Используется параметризация для тестирования различных комбинаций цветов

---

## 📄 Лицензия
Проект создан в образовательных целях в рамках обучения автоматизации тестирования.
