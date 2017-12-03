package app.agents;

import app.SensorEnum;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import jade.core.behaviours.OneShotBehaviour;

public class MotionSensor extends BaseSensorAgent {

    private boolean isOn = false;

    public MotionSensor() {
        super(SensorEnum.MOTION.toString());
    }

    @Override
    protected void setup() {
        super.setup();

        addBehaviour(new StartupBehaviour());
        addBehaviour(new SensorBehaviour<>(this, WorldAgent.TIME_STEP, conversationId, SensorEnum.MOTION));
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

    class StartupBehaviour extends OneShotBehaviour {

        @Override
        public void action() {
            BaseSensorAgent agent = (BaseSensorAgent) myAgent;
            try {
                Unirest.get(getUrl(SensorEnum.MOTION, "Off", agent.getIDX())).asJson();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }
    }
}
