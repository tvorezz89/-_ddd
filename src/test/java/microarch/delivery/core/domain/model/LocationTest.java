package microarch.delivery.core.domain.model;

import libs.errs.Result;
import libs.errs.Error;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @Test
    void create_Positive() {
        Result<Location, Error> location = Location.create(5,5);

        assertTrue(location.isSuccess());
    }

   @Test
    void create_X_isLess() {
        Result<Location, Error> location = Location.create(0,5);

        assertTrue(location.isFailure());
        assertEquals("value.must.be.greater.than", location.getError().getCode());
    }

   @Test
    void create_X_isGreater() {
        Result<Location, Error> location = Location.create(11,5);

       assertTrue(location.isFailure());
       assertEquals("value.must.be.less.than", location.getError().getCode());
    }

   @Test
    void create_Y_isLess() {
        Result<Location, Error> location = Location.create(5,0);

       assertTrue(location.isFailure());
       assertEquals("value.must.be.greater.than", location.getError().getCode());
    }

   @Test
    void create_Y_isGreater() {
        Result<Location, Error> location = Location.create(5,11);

       assertTrue(location.isFailure());
       assertEquals("value.must.be.less.than", location.getError().getCode());
    }

    @Test
    void compare_Positive() {
        Result<Location, Error> location1 = Location.create(5,5);
        Result<Location, Error> location2 = Location.create(5,5);

        assertTrue(location1.getValue().equals(location2.getValue()));
    }

    @Test
    void compare_Negative() {
        Result<Location, Error> location1 = Location.create(5,5);
        Result<Location, Error> location2 = Location.create(5,6);

        assertFalse(location1.getValue().equals(location2.getValue()));
    }

    @Test
    void computeDistance() {
        Result<Location, Error> location1 = Location.create(2,6);
        Result<Location, Error> location2 = Location.create(4,9);
        if (location1.isSuccess() && location2.isSuccess()) {
            int distance = location1.getValue().computeDistance(location2.getValue());
            assertEquals(5, distance);
        }
    }
}