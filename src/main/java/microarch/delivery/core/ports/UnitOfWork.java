package microarch.delivery.core.ports;

import java.util.function.Supplier;

public interface UnitOfWork {
    void execute(Runnable action);

    <T> T execute(Supplier<T> action);
}
