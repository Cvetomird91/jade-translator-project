package org.pu.jade.translators.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.commons.collections4.CollectionUtils;
import org.pu.jade.translators.gui.TranslatorAgentGui;

import java.util.List;

import static org.pu.jade.translators.conf.Constants.COMMUNICATION_INIT_MESSAGE;

public class TranslatorAgent extends Agent {

    private List<String> spokenLanguages;
    private Double ratePerWord;
    private Double discountPercentage;
    private Double wordLimitForDiscount;
    private TranslatorAgentGui gui;
    MessageTemplate mt;
    ACLMessage msg;

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

                if (!CollectionUtils.isEmpty(spokenLanguages)) {
                    MessageTemplate mt = MessageTemplate.MatchConversationId(COMMUNICATION_INIT_MESSAGE);

                    ACLMessage msg = myAgent.receive(mt);

                    if (msg != null) {
                        if (msg.getPerformative() == ACLMessage.CFP) {

                            String languages = msg.getContent();
                            System.out.println(myAgent.getName() + " Desired Languages: " + languages);
                            System.out.println(myAgent.getName() + " Spoken Languages: " + spokenLanguages);

                            ACLMessage replyMsg = msg.createReply();
                            replyMsg.setContent("[Responce]: Spoken Languages: " + spokenLanguages + ", rate per word: " + ratePerWord);//изпращаме цената
                            replyMsg.setPerformative(ACLMessage.PROPOSE);

                            myAgent.send(replyMsg);
                        }

                        if (msg.getPerformative() == ACLMessage.CONFIRM) {
                            System.out.println(msg.getContent());
                        }

                    }
                }
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
