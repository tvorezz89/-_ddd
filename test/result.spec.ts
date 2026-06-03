import { describe, expect, it } from 'vitest';
import { AppError, GeneralErrors, Result, UnitResult } from '../src/libs/errs';

describe('Result', () => {
  it('maps successful values', () => {
    const result = Result.success<number>(2).map((value) => value + 3);

    expect(result.isSuccess).toBe(true);
    expect(result.getValue()).toBe(5);
  });

  it('keeps failure errors during map', () => {
    const error = GeneralErrors.valueIsRequired('id');
    const result = Result.failure<number>(error).map((value) => value + 3);

    expect(result.isFailure()).toBe(true);
    expect(result.getError()).toBe(error);
  });
});

describe('UnitResult', () => {
  it('converts to Result', () => {
    const error = AppError.of('code', 'message');
    const result = UnitResult.failure(error).toResult();

    expect(result.isFailure()).toBe(true);
    expect(result.getError()).toBe(error);
  });
});
