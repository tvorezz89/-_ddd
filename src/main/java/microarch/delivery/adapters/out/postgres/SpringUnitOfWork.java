package microarch.delivery.adapters.out.postgres;

import java.util.function.Supplier;

import microarch.delivery.core.ports.UnitOfWork;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class SpringUnitOfWork implements UnitOfWork {
    private final TransactionTemplate _transactions;

    public SpringUnitOfWork(TransactionTemplate transactions) {
        _transactions = transactions;
    }

    @Override
    public void execute(Runnable action) {
        _transactions.executeWithoutResult(status -> action.run());
    }

    @Override
    public <T> T execute(Supplier<T> action) {
        return _transactions.execute(status -> action.get());
    }
}
