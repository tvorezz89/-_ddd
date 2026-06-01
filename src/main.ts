import "reflect-metadata";
import { NestFactory } from "@nestjs/core";
import { ConfigService } from "@nestjs/config";
import { AppModule } from "./app.module";
import { GlobalExceptionFilter } from "./global-exception.filter";
import type { AppConfig } from "./config/app-config";

async function bootstrap(): Promise<void> {
  const app = await NestFactory.create(AppModule);
  app.useGlobalFilters(new GlobalExceptionFilter());

  const configService = app.get(ConfigService<AppConfig, true>);
  const port = configService.get("http.port", { infer: true });

  await app.listen(port);
}
