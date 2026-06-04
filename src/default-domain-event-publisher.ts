import { EventEmitter } from 'node:events';
import { Injectable } from '@nestjs/common';
import type { Aggregate, DomainEvent, DomainEventPublisher } from './libs/ddd';

export const DOMAIN_EVENT_EMITTER = Symbol('DOMAIN_EVENT_EMITTER');

@Injectable()
export class DefaultDomainEventPublisher implements DomainEventPublisher {
  constructor(private readonly emitter: EventEmitter) {}

  publish(aggregates: Iterable<Aggregate<unknown>>): void {
    for (const aggregate of aggregates) {
      for (const event of aggregate.getDomainEvents()) {
        this.publishEvent(event);
      }
    }
  }

  private publishEvent(event: DomainEvent): void {
    this.emitter.emit(event.constructor.name, event);
  }
}
