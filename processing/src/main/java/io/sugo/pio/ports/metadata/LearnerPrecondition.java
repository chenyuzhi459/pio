package io.sugo.pio.ports.metadata;

/**
 */
import io.sugo.pio.operator.learner.CapabilityProvider;
import io.sugo.pio.ports.InputPort;

/**
 * @author Simon Fischer
 */
public class LearnerPrecondition extends CapabilityPrecondition {

    public LearnerPrecondition(CapabilityProvider capabilityProvider, InputPort inputPort) {
        super(capabilityProvider, inputPort);
    }

    @Override
    public void makeAdditionalChecks(ExampleSetMetaData metaData) {
        // checking Capabilities
        super.makeAdditionalChecks(metaData);
    }
}