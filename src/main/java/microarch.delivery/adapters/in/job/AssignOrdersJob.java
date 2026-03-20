package microarch.delivery.adapters.in.job;

import microarch.delivery.core.application.command.AssignOrderToCourierCommand;
import microarch.delivery.core.application.command.AssignOrderToCourierCommandHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AssignOrdersJob implements Job {
    private final AssignOrderToCourierCommandHandler handler;

    @Autowired
    public AssignOrdersJob(AssignOrderToCourierCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void execute(JobExecutionContext context) {
        var command = AssignOrderToCourierCommand.create();
        if (command.isFailure()) {
            throw new RuntimeException();
        }

        handler.handle(command.getValue());
    }
}
