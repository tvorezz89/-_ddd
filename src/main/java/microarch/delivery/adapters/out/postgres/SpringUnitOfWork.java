package microarch.delivery.adapters.out.postgres;

import microarch.delivery.core.application.UnitOfWork;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * Реализация Unit of Work на транзакциях Spring (PlatformTransactionManager). Все операции внутри блока выполняются в
 * одной транзакции БД.
 */
@Component
public class SpringUnitOfWork implements UnitOfWork {
    private final TransactionTemplate transactionTemplate;

    public SpringUnitOfWork(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public <T> T execute(Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }

    @Override
    public void execute(Runnable action) {
        transactionTemplate.executeWithoutResult(status -> action.run());
    }
}
