package app.behaviours;

import app.agents.PersonAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class PersonEntersBehaviour extends OneShotBehaviour {
    @Override
    public void action() {

        PersonAgent person = (PersonAgent) myAgent;

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(person.worldAgentAID);
        msg.setContent("IN");
        person.send(msg);
    }
}
