package app.agents;

import app.Helper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

import java.util.UUID;

public class BaseSensorAgent extends Agent {

    public AID worldAgentAID;
    public AID roomAID;
    protected UUID conversationId;

    public String msgContent;
    private int IDX;

    BaseSensorAgent(String msgContent) {
        this.msgContent = msgContent;
        this.conversationId = UUID.randomUUID();
    }

    @Override
    protected void setup() {

        addBehaviour(new FindWorldBehaviour());
        super.setup();
    }

    public void setRoomAID(AID roomAID) {
        this.roomAID = roomAID;
    }

    public int getIDX() {
        return IDX;
    }

    public void setIDX(int IDX) {
        this.IDX = IDX;
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
