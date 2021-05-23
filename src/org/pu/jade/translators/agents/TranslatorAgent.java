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

    private List<String> spokenLanguages;
    private Double ratePerWord;
    private Double discountPercentage;
    private Double wordLimitForDiscount;
    private TranslatorAgentGui gui;

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

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Double getWordLimitForDiscount() {
        return wordLimitForDiscount;
    }

    public void setWordLimitForDiscount(Double wordLimitForDiscount) {
        this.wordLimitForDiscount = wordLimitForDiscount;
    }

    public void setSpokenLanguages(List<String> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public void setRatePerWord(Double ratePerWord) {
        this.ratePerWord = ratePerWord;
    }

}
