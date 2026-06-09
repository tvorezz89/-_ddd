// src/common/domain/value-objects/location.value-object.spec.ts
import { Location } from './location.value-object';

describe('Location Value Object', () => {
  describe('Creation & Validation', () => {
    it('должен успешно создать объект с валидными координатами', () => {
      const location = new Location(5, 5);

      expect(location.x).toBe(5);
      expect(location.y).toBe(5);
    });

    it('должен успешно создать объект на минимальной границе (1,1)', () => {
      const location = new Location(1, 1);

      expect(location.x).toBe(1);
      expect(location.y).toBe(1);
    });

    it('должен успешно создать объект на максимальной границе (10,10)', () => {
      const location = new Location(10, 10);

      expect(location.x).toBe(10);
      expect(location.y).toBe(10);
    });

    it('должен выбросить ошибку, если координата X меньше 1', () => {
      expect(() => new Location(0, 5)).toThrow(
        'Координата X должна быть в диапазоне от 1 до 10',
      );
    });

    it('должен выбросить ошибку, если координата X больше 10', () => {
      expect(() => new Location(11, 5)).toThrow(
        'Координата X должна быть в диапазоне от 1 до 10',
      );
    });

    it('должен выбросить ошибку, если координата Y меньше 1', () => {
      expect(() => new Location(5, -1)).toThrow(
        'Координата Y должна быть в диапазоне от 1 до 10',
      );
    });

    it('должен выбросить ошибку, если координата Y больше 10', () => {
      expect(() => new Location(5, 12)).toThrow(
        'Координата Y должна быть в диапазоне от 1 до 10',
      );
    });
  });

  describe('Immutability (Неизменяемость)', () => {
    it('не должен позволять изменять свойства X и Y после создания', () => {
      const location = new Location(3, 4);

      // В TypeScript это вызовет ошибку компиляции,
      // а Object.freeze() в рантайме (в strict mode) выбросит TypeError
      expect(() => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
        (location as any).x = 5;
      }).toThrow();

      expect(location.x).toBe(3); // Значение не изменилось
    });
  });

  describe('Equality (Эквивалентность)', () => {
    it('должен вернуть true, если X и Y у двух объектов совпадают', () => {
      const loc1 = new Location(2, 3);
      const loc2 = new Location(2, 3);

      expect(loc1.equals(loc2)).toBe(true);
    });

    it('должен вернуть false, если координаты X отличаются', () => {
      const loc1 = new Location(2, 3);
      const loc2 = new Location(5, 3);

      expect(loc1.equals(loc2)).toBe(false);
    });

    it('должен вернуть false, если координаты Y отличаются', () => {
      const loc1 = new Location(2, 3);
      const loc2 = new Location(2, 8);

      expect(loc1.equals(loc2)).toBe(false);
    });

    it('должен вернуть false, если передан null или undefined', () => {
      const loc = new Location(1, 1);

      expect(loc.equals(null as any)).toBe(false);
      expect(loc.equals(undefined as any)).toBe(false);
    });
  });

  describe('Distance Calculation (Расчет расстояния)', () => {
    it('должен вернуть 0, если рассчитывается расстояние до самого себя', () => {
      const loc = new Location(4, 4);

      expect(loc.distanceTo(loc)).toBe(0);
    });

    it('должен корректно рассчитать расстояние по сетке (пример из ТЗ: 2 шага по X, 3 по Y = 5)', () => {
      const courierLocation = new Location(1, 1);
      const orderLocation = new Location(3, 4); // X: 3-1=2, Y: 4-1=3

      expect(courierLocation.distanceTo(orderLocation).toFixed(0)).toBe('5');
    });

    it('должен корректно считать расстояние в обратном направлении (от заказа к курьеру)', () => {
      const courierLocation = new Location(1, 1);
      const orderLocation = new Location(3, 4);

      expect(orderLocation.distanceTo(courierLocation).toFixed(0)).toBe('5');
    });

    it('должен корректно работать при движении только по одной оси (горизонталь)', () => {
      const loc1 = new Location(1, 5);
      const loc2 = new Location(6, 5); // Только X изменился на 5 шагов

      expect(loc1.distanceTo(loc2).toFixed(0)).toBe('5');
    });

    it('должен корректно работать при движении только по одной оси (вертикаль)', () => {
      const loc1 = new Location(3, 2);
      const loc2 = new Location(3, 10); // Только Y изменился на 8 шагов

      expect(loc1.distanceTo(loc2).toFixed(0)).toBe('8');
    });
  });
});
