package org.pu.jade.translators.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.pu.jade.translators.gui.TranslatorAgentGui;

import java.util.List;

public class TranslatorAgent extends Agent {

    List<String> spokenLanguages;
    Double ratePerWord;
    TranslatorAgentGui gui;

    public void setSpokenLanguages(List<String> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public void setRatePerWord(Double ratePerWord) {
        this.ratePerWord = ratePerWord;
    }

    @Override
    public void setup() {
        gui = new TranslatorAgentGui(this);

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

}
