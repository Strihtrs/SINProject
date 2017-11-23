package app.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

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
            findWorld();
        }

        @Override
        public boolean done() {
            return worldAgentAID != null;
        }

        private void findWorld() {

            DFAgentDescription dfad = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("world");
            dfad.addServices(sd);

            DFAgentDescription[] results;
            try {
                results = DFService.search(this.myAgent, dfad);
                if (results.length > 0)
                    worldAgentAID = results[0].getName();
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }
    }
}
