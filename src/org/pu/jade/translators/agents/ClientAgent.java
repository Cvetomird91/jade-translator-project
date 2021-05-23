package org.pu.jade.translators.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.pu.jade.translators.gui.ClientAgentGui;

import java.util.List;

public class ClientAgent extends Agent {

    List<String> desiredLanguages;
    Double desiredRatePerWord;
    ClientAgentGui gui;

    @Override
    public void setup() {
        gui = new ClientAgentGui(this);

        DFAgentDescription dad = new DFAgentDescription();
        dad.setName(getAID());

        ServiceDescription sd = new ServiceDescription();

        sd.setType("translate-text");
        sd.setName("transalte-text-into-multiple-languages");

        dad.addServices(sd);

        try {
            DFService.register(this, dad);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {

            }
        });
    }

    public List<String> getDesiredLanguages() {
        return desiredLanguages;
    }

    public void setDesiredLanguages(List<String> desiredLanguages) {
        this.desiredLanguages = desiredLanguages;
    }

    public Double getDesiredRatePerWord() {
        return desiredRatePerWord;
    }

    public void setDesiredRatePerWord(Double desiredRatePerWord) {
        this.desiredRatePerWord = desiredRatePerWord;
    }
}
