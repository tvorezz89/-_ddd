import { DomainInvariantException } from './domain-invariant.exception';
import { GeneralErrors } from './general-errors';

const SEPARATOR = '||';

export class AppError {
  private constructor(
    public readonly code: string,
    public readonly message: string,
  ) {}

  static of(code: string, message: string): AppError {
    if (code == null) {
      throw new TypeError('Code must not be null');
    }

    if (message == null) {
      throw new TypeError('Message must not be null');
    }

    return new AppError(code, message);
  }

  serialize(): string {
    return `${this.code}${SEPARATOR}${this.message}`;
  }

  static deserialize(serialized: string): AppError {
    if (serialized === 'A non-empty request body is required.') {
      return GeneralErrors.valueIsRequired('serialized');
    }

    const parts = serialized.split(SEPARATOR);
    if (parts.length < 2 || parts[0] == null || parts[1] == null) {
      throw new Error(`Invalid error serialization: '${serialized}'`);
    }

    return AppError.of(parts[0], parts[1]);
  }

  static throwIf(error: AppError | null | undefined): void {
    if (error != null) {
      throw new DomainInvariantException(error);
    }
  }

  equals(other: AppError): boolean {
    return this.code === other.code && this.message === other.message;
  }

  toString(): string {
    return `Error{code='${this.code}', message='${this.message}'}`;
  }
}
