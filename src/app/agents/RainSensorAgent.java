package app.agents;

import app.behaviours.SensorBehaviour;

public class RainSensorAgent extends BaseSensorAgent {

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new SensorBehaviour<>(this, 1000, "rain", 3, conversationId));
    }
}
