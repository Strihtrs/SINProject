package app.agents;

import app.SensorEnum;

public class MotionSensor extends BaseSensorAgent {

    private boolean isOn = false;

    public MotionSensor() {
        super(SensorEnum.MOTION.toString());
    }

    @Override
    protected void setup() {
        super.setup();

        addBehaviour(new SensorBehaviour<>(this, 500, conversationId, SensorEnum.MOTION));
    }

    @Override
    protected boolean shouldSend(String content) {
        if (content.equals("On") && !isOn) {
            isOn = true;
            return true;
        }
        if (content.equals("Off") && isOn) {
            isOn = false;
            return true;
        }
        return false;
    }
}
