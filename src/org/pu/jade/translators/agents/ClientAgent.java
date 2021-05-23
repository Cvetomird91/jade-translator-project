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

    String sourceLanguage;
    List<String> targetLanguages;
    Double desiredRatePerWord;
    Integer textWordCount;
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

    public List<String> getTargetLanguages() {
        return targetLanguages;
    }

    public void setTargetLanguages(List<String> targetLanguages) {
        this.targetLanguages = targetLanguages;
    }

    public Double getDesiredRatePerWord() {
        return desiredRatePerWord;
    }

    public void setDesiredRatePerWord(Double desiredRatePerWord) {
        this.desiredRatePerWord = desiredRatePerWord;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public Integer getTextWordCount() {
        return textWordCount;
    }

    public void setTextWordCount(Integer textWordCount) {
        this.textWordCount = textWordCount;
    }
}
