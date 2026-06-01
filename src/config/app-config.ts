export interface AppConfig {
  http: {
    port: number;
  };
  database: {
    host: string;
    port: number;
    name: string;
    username: string;
    password: string;
    sslmode: string;
  };
  kafka: {
    bootstrapServers: string;
    consumerGroup: string;
    basketEventsTopic: string;
    orderEventsTopic: string;
  };
  grpc: {
    geoService: {
      host: string;
      port: number;
    };
  };
}

export const appConfig = (): AppConfig => ({
  http: {
    port: parseInteger(process.env.HTTP_PORT, 8082),
  },
  database: {
    host: process.env.DB_HOST ?? 'localhost',
    port: parseInteger(process.env.DB_PORT, 5432),
    name: process.env.DB_NAME ?? 'delivery',
    username: process.env.DB_USER ?? 'username',
    password: process.env.DB_PASSWORD ?? 'secret',
    sslmode: process.env.DB_SSLMODE ?? 'disable',
  },
  kafka: {
    bootstrapServers: process.env.KAFKA_HOST ?? 'localhost:9092',
    consumerGroup: process.env.KAFKA_CONSUMER_GROUP ?? 'delivery-group',
    basketEventsTopic: process.env.KAFKA_BASKET_EVENTS_TOPIC ?? 'basket.events',
    orderEventsTopic: process.env.KAFKA_ORDER_EVENTS_TOPIC ?? 'order.events',
  },
  grpc: {
    geoService: {
      host: process.env.GEO_SERVICE_GRPC_HOST ?? '0.0.0.0',
      port: parseInteger(process.env.GEO_SERVICE_GRPC_PORT, 5004),
    },
  },
});

function parseInteger(value: string | undefined, fallback: number): number {
  if (value == null || value.trim() === '') {
    return fallback;
  }

  const parsed = Number.parseInt(value, 10);
  if (Number.isNaN(parsed)) {
    throw new Error(`Expected integer environment value, got '${value}'`);
  }

  return parsed;
}
