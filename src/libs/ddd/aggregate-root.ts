import type { DomainEvent } from './domain-event';

export interface AggregateRoot<TId> {
  readonly id?: TId;
  getDomainEvents(): readonly DomainEvent[];
  clearDomainEvents(): void;
}
