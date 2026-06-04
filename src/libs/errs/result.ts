import { DomainInvariantException } from './domain-invariant.exception';
import type { AppError } from './error';

export class Result<T, E extends AppError = AppError> {
  private constructor(
    private readonly value: T | undefined,
    private readonly error: E | undefined,
    public readonly isSuccess: boolean,
  ) {}

  static success<T, E extends AppError = AppError>(value: T): Result<T, E> {
    if (value == null) {
      throw new TypeError('Value must not be null');
    }

    return new Result<T, E>(value, undefined, true);
  }

  static successVoid<E extends AppError = AppError>(): Result<void, E> {
    return new Result<void, E>(undefined, undefined, true);
  }

  static failure<T, E extends AppError = AppError>(error: E): Result<T, E> {
    if (error == null) {
      throw new TypeError('Error must not be null');
    }

    return new Result<T, E>(undefined, error, false);
  }

  isFailure(): boolean {
    return !this.isSuccess;
  }

  getValue(): T {
    if (!this.isSuccess) {
      throw new Error('Cannot get value from failure');
    }

    return this.value as T;
  }

  getError(): E {
    if (this.isSuccess) {
      throw new Error('Cannot get error from success');
    }

    return this.error as E;
  }

  map<U>(mapper: (value: T) => U): Result<U, E> {
    return this.isSuccess ? Result.success<U, E>(mapper(this.value as T)) : Result.failure<U, E>(this.error as E);
  }

  flatMap<U>(mapper: (value: T) => Result<U, E>): Result<U, E> {
    return this.isSuccess ? mapper(this.value as T) : Result.failure<U, E>(this.error as E);
  }

  onSuccess(handler: (value: T) => void): Result<T, E> {
    if (this.isSuccess) {
      handler(this.value as T);
    }

    return this;
  }

  onFailure(handler: (error: E) => void): Result<T, E> {
    if (this.isFailure()) {
      handler(this.error as E);
    }

    return this;
  }

  fold<U>(onSuccess: (value: T) => U, onFailure: (error: E) => U): U {
    return this.isSuccess ? onSuccess(this.value as T) : onFailure(this.error as E);
  }

  mapError<F extends AppError>(mapper: (error: E) => F): Result<T, F> {
    return this.isSuccess ? Result.success<T, F>(this.value as T) : Result.failure<T, F>(mapper(this.error as E));
  }

  getValueOrThrow(): T {
    if (this.isSuccess) {
      return this.value as T;
    }

    throw new DomainInvariantException(this.error as E);
  }

  toString(): string {
    return this.isSuccess ? `Success(${this.value})` : `Failure(${this.error})`;
  }
}
