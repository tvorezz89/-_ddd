import type { AppError } from './error';
import { GeneralErrors } from './general-errors';

const EMPTY_UUID = '00000000-0000-0000-0000-000000000000';

export class Guard {
  private constructor() {}

  static combine(...errors: Array<AppError | null | undefined>): AppError | null {
    return errors.find((error) => error != null) ?? null;
  }

  static againstNullOrEmpty(value: string | readonly unknown[] | null | undefined, paramName: string): AppError | null {
    if (value == null || (typeof value === 'string' ? value.trim() === '' : value.length === 0)) {
      return GeneralErrors.valueIsRequired(paramName);
    }

    return null;
  }

  static againstNullOrEmptyUuid(uuid: string | null | undefined, paramName: string): AppError | null {
    if (uuid == null || uuid === EMPTY_UUID) {
      return GeneralErrors.valueIsRequired(paramName);
    }

    return null;
  }

  static againstGreaterThan<T>(value: T | null | undefined, max: T, paramName: string, compare = defaultCompare<T>): AppError | null {
    if (value == null || compare(value, max) > 0) {
      return GeneralErrors.valueMustBeLessThan(paramName, value, max);
    }

    return null;
  }

  static againstGreaterOrEqual<T>(value: T | null | undefined, max: T, paramName: string, compare = defaultCompare<T>): AppError | null {
    if (value == null || compare(value, max) >= 0) {
      return GeneralErrors.valueMustBeLessOrEqual(paramName, value, max);
    }

    return null;
  }

  static againstLessThan<T>(value: T | null | undefined, min: T, paramName: string, compare = defaultCompare<T>): AppError | null {
    if (value == null || compare(value, min) < 0) {
      return GeneralErrors.valueMustBeLessThan(paramName, value, min);
    }

    return null;
  }

  static againstLessOrEqual<T>(value: T | null | undefined, min: T, paramName: string, compare = defaultCompare<T>): AppError | null {
    if (value == null || compare(value, min) <= 0) {
      return GeneralErrors.valueMustBeGreaterOrEqual(paramName, value, min);
    }

    return null;
  }

  static againstOutOfRange<T>(
    value: T | null | undefined,
    min: T,
    max: T,
    paramName: string,
    compare = defaultCompare<T>,
  ): AppError | null {
    if (value == null || compare(value, min) < 0 || compare(value, max) > 0) {
      return GeneralErrors.valueIsOutOfRange(paramName, value, min, max);
    }

    return null;
  }
}

function defaultCompare<T>(left: T, right: T): number {
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
