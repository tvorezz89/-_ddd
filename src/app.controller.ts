import { Controller, Get } from '@nestjs/common';

@Controller()
export class AppController {
  @Get('health')
  health(): { name: string; status: 'ok' } {
    return { name: 'delivery', status: 'ok' };
  }
}
