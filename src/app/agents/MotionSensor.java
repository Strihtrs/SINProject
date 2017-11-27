package app.agents;

import app.SensorEnum;
import app.behaviours.SensorBehaviour;

public class MotionSensor extends BaseSensorAgent {

    public MotionSensor() {
        super(SensorEnum.MOTION.toString());
    }

    @Override
    protected void setup() {
        super.setup();

        addBehaviour(new SensorBehaviour<>(this, 2000, conversationId));
    }
}
