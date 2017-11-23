package app.agents;

import app.Helper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import java.util.UUID;

public class BaseSensorAgent extends Agent {

    public AID worldAgentAID;
    protected UUID conversationId;

    BaseSensorAgent() {
        this.conversationId = UUID.randomUUID();
    }

    @Override
    protected void setup() {

        addBehaviour(new FindWorldBehaviour());
        super.setup();
    }

    private class FindWorldBehaviour extends Behaviour {

        @Override
        public void action() {
            worldAgentAID = Helper.findAgentByName(myAgent, "mr-world-wide");
        }

        @Override
        public boolean done() {
            return worldAgentAID != null;
        }
    }
}
