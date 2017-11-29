package app.agents;

import app.SensorEnum;

public class TempSensorAgent extends BaseSensorAgent {

    public TempSensorAgent() {
        super(SensorEnum.TEMPERATURE.toString());
    }

    @Override
    protected void setup() {

        super.setup();
        addBehaviour(new SensorBehaviour<>(this, 1000, conversationId, SensorEnum.TEMPERATURE));
    }
}
