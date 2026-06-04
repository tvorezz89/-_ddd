import { DomainInvariantException } from './domain-invariant.exception';
import type { AppError } from './error';
import { Result } from './result';

export class UnitResult<E extends AppError = AppError> {
  private constructor(
    public readonly isSuccess: boolean,
    private readonly error?: E,
  ) {}

  static success<E extends AppError = AppError>(): UnitResult<E> {
    return new UnitResult<E>(true);
  }

  static failure<E extends AppError = AppError>(error: E): UnitResult<E> {
    if (error == null) {
      throw new TypeError('Error must not be null on failure');
    }

    return new UnitResult<E>(false, error);
  }

  isFailure(): boolean {
    return !this.isSuccess;
  }

  getError(): E {
    if (this.isSuccess) {
      throw new Error('Cannot get error from success');
    }

    return this.error as E;
  }

  onSuccess(handler: () => void): UnitResult<E> {
    if (this.isSuccess) {
      handler();
    }

    return this;
  }

  onFailure(handler: (error: E) => void): UnitResult<E> {
    if (this.isFailure()) {
      handler(this.error as E);
    }

    return this;
  }

  fold<U>(onSuccess: () => U, onFailure: (error: E) => U): U {
    return this.isSuccess ? onSuccess() : onFailure(this.error as E);
  }

  merge(other: UnitResult<E>): UnitResult<E> {
    if (this.isFailure()) {
      return this;
    }

    if (other.isFailure()) {
      return other;
    }

    return UnitResult.success<E>();
  }

  static from<E extends AppError>(result: Result<void, E>): UnitResult<E> {
    return result.isSuccess ? UnitResult.success<E>() : UnitResult.failure<E>(result.getError());
  }

  toResult(): Result<void, E> {
    return this.isFailure() ? Result.failure<void, E>(this.error as E) : Result.successVoid<E>();
  }

  getOrElseThrow(exceptionMapper?: (error: E) => Error): void {
    if (this.isSuccess) {
      return;
    }

    if (exceptionMapper != null) {
      throw exceptionMapper(this.error as E);
    }

    throw new DomainInvariantException(this.error as E);
  }

  toString(): string {
    return this.isSuccess ? 'Success' : `Failure(${this.error})`;
  }
}
