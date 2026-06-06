import { Module } from '@nestjs/common';
import { OrderModule } from './order/order.module';
import { CourierModule } from './courier/courier.module';

@Module({
  imports: [OrderModule, CourierModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
