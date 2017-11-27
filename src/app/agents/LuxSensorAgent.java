package app.agents;

import app.SensorEnum;
import app.behaviours.SensorBehaviour;

public class LuxSensorAgent extends BaseSensorAgent {

    public LuxSensorAgent() {
        super(SensorEnum.LUX.toString());
    }

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new SensorBehaviour<>(this, 500, conversationId));
    }
}
