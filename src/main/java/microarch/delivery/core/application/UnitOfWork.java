package microarch.delivery.core.application;

import java.util.function.Supplier;

/**
 * Порт Unit of Work: выполняет блок работы атомарно — либо все операции внутри блока сохраняются, либо ни одна (откат
 * транзакции).
 */
public interface UnitOfWork {
    <T> T execute(Supplier<T> action);

    void execute(Runnable action);
}
