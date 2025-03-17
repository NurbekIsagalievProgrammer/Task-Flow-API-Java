# Task Flow API

REST API сервис для управления задачами и командной работы. Позволяет организовать процесс распределения и выполнения задач между сотрудниками с разграничением прав доступа.

## Технологии

- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- PostgreSQL
- Swagger/OpenAPI
- Docker

## Запуск проекта

1. Убедитесь, что у вас установлены:
   - Java 17
   - Maven
   - Docker и Docker Compose

2. Клонируйте репозиторий:
   ```bash
   git clone <repository-url>
   cd task-management-system
   ```

3. Запустите базу данных:
   ```bash
   docker-compose up -d
   ```

4. Запустите приложение:
   ```bash
   mvn spring-boot:run
   ```

5. Запуск тестов:
   ```bash
   mvn test
   ```

6. Приложение будет доступно по адресу: http://localhost:8080
   Swagger UI: http://localhost:8080/swagger-ui.html

## API Endpoints

### Аутентификация
- POST /api/v1/auth/register - Регистрация нового пользователя
- POST /api/v1/auth/authenticate - Аутентификация пользователя
- PUT /api/v1/auth/users/{id}/make-admin - Повышение пользователя до админа (только админ)

### Задачи
- POST /api/v1/tasks - Создание новой задачи (только админ)
- GET /api/v1/tasks - Получение списка задач
- GET /api/v1/tasks/{id} - Получение задачи по ID
- PUT /api/v1/tasks/{id} - Обновление задачи (только админ)
- PATCH /api/v1/tasks/{id}/status - Обновление статуса задачи
- DELETE /api/v1/tasks/{id} - Удаление задачи (только админ)

### Комментарии
- POST /api/v1/tasks/{taskId}/comments - Добавление комментария к задаче
- GET /api/v1/tasks/{taskId}/comments - Получение комментариев задачи

## Роли и права доступа

### Администратор
- Создание новых задач
- Редактирование всех задач
- Назначение исполнителей
- Удаление задач
- Управление статусами и приоритетами
- Добавление комментариев
- Повышение пользователей до админа

### Пользователь
- Просмотр назначенных задач
- Обновление статуса назначенных задач
- Добавление комментариев к назначенным задачам

## Примеры запросов

### Регистрация
```json
POST /api/v1/auth/register
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Аутентификация
```json
POST /api/v1/auth/authenticate
{
  "email": "john@example.com",
  "password": "password123"
}
```

### Создание задачи (админ)
```json
POST /api/v1/tasks
{
  "title": "Новая задача",
  "description": "Описание задачи",
  "priority": "HIGH",
  "assigneeId": 2
}
```

### Обновление статуса
```json
PATCH /api/v1/tasks/{id}/status?status=IN_PROGRESS
```

## Безопасность
Все защищенные endpoints требуют JWT токен в заголовке:
```
Authorization: Bearer your_jwt_token
```

## Первоначальная настройка

### Подключение к базе данных

1. После запуска Docker контейнера с PostgreSQL:
   ```bash
   docker-compose up -d
   ```

2. Подключение к PostgreSQL через командную строку:
   ```bash
   # Подключение к контейнеру
   docker exec -it testjava_postgres_1 bash

   # Внутри контейнера подключаемся к БД
   psql -U postgres -d task_management
   ```

   Или одной командой:
   ```bash
   docker exec -it testjava_postgres_1 psql -U postgres -d task_management
   ```

3. Полезные команды в psql:
   ```sql
   -- Показать все таблицы
   \dt

   -- Показать структуру таблицы users
   \d users
   
   -- Выйти из psql
   \q
   ```

### Создание первого администратора

1. После подключения к БД выполните SQL-запрос:
   ```sql
   INSERT INTO users (email, password, first_name, last_name, role)
   VALUES (
       'admin@example.com',
       '$2a$10$vYQNGG0kI2vG1.U8qX1WqOEPm6r3o3.oqgDxqUh.TZhJGrqgHxKi2', -- пароль: admin123
       'Admin',
       'User',
       'ADMIN'
   );
   ```

2. Проверьте что админ создан:
   ```sql
   SELECT * FROM users;
   ```

3. После этого можно войти через API с учетными данными:
   - Email: admin@example.com
   - Password: admin123

Этот администратор сможет создавать задачи и назначать других администраторов через API.

## Ключевые возможности

- Управление задачами (создание, редактирование, удаление)
- Система приоритетов и статусов
- Комментирование задач
- Ролевая модель (администраторы и пользователи)
- JWT аутентификация
- Документированное API (Swagger) 