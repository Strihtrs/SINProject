package app;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public final class Helper {

    public static AID findAgentByName(Agent a, String name) {

        DFAgentDescription dfad = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(name);
        dfad.addServices(sd);
        DFAgentDescription[] results;

        try {
            results = DFService.search(a, dfad);
            if (results.length > 0)
                return results[0].getName();

        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void registerInDFService(Agent a, String name) {

        DFAgentDescription dfad = new DFAgentDescription();
        dfad.setName(a.getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setName(name);
        sd.setType(name);
        dfad.addServices(sd);

        try {
            DFService.register(a, dfad);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
