package microarch.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Guard;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCourierCommand {
    private final String name;

    public static Result<CreateCourierCommand, Error> create(String name) {
        var validation = Guard.againstNullOrEmpty(name, "name");
        if (Objects.nonNull(validation))
            return Result.failure(validation);

        return Result.success(new CreateCourierCommand(name));
    }
}
