import { describe, expect, it } from 'vitest';
import { ValueObject } from '../src/libs/ddd';

class Location extends ValueObject<Location> {
  constructor(
    private readonly x: number,
    private readonly y: number,
  ) {
    super();
  }

  protected equalityComponents(): Iterable<unknown> {
    return [this.x, this.y];
  }
}

describe('ValueObject', () => {
  it('compares by equality components', () => {
    expect(new Location(1, 2).equals(new Location(1, 2))).toBe(true);
    expect(new Location(1, 2).equals(new Location(2, 1))).toBe(false);
  });
});
