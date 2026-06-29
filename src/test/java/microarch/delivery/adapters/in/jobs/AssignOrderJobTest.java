package microarch.delivery.adapters.in.jobs;

import libs.errs.Error;
import libs.errs.UnitResult;
import microarch.delivery.core.application.AssignOrderCommand;
import microarch.delivery.core.application.AssignOrderCommandHandler;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssignOrderJobTest {

    @Test
    void runShouldDelegateToAssignOrderUseCase() {
        var handler = new RecordingAssignOrderHandler();
        var job = new AssignOrderJob(handler);

        job.run();

        assertThat(handler.calls).isEqualTo(1);
        assertThat(handler.command).isNotNull();
    }

    private static final class RecordingAssignOrderHandler implements AssignOrderCommandHandler {
        private int calls;
        private AssignOrderCommand command;

        @Override
        public UnitResult<Error> handle(AssignOrderCommand command) {
            calls++;
            this.command = command;
            return UnitResult.success();
        }
    }
}
