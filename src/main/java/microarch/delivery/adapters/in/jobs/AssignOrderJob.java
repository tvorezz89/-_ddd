package microarch.delivery.adapters.in.jobs;

import lombok.RequiredArgsConstructor;
import microarch.delivery.core.application.AssignOrderCommand;
import microarch.delivery.core.application.AssignOrderCommandHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssignOrderJob {
    private final AssignOrderCommandHandler assignOrderCommandHandler;

    @Scheduled(fixedDelay = 1000)
    public void run() {
        assignOrderCommandHandler.handle(AssignOrderCommand.create().getValue());
    }
}
