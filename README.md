# Демо проект к курсу "Domain Driven Design и Clean Architecture на языке Java"
📚 Подробнее о курсе: [microarch.ru/courses/ddd/languages/java](https://microarch.ru/courses/ddd/languages/java?utm_source=gitlab&utm_medium=repository)

---

## Условия использования

Вы можете использовать и модифицировать данный код **в образовательных целях**, при условии сохранения ссылки на курс и оригинального источника.

---

# Запросы к БД
```
SELECT * public.assignments;
SELECT * FROM public.couriers;
SELECT * FROM public.orders;
SELECT * public.outbox;
```

# Очистка БД (все кроме справочников)
```
DELETE FROM public.assignments;
DELETE FROM public.couriers;
DELETE FROM public.orders;

DELETE FROM public.outbox;
```

# Генерация HTTP сервера
```
mvn clean compile
```

# Генерация gRPC клиента из Protobuf
```
mvn clean compile
```
# Генерация интеграционных событий Kafka из Protobuf
```
mvn clean compile
```

## Лицензия

Код распространяется под лицензией [MIT](./LICENSE).  
© 2025 microarch.ru

# Delivery Service TypeScript

TypeScript/NestJS analogue of the Java Spring Boot delivery service.

The original repository currently contains the service shell, application configuration, DDD primitives, result/error helpers, and protobuf contracts. This project ports those parts to TypeScript while keeping the same environment-driven defaults.

## Scripts

```bash
npm install
npm run typecheck
npm run build
npm test
npm run start:dev
```

## Environment

| Variable | Default |
| --- | --- |
| `HTTP_PORT` | `8082` |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `delivery` |
| `DB_USER` | `username` |
| `DB_PASSWORD` | `secret` |
| `DB_SSLMODE` | `disable` |
| `KAFKA_HOST` | `localhost:9092` |
| `KAFKA_CONSUMER_GROUP` | `delivery-group` |
| `KAFKA_BASKET_EVENTS_TOPIC` | `basket.events` |
| `KAFKA_ORDER_EVENTS_TOPIC` | `order.events` |
| `GEO_SERVICE_GRPC_HOST` | `0.0.0.0` |
| `GEO_SERVICE_GRPC_PORT` | `5004` |

## Endpoints

```http
GET /health
```

Returns service name and status.
