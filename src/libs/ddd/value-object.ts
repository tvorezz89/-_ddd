export abstract class ValueObject<T extends ValueObject<T>> {
  protected abstract equalityComponents(): Iterable<unknown>;

  equals(other: unknown): boolean {
    if (this === other) {
      return true;
    }

    if (other == null || this.constructor !== other.constructor) {
      return false;
    }

    const thisComponents = Array.from(this.equalityComponents());
    const otherComponents = Array.from((other as ValueObject<T>).equalityComponents());

    if (thisComponents.length !== otherComponents.length) {
      return false;
    }

    return thisComponents.every((component, index) => Object.is(component, otherComponents[index]));
  }

  compareTo(other: T): number {
    const thisComponents = Array.from(this.equalityComponents());
    const otherComponents = Array.from(other.equalityComponents());

    for (let index = 0; index < thisComponents.length && index < otherComponents.length; index += 1) {
      const result = safeCompare(thisComponents[index], otherComponents[index]);
      if (result !== 0) {
        return result;
      }
    }

    return thisComponents.length - otherComponents.length;
  }

  toString(): string {
    return `${this.constructor.name}[${Array.from(this.equalityComponents()).join(', ')}]`;
  }
}

function safeCompare(left: unknown, right: unknown): number {
  if (left === right) {
    return 0;
  }

  if (left == null) {
    return -1;
  }

  if (right == null) {
    return 1;
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

  throw new Error('Fields must be comparable');
}
