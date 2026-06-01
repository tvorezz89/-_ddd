import type { Aggregate } from './aggregate';

export interface DomainEventPublisher {
  publish(aggregates: Iterable<Aggregate<unknown>>): void | Promise<void>;
}
