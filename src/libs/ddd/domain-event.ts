import { randomUUID } from 'node:crypto';

export abstract class DomainEvent {
  public readonly eventId: string = randomUUID();
  public readonly occurredOnUtc: Date = new Date();

  protected constructor(public readonly source: unknown = 'default') {}
}
