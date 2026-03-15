package microarch.delivery.core.application.command;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import microarch.delivery.core.domain.kernel.Speed;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCourierCommand {

    private String name;

    private Speed speed;

    public static Result<CreateCourierCommand, Error> create(String name, Integer speed) {
        var validateName = Guard.againstNullOrEmpty(name, "name");
        if (validateName != null) {
            return Result.failure(validateName);
        }

        var speedResult = Speed.create(speed);
        if (speedResult.isFailure()) {
            return Result.failure(speedResult.getError());
        }

        return Result.success(new CreateCourierCommand(name, speedResult.getValue()));
    }
}
