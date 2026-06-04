import { ValueObject } from "../../../../../libs/ddd";
import { AppError, GeneralErrors, Result } from "../../../../../libs/errs";

export class Location extends ValueObject<Location> {
  private static readonly MIN_VALUE = 1;
  private static readonly MAX_VALUE = 10;

  private constructor(
    public readonly x: number,
    public readonly y: number,
  ) {
    super();
  }

  static create(x: number, y: number): Result<Location, AppError> {
    if (!Location.isCoordinateInRange(x)) {
      return Result.failure(
        GeneralErrors.valueIsOutOfRange(
          "Координата x",
          x,
          Location.MIN_VALUE,
          Location.MAX_VALUE,
        ),
      );
    }

    if (!Location.isCoordinateInRange(y)) {
      return Result.failure(
        GeneralErrors.valueIsOutOfRange(
          "Координата y",
          y,
          Location.MIN_VALUE,
          Location.MAX_VALUE,
        ),
      );
    }

    return Result.success(new Location(x, y));
  }

  public getSteps(newLocation: Location): number {
    const stepsX = Math.abs(newLocation.x - this.x);
    const stepsY = Math.abs(newLocation.y - this.y);

    return stepsX + stepsY;
  }

  protected override equalityComponents(): Iterable<unknown> {
    return [this.x, this.y];
  }

  private static isCoordinateInRange(value: number): boolean {
    return value >= Location.MIN_VALUE && value <= Location.MAX_VALUE;
  }
}
