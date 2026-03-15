package microarch.delivery.adapters.in.scheduler;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.command.DispatchOrdersCommandHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignOrdersJob implements Job {


    private final DispatchOrdersCommandHandler useCase;

    @Override
    public void execute(JobExecutionContext context) {
        useCase.handle();
    }
}