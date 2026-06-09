export class Location {
  public readonly x: number;
  public readonly y: number;

  // Ограничения бизнес-логики (Константы)
  private static readonly MIN_COORD = 1;
  private static readonly MAX_COORD = 10;

  constructor(x: number, y: number) {
    this.validate(x, y);
    this.x = x;
    this.y = y;

    Object.freeze(this);
  }

  // Бизнес-правило: Валидация при создании
  private validate(x: number, y: number): void {
    if (x < Location.MIN_COORD || x > Location.MAX_COORD) {
      throw new Error(
        `Координата X должна быть в диапазоне от ${Location.MIN_COORD} до ${Location.MAX_COORD}`,
      );
    }

    if (y < Location.MIN_COORD || y > Location.MAX_COORD) {
      throw new Error(
        `Координата Y должна быть в диапазоне от ${Location.MIN_COORD} до ${Location.MAX_COORD}`,
      );
    }
  }

  // Требование: Проверка на эквивалентность
  public equals(other: Location): boolean {
    if (!other) return false;
    return this.x === other.x && this.y === other.y;
  }

  // Требование: Расчет расстояния
  // Курьер ходит только по матрице (горизонталь/вертикаль)
  public distanceTo(other: Location): number {
    const stepsX = Math.abs(this.x - other.x);
    const stepsY = Math.abs(this.y - other.y);

    return stepsX + stepsY;
  }
}
