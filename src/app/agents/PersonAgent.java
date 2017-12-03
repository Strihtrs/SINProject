package app.agents;

import app.Helper;
import app.RoomEnum;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

public class PersonAgent extends Agent {

    private AID currentRoom;
    private AID nextRoom;

    public PersonAgent() {
        super();
    }

    @Override
    protected void setup() {
        super.setup();

        // inform World that person is coming IN
        addBehaviour(new RoomChangeBehaviour(RoomEnum.LOBBY.toString()));
        addBehaviour(new RandomChoiceBehaviour(this, WorldAgent.TIME_STEP*5));
        addBehaviour(new HandleRoomResponseBehaviour());
    }

    class HandleRoomResponseBehaviour extends Behaviour {

        @Override
        public void action() {

            MessageTemplate mt = MessageTemplate.MatchSender(nextRoom);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {

                int type = msg.getPerformative();
                if (type == ACLMessage.ACCEPT_PROPOSAL) {

                    informCurrentRoom();
                    currentRoom = nextRoom;

                } else if (type == ACLMessage.REJECT_PROPOSAL) {
                    nextRoom = null;
                }

            } else {
                block();
            }
        }

        private void informCurrentRoom() {

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(currentRoom);
            msg.setContent("leaving");
            myAgent.send(msg);
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

            PersonAgent person = (PersonAgent) myAgent;
            AID targetRoom = Helper.findAgentByName(person, this.targetRoom);

            person.nextRoom = targetRoom;

            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(targetRoom);

            if (currentRoom == null) {
                if (targetRoom.getLocalName().equals(RoomEnum.LOBBY.toString())) {
                    msg.setContent("enter_world");
                }
            } else {
                msg.setContent("enter_" + currentRoom.getLocalName());
            }
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
                String target;
                int pick;
                do {
                    pick = new Random().nextInt(RoomEnum.values().length);
                    target = RoomEnum.values()[pick].toString();
                } while (target.equals(currentRoom.getLocalName()));

                myAgent.addBehaviour(new RoomChangeBehaviour(target));
                System.out.println(myAgent.getLocalName() + " chce do " + RoomEnum.values()[pick].toString());
            }

        }
    }
}
