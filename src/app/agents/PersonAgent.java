package app.agents;

import app.Helper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.Runtime;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

public class PersonAgent extends BaseSensorAgent {

    private AID currentRoom;
    private AID nextRoom;

    @Override
    protected void setup() {
        super.setup();

        // inform World that person is coming IN
        addBehaviour(new RoomChangeBehaviour(RoomEnum.LOBBY.toString()));
        addBehaviour(new RandomChoiceBehaviour(this, 1000));
        addBehaviour(new HandleRoomResponseBehaviour());
    }

    class HandleRoomResponseBehaviour extends Behaviour {

        @Override
        public void action() {

            MessageTemplate mt = MessageTemplate.MatchSender(nextRoom);
            ACLMessage msg = myAgent.receive(mt);

            if(msg != null) {
                //System.out.println("dostavam objednavku");

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

    class RoomChangeBehaviour extends OneShotBehaviour {

        private final String targetRoom;

        public RoomChangeBehaviour(String targetRoom) {
            this.targetRoom = targetRoom;
        }

        @Override
        public void action() {

            //System.out.println("Tvorim");
            PersonAgent person = (PersonAgent) myAgent;
            AID lobbyRoom = Helper.findAgentByName(person, targetRoom);

            person.nextRoom = lobbyRoom;

            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(lobbyRoom);
            msg.setContent("enter");
            person.send(msg);
        }
    }

    class RandomChoiceBehaviour extends TickerBehaviour {
        public RandomChoiceBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            if (new Random().nextFloat() > 0.5) {
                int pick = new Random().nextInt(RoomEnum.values().length);
                myAgent.addBehaviour(new RoomChangeBehaviour(RoomEnum.values()[pick].toString()));
                System.out.println(myAgent.getLocalName() + " chce do " + RoomEnum.values()[pick].toString());
            }

        }
    }
}
