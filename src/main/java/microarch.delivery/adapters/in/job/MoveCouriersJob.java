package microarch.delivery.adapters.in.job;

import microarch.delivery.core.application.command.MoveCouriersCommand;
import microarch.delivery.core.application.command.MoveCouriersCommandHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoveCouriersJob implements Job {
    private final MoveCouriersCommandHandler handler;

    @Autowired
    public MoveCouriersJob(MoveCouriersCommandHandler handler) {
        this.handler = handler;
    }

    @Override
    public void execute(JobExecutionContext context) {
        var command = MoveCouriersCommand.create();

        if (command.isFailure()) {
            throw new RuntimeException();
        }

        handler.handle(command.getValue());
    }
}
