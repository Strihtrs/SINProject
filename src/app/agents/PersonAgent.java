package app.agents;

import app.Helper;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PersonAgent extends BaseSensorAgent {

    private AID currentRoom;
    private AID nextRoom;

    @Override
    protected void setup() {
        super.setup();

        // inform World that person is coming IN
        addBehaviour(new PersonEntersBehaviour());
        addBehaviour(new HandleRoomResponseBehaviour());
    }

    class HandleRoomResponseBehaviour extends Behaviour {

        @Override
        public void action() {

            MessageTemplate mt = MessageTemplate.MatchSender(nextRoom);
            ACLMessage msg = myAgent.receive(mt);

            if(msg != null) {
                System.out.println("dostavam objednavku");

                int type = msg.getPerformative();
                if(type == ACLMessage.ACCEPT_PROPOSAL) {

                    currentRoom = nextRoom;

                } else if(type == ACLMessage.REJECT_PROPOSAL) {
                    nextRoom = null;
                }

            } else {
                block();
            }
        }

        @Override
        public boolean done() {
            return false;
        }
    }

    class PersonEntersBehaviour extends OneShotBehaviour {
        @Override
        public void action() {

            System.out.println("Tvorim");
            PersonAgent person = (PersonAgent) myAgent;
            AID lobbyRoom = Helper.findAgentByName(person, "lobby-room");

            person.nextRoom = lobbyRoom;

            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(lobbyRoom);
            msg.setContent("enter");
            person.send(msg);
        }
    }
}
