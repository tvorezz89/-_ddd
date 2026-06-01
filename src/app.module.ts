import { EventEmitter } from 'node:events';
import { Module } from '@nestjs/common';
import { ConfigModule, ConfigService } from '@nestjs/config';
import { TypeOrmModule } from '@nestjs/typeorm';
import { appConfig, type AppConfig } from './config/app-config';
import { AppController } from './app.controller';
import { DefaultDomainEventPublisher, DOMAIN_EVENT_EMITTER } from './default-domain-event-publisher';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true, load: [appConfig] }),
    TypeOrmModule.forRootAsync({
      inject: [ConfigService],
      useFactory: (configService: ConfigService<AppConfig, true>) => {
        const database = configService.get('database', { infer: true });

        return {
          type: 'postgres' as const,
          host: database.host,
          port: database.port,
          username: database.username,
          password: database.password,
          database: database.name,
          autoLoadEntities: true,
          synchronize: true,
          logging: true,
          ssl: database.sslmode === 'disable' ? false : { rejectUnauthorized: false },
        };
      },
    }),
  ],
  controllers: [AppController],
  providers: [
    {
      provide: DOMAIN_EVENT_EMITTER,
      useFactory: () => new EventEmitter(),
    },
    {
      provide: DefaultDomainEventPublisher,
      inject: [DOMAIN_EVENT_EMITTER],
      useFactory: (emitter: EventEmitter) => new DefaultDomainEventPublisher(emitter),
    },
  ],
  exports: [DefaultDomainEventPublisher, DOMAIN_EVENT_EMITTER],
})
export class AppModule {}
