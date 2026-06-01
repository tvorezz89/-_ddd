import { BaseEntity } from './base-entity';
import type { DomainEvent } from './domain-event';
import type { AggregateRoot } from './aggregate-root';

export abstract class Aggregate<TId> extends BaseEntity<TId> implements AggregateRoot<TId> {
  private domainEvents: DomainEvent[] = [];

  protected constructor(id?: TId) {
    super(id);
  }

  getDomainEvents(): readonly DomainEvent[] {
    return [...this.domainEvents];
  }

  clearDomainEvents(): void {
    this.domainEvents = [];
  }

  protected raiseDomainEvent(domainEvent: DomainEvent): void {
    this.domainEvents.push(domainEvent);
  }
}
