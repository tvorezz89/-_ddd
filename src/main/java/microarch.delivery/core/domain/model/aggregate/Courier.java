package microarch.delivery.core.domain.model.aggregate;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import microarch.delivery.core.domain.model.Location;

import java.util.UUID;

public class Courier extends Aggregate<UUID> {

    private String name;
    private Integer speed;
    private Location location;

    private Courier() {};

    private Courier(String name, int speed, Location location) {
        this.name = name;
        this.speed = speed;
        this.location = location;
    }

    private Result<Courier, Error> create(String name, Integer speed, Location location) {
        if (name == null || name.isEmpty()) return Result.failure(GeneralErrors.valueIsRequired("name"));
        if (speed == null || speed == 0) return Result.failure(GeneralErrors.valueIsRequired("speed"));
        if (location == null) return Result.failure(GeneralErrors.valueIsRequired("location"));

        return Result.success(new Courier(name, speed, location));
    }


    @Override
    public UUID getId() {
        return id;
    }
}
