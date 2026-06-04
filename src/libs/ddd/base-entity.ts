export abstract class BaseEntity<TId> {
  protected constructor(public readonly id?: TId) {}

  protected isTransient(): boolean {
    return this.id == null || this.id === this.defaultValue();
  }

  protected defaultValue(): TId | undefined {
    return undefined;
  }

  equals(other: unknown): boolean {
    if (other == null) {
      return false;
    }

    if (this === other) {
      return true;
    }

    if (!(other instanceof BaseEntity)) {
      return false;
    }

    if (this.constructor !== other.constructor) {
      return false;
    }

    if (this.isTransient() || other.isTransient()) {
      return false;
    }

    return this.id === other.id;
  }

  compareTo(other: BaseEntity<TId> | null | undefined, compare = defaultCompare<TId>): number {
    if (other == null) {
      return 1;
    }

    if (this === other) {
      return 0;
    }

    return compare(this.id as TId, other.id as TId);
  }
}

function defaultCompare<T>(left: T, right: T): number {
  if (left === right) {
    return 0;
  }

  if (typeof left === 'number' && typeof right === 'number') {
    return left - right;
  }

  if (typeof left === 'string' && typeof right === 'string') {
    return left.localeCompare(right);
  }

  if (left instanceof Date && right instanceof Date) {
    return left.getTime() - right.getTime();
  }

  throw new Error('Entity ids must be comparable');
}
