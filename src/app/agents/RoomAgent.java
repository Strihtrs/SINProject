package app.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class RoomAgent extends Agent {

    private Set<AID> sensorList;
    private Set<AID> roomList;
    private int peopleCount;

    private boolean isLocked;

    public RoomAgent() {
        sensorList = new HashSet<>();
        roomList = new HashSet<>();
    }

    public Set<AID> GetNextRooms() {
        return roomList;
    }

    @Override
    protected void setup() {
        super.setup();

        addBehaviour(new PersonEntersRoomBehaviour());
    }

    class PersonEntersRoomBehaviour extends CyclicBehaviour {

        @Override
        public void action() {

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null && Objects.equals(msg.getContent(), "enter")) {


                ACLMessage reply = msg.createReply();

                if (!isLocked) {
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    System.out.println(myAgent.getLocalName() + " prijal " + msg.getSender().getLocalName());
                    peopleCount++;
                } else {
                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    System.out.println(myAgent.getName() + " zamitnul " + msg.getSender().getName());
                }

                myAgent.send(reply);
            } else {
                block();
            }
        }
    }
}
