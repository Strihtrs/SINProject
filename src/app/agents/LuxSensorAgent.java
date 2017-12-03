package app.agents;

import app.SensorEnum;

public class LuxSensorAgent extends BaseSensorAgent {

    public LuxSensorAgent() {
        super(SensorEnum.LUX.toString());
    }

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new SensorBehaviour<>(this, WorldAgent.TIME_STEP, conversationId, SensorEnum.LUX));
    }
}
