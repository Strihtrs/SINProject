package app.agents;

import app.SensorEnum;

public class RainSensorAgent extends BaseSensorAgent {

    public RainSensorAgent() {
        super(SensorEnum.RAIN.toString());
    }

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new SensorBehaviour<>(this, 1000, conversationId, SensorEnum.RAIN));
    }
}
