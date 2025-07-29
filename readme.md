# Система управления банковскими картами

### Описание проекта
**Bank System** - это RESTful API для управления банковскими картами с системой аутентификации и авторизации. Проект предоставляет функционал для администраторов и пользователей по управлению и переводами между картами, а также просмотру информации.

### Основные возможности:
- **Для администраторов**:
    - Создание/блокировка/активация/удаление карт
    - Управление пользователями
    - Просмотр всех карт в системе

- **Для пользователей**:
    - Просмотр своих карт с пагинацией
    - Запрос на блокировку карты
    - Переводы между своими картами
    - Просмотр баланса

## Технологии
| Технология         | Версия   |
|--------------------|----------|
| Java               | 17       |
| Spring Boot        | 3.5.3    |
| PostgreSQL         | 15 |
| Liquibase          | 4.31.1   |
| Springdoc OpenAPI  | 2.8.9    |

## Запуск проекта с помощью Docker

Запуск осуществляется при помощи docker-compose.yml файла, где в разделе `environment` настраиваются подключение к базе данных и шифрование/маскировка:
```dockerfile
name: Bank_REST

services:
  postgres:
    image: postgres:15
    container_name: postgres-db
    restart: always
    environment:
      POSTGRES_DB: app_db
      POSTGRES_USER: app_user
      POSTGRES_PASSWORD: app_password
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  bank-rest-app:
    image: tokarevv3/bank_rest:latest
    container_name: bank-rest-app
    depends_on:
      - postgres
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/app_db
      SPRING_DATASOURCE_USERNAME: app_user
      SPRING_DATASOURCE_PASSWORD: app_password

      CUSTOM_JWT_SECRET: secret
      CUSTOM_JWT_EXPIRATION: 86400000
      CUSTOM_ENCRYPTION_ALGORITHM: AES
      CUSTOM_ENCRYPTION_SECRET: 1234567890123456

volumes:
  postgres-data:
```
### ⚡ Быстрый старт
Быстрый старт приложения, где параметры окружения установлены по умолчанию:
```bash
git clone https://github.com/tokarevv3/Bank_REST.git
cd Bank_REST
docker compose up --build -d
```
## 📚 Доступ к Swagger UI

После успешного запуска приложения интерактивная документация API будет доступна по адресу:

🔗 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui/index.html)
