import type { AppError } from './error';

export class DomainInvariantException extends Error {
  constructor(error: AppError) {
    super(`Domain invariant violated: ${error.message}`);
    this.name = 'DomainInvariantException';
  }
}
