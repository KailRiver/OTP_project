# OTPService

Сервис для генерации, отправки и верификации одноразовых паролей (OTP) с поддержкой многоканальной доставки и JWT-аутентификацией.

## Описание

OTPService - это Spring Boot приложение, предоставляющее REST API для:
- Генерации OTP кодов с настраиваемыми параметрами
- Отправки кодов через SMS (SMPP), Email, Telegram и сохранения в файл
- Верификации полученных кодов
- Управления пользователями и ролями (USER/ADMIN)
- Настройки параметров OTP (длина, время жизни)

## Технологии

- **Язык**: Java 17
- **Фреймворки**:
   - Spring Boot 3.4.4
   - Spring Security
   - Spring Data JPA
- **База данных**: PostgreSQL
- **Протоколы/API**:
   - SMPP (OpenSMPP) для SMS
   - Telegram Bot API
   - SMTP для Email
- **Аутентификация**: JWT
- **Документация**: SpringDoc OpenAPI 3.0
- **Логирование**: SLF4J + Logback
- **Тестирование**: JUnit 5, Testcontainers

## Требования

- JDK 17+
- PostgreSQL 14+
- SMPP сервер (для SMS)
- Telegram бот (для Telegram уведомлений)
- SMTP сервер (для Email)

## Установка и запуск

1. Клонируйте репозиторий:
```bash
git clone git@github.com:KailRiver/OTP_project.git
```

2. Настройте базу данных (Docker):
```bash
docker-compose up -d postgres
```

3. Настройте конфигурацию в `application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/otp_db
    username: otp_user
    password: otp_password

telegram:
  bot:
    token: YOUR_TELEGRAM_BOT_TOKEN
    chat-id: YOUR_CHAT_ID

smpp:
  host: smpp-server
  port: 2775
```

4. Соберите и запустите приложение:
```bash
./gradlew build
./gradlew bootRun
```

## API Документация

После запуска доступны:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI спецификация: `http://localhost:8080/v3/api-docs`

## Примеры запросов

### 1. Регистрация пользователя
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "securePass123!",
    "role": "USER",
    "email": "user@example.com",
    "phoneNumber": "+79123456789"
  }'
```

### 2. Аутентификация
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "securePass123!"
  }'
```

### 3. Генерация OTP
```bash
curl -X POST http://localhost:8080/otp \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "operationId": "payment-123"
  }'
```

### 4. Верификация OTP
```bash
curl -X POST http://localhost:8080/otp/validate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "A1B2C3"
  }'
```

## Администрирование

### Настройка параметров OTP (только для ADMIN)
```bash
curl -X PUT http://localhost:8080/admin/config \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "codeLength": 6,
    "ttlSeconds": 300
  }'
```

### Получение списка пользователей
```bash
curl -X GET http://localhost:8080/admin/users \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

## Конфигурация

Основные параметры в `application.yaml`:

```yaml
otp:
  default-code-length: 6
  default-ttl-seconds: 300
  check-expired-delay-ms: 60000

jwt:
  secret: "your-256-bit-secret"
  expiration-ms: 3600000
```

## Безопасность

- Все пароли хранятся в виде BCrypt хешей
- JWT токены с ограниченным временем жизни
- CSRF защита
- CORS политики
- Защита эндпоинтов по ролям

## Логирование

Настроено логирование ключевых событий:
- Аутентификация/авторизация
- Операции с OTP
- Интеграционные вызовы
- Ошибки и исключения

Пример конфигурации в `logback.xml`:
```xml
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/otp-service.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## Тестирование

Запуск тестов:
```bash
./gradlew test
```

Тестовая конфигурация использует H2 in-memory БД (`application-test.yaml`).

## Развертывание

Docker образ:
```bash
docker build -t otpservice .
docker run -p 8080:8080 otpservice
```

## Лицензия

MIT License. Подробности в файле LICENSE.