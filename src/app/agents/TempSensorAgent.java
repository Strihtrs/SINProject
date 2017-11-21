package app.agents;

import app.behaviours.SensorBehaviour;

public class TempSensorAgent extends BaseSensorAgent {

    @Override
    protected void setup() {

        super.setup();
        addBehaviour(new SensorBehaviour<>(this, 700, "temp", 2, conversationId));
    }
}
