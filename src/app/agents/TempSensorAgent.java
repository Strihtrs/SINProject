package app.agents;

import app.SensorEnum;
import app.behaviours.SensorBehaviour;

public class TempSensorAgent extends BaseSensorAgent {

    public TempSensorAgent() {
        super(SensorEnum.TEMPERATURE.toString());
    }

    @Override
    protected void setup() {

        super.setup();
        addBehaviour(new SensorBehaviour<>(this, 700, conversationId));
    }
}
