import { AppError } from './error';

export class GeneralErrors {
  private constructor() {}

  static notFound<T>(name: string, id: T): AppError {
    assertName(name);
    return AppError.of('record.not.found', `Record not found. Name: ${name}, id: ${id}`);
  }

  static valueIsInvalid<T>(name: string, value: T): AppError {
    assertName(name);
    return AppError.of('value.is.invalid', `Value '${value}' is invalid for ${name}`);
  }

  static valueIsRequired(name: string): AppError {
    assertName(name);
    return AppError.of('value.is.required', `Value is required for ${name}`);
  }

  static invalidLength(name: string): AppError {
    assertName(name);
    return AppError.of('invalid.string.length', `Invalid ${name} length`);
  }

  static collectionIsTooSmall(min: number, current: number): AppError {
    return AppError.of(
      'collection.is.too.small',
      `The collection must contain ${min} items or more. It contains ${current} items.`,
    );
  }

  static collectionIsTooLarge(max: number, current: number): AppError {
    return AppError.of(
      'collection.is.too.large',
      `The collection must contain ${max} items or fewer. It contains ${current} items.`,
    );
  }

  static valueIsOutOfRange<T>(name: string, value: T, min: T, max: T): AppError {
    assertName(name);
    return AppError.of(
      'value.is.out.of.range',
      `Value ${value} for ${name} is out of range. Min value is ${min}, max value is ${max}.`,
    );
  }

  static valueMustBeGreaterThan<T>(name: string, value: T, min: T): AppError {
    assertName(name);
    return AppError.of('value.must.be.greater.than', `The value of ${name} (${value}) must be greater than ${min}.`);
  }

  static valueMustBeGreaterOrEqual<T>(name: string, value: T, min: T): AppError {
    assertName(name);
    return AppError.of(
      'value.must.be.greater.or.equal',
      `The value of ${name} (${value}) must be greater than or equal to ${min}.`,
    );
  }

  static valueMustBeLessThan<T>(name: string, value: T, max: T): AppError {
    assertName(name);
    return AppError.of('value.must.be.less.than', `The value of ${name} (${value}) must be less than ${max}.`);
  }

  static valueMustBeLessOrEqual<T>(name: string, value: T, max: T): AppError {
    assertName(name);
    return AppError.of(
      'value.must.be.less.or.equal',
      `The value of ${name} (${value}) must be less than or equal to ${max}.`,
    );
  }
}

function assertName(name: string): void {
  if (name == null || name.trim() === '') {
    throw new Error('Name must not be null or empty');
  }
}
