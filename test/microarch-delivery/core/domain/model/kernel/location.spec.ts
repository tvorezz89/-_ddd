import { describe, expect, it } from "vitest";
import { Location } from "../../../../../../src/microarch-delivery/core/domain/model/kernel/location";

describe("Location", () => {
  describe("create", () => {
    it("creates location with minimum valid coordinates", () => {
      const result = Location.create(1, 1);

      expect(result.isSuccess).toBe(true);

      const location = result.getValue();
      expect(location).toBeInstanceOf(Location);
      expect(location.x).toBe(1);
      expect(location.y).toBe(1);
    });

    it("creates location with maximum valid coordinates", () => {
      const result = Location.create(10, 10);

      expect(result.isSuccess).toBe(true);

      const location = result.getValue();
      expect(location).toBeInstanceOf(Location);
      expect(location.x).toBe(10);
      expect(location.y).toBe(10);
    });

    it("creates location with coordinates inside valid range", () => {
      const result = Location.create(5, 5);

      expect(result.isSuccess).toBe(true);

      const location = result.getValue();
      expect(location).toBeInstanceOf(Location);
      expect(location.x).toBe(5);
      expect(location.y).toBe(5);
    });

    it("stores x coordinate", () => {
      const location = createLocation(3, 7);

      expect(location.x).toBe(3);
    });

    it("stores y coordinate", () => {
      const location = createLocation(3, 7);

      expect(location.y).toBe(7);
    });

    it("fails when x coordinate is less than minimum", () => {
      const result = Location.create(0, 1);

      expect(result.isFailure()).toBe(true);
      expect(result.getError().code).toBe("value.is.out.of.range");
      expect(result.getError().message).toBe(
        "Value 0 for Координата x is out of range. Min value is 1, max value is 10.",
      );
    });

    it("fails when x coordinate is greater than maximum", () => {
      const result = Location.create(11, 1);

      expect(result.isFailure()).toBe(true);
      expect(result.getError().code).toBe("value.is.out.of.range");
      expect(result.getError().message).toBe(
        "Value 11 for Координата x is out of range. Min value is 1, max value is 10.",
      );
    });

    it("fails when y coordinate is less than minimum", () => {
      const result = Location.create(1, 0);

      expect(result.isFailure()).toBe(true);
      expect(result.getError().code).toBe("value.is.out.of.range");
      expect(result.getError().message).toBe(
        "Value 0 for Координата y is out of range. Min value is 1, max value is 10.",
      );
    });

    it("fails when y coordinate is greater than maximum", () => {
      const result = Location.create(1, 11);

      expect(result.isFailure()).toBe(true);
      expect(result.getError().code).toBe("value.is.out.of.range");
      expect(result.getError().message).toBe(
        "Value 11 for Координата y is out of range. Min value is 1, max value is 10.",
      );
    });

    it("returns x coordinate error when both coordinates are invalid and x is checked first", () => {
      const result = Location.create(0, 11);

      expect(result.isFailure()).toBe(true);
      expect(result.getError().message).toBe(
        "Value 0 for Координата x is out of range. Min value is 1, max value is 10.",
      );
    });
  });

  describe("equals", () => {
    it("returns true for locations with the same coordinates", () => {
      const location = createLocation(1, 2);
      const otherLocation = createLocation(1, 2);

      expect(location.equals(otherLocation)).toBe(true);
    });

    it("returns false for locations with different x coordinate", () => {
      const location = createLocation(1, 2);
      const otherLocation = createLocation(2, 2);

      expect(location.equals(otherLocation)).toBe(false);
    });

    it("returns false for locations with different y coordinate", () => {
      const location = createLocation(1, 2);
      const otherLocation = createLocation(1, 3);

      expect(location.equals(otherLocation)).toBe(false);
    });

    it("returns false when compared with null", () => {
      const location = createLocation(1, 2);

      expect(location.equals(null)).toBe(false);
    });

    it("returns false when compared with non-location value", () => {
      const location = createLocation(1, 2);

      expect(location.equals({ x: 1, y: 2 })).toBe(false);
    });
  });

  describe("compareTo", () => {
    it("returns 0 for locations with the same coordinates", () => {
      const location = createLocation(1, 2);
      const otherLocation = createLocation(1, 2);

      expect(location.compareTo(otherLocation)).toBe(0);
    });

    it("orders locations by x coordinate first", () => {
      const location = createLocation(1, 10);
      const otherLocation = createLocation(2, 1);

      expect(location.compareTo(otherLocation)).toBeLessThan(0);
      expect(otherLocation.compareTo(location)).toBeGreaterThan(0);
    });

    it("orders locations by y coordinate when x coordinates are equal", () => {
      const location = createLocation(1, 2);
      const otherLocation = createLocation(1, 3);

      expect(location.compareTo(otherLocation)).toBeLessThan(0);
      expect(otherLocation.compareTo(location)).toBeGreaterThan(0);
    });
  });

  describe("getSteps", () => {
    it("returns 0 for the same location", () => {
      const location = createLocation(5, 5);
      const otherLocation = createLocation(5, 5);

      expect(location.getSteps(otherLocation)).toBe(0);
    });

    it("returns horizontal distance when y coordinates are equal", () => {
      const location = createLocation(2, 5);
      const otherLocation = createLocation(7, 5);

      expect(location.getSteps(otherLocation)).toBe(5);
    });

    it("returns vertical distance when x coordinates are equal", () => {
      const location = createLocation(5, 2);
      const otherLocation = createLocation(5, 7);

      expect(location.getSteps(otherLocation)).toBe(5);
    });

    it("returns manhattan distance between two locations", () => {
      const location = createLocation(2, 3);
      const otherLocation = createLocation(7, 9);

      expect(location.getSteps(otherLocation)).toBe(11);
    });
  });

  describe("toString", () => {
    it("includes x and y coordinates", () => {
      const location = createLocation(3, 7);

      expect(location.toString()).toBe("Location[3, 7]");
    });
  });
});

function createLocation(x: number, y: number): Location {
  return Location.create(x, y).getValue();
}
