package microarch.delivery.adapters.in.scheduler;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.command.MoveCouriersCommandHandler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoveCourierJob implements Job {

    @Autowired
    private MoveCouriersCommandHandler useCase;

    @Override
    public void execute(JobExecutionContext context) {
        useCase.handle();
    }
}

