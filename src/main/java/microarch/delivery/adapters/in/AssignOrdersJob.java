package microarch.delivery.adapters.in;

import microarch.delivery.core.application.AssignOrderUseCase;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssignOrdersJob implements Job {
    private final AssignOrderUseCase useCase;

    @Autowired
    public AssignOrdersJob(AssignOrderUseCase useCase1) {
        useCase = useCase1;
    }

    @Override
    public void execute(JobExecutionContext context) {
        useCase.handle();
    }
}
