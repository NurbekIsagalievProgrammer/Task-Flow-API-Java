# Task Flow API

REST API сервис для управления задачами и командной работы. Позволяет организовать процесс распределения и выполнения задач между сотрудниками с разграничением прав доступа.

## Технологии

- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- PostgreSQL
- Docker

## Быстрый старт

1. Запустите базу данных:
```bash
docker-compose up -d
```

2. Подключитесь к PostgreSQL и создайте первого администратора:
```sql
-- Включаем расширение для хеширования
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Создаем администратора (пароль: admin123)
INSERT INTO users (email, first_name, last_name, password, role)
VALUES (
    'admin@example.com',
    'Admin',
    'User',
    crypt('admin123', gen_salt('bf')),
    'ADMIN'
);
```

3. Запустите приложение:
```bash
mvn spring-boot:run
```

4. Войдите в систему через Swagger UI (http://localhost:8080/swagger-ui/index.html#/):
```json
POST /api/v1/auth/authenticate
{
  "email": "admin@example.com",
  "password": "admin123"
}
```

## Ключевые возможности

- Управление задачами (создание, редактирование, удаление)
- Система приоритетов и статусов
- Комментирование задач
- Ролевая модель (администраторы и пользователи)
- JWT аутентификация
- Документированное API (Swagger)

## Права доступа

### Администратор
- Создание, редактирование и удаление любых задач
- Назначение исполнителей
- Изменение статусов и приоритетов
- Управление пользователями
- Комментирование задач

### Пользователь
- Просмотр назначенных задач
- Изменение статуса своих задач
- Комментирование назначенных задач

## API Endpoints

### Аутентификация
- POST /api/v1/auth/register - Регистрация нового пользователя
- POST /api/v1/auth/authenticate - Вход в систему

### Задачи

#### Получение списка задач
```json
GET /api/v1/tasks

// Параметры пагинации в теле запроса:
{
  "page": 0,        // номер страницы (начиная с 0)
  "size": 10,       // количество задач на странице
  "sort": ["priority,desc"]  // сортировка по полю
}

// Возможные поля для сортировки:
// - id,desc/asc
// - title,desc/asc
// - priority,desc/asc
// - status,desc/asc
// - createdAt,desc/asc
```

#### Создание задачи
```json
POST /api/v1/tasks
{
  "title": "Название задачи",
  "description": "Описание задачи",
  "priority": "HIGH",  // LOW, MEDIUM, HIGH
  "assigneeId": 1      // ID существующего пользователя
}
```

#### Обновление задачи
```json
PUT /api/v1/tasks/{id}
{
  "title": "Обновленное название",
  "description": "Обновленное описание",
  "priority": "HIGH",     // LOW, MEDIUM, HIGH
  "assigneeId": 1         // ID существующего пользователя
}
```

#### Обновление статуса
```
PATCH /api/v1/tasks/{id}/status?status=IN_PROGRESS
// Статусы: PENDING, IN_PROGRESS, COMPLETED
```

#### Удаление задачи
```
DELETE /api/v1/tasks/{id}

// Успешный ответ: 204 No Content
// Возможные ошибки:
// - 404 Not Found (если задача не найдена)
// - 403 Forbidden (если нет прав на удаление)
```

### Комментарии

#### Получение комментариев к задаче
```json
GET /api/v1/tasks/{taskId}/comments












































// Возможные поля для сортировки:
// - id,desc/asc
// - content,desc/asc
// - createdAt,desc/asc
```

#### Добавление комментария
```json
POST /api/v1/tasks/{taskId}/comments
{
  "content": "Текст комментария"
}
```

#### Повышение пользователя до администратора
```
PUT /api/v1/auth/users/{id}/make-admin

// id - это ID пользователя, которого нужно сделать администратором
// Например: /api/v1/auth/users/7/make-admin сделает пользователя с ID=7 администратором

// Требуется токен администратора в заголовке:
Authorization: <jwt_token>

// Успешный ответ: 200 OK
// Возможные ошибки:
// - 404 Not Found (если пользователь не найден)
// - 403 Forbidden (если нет прав администратора)
// - 401 Unauthorized (если не передан токен)
``` 