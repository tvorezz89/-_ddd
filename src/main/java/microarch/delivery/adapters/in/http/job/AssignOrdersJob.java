package microarch.delivery.adapters.in.http.job;

import microarch.delivery.core.application.commands.AssignOrderCommand;
import microarch.delivery.core.application.commands.AssignOrderCommandHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssignOrdersJob implements Job {
    private final AssignOrderCommandHandler useCase;

    @Autowired
    public AssignOrdersJob(AssignOrderCommandHandler useCase) {
        this.useCase = useCase;
    }

    @Override
    public void execute(JobExecutionContext context) {
        useCase.handle(AssignOrderCommand.create().getValueOrThrow());
    }
}