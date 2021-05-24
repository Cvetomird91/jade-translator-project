package org.pu.jade.translators.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pu.jade.translators.gui.ClientAgentGui;

import java.util.List;

public class ClientAgent extends Agent {

    String sourceLanguage;
    List<String> targetLanguages;
    Double desiredRatePerWord;
    ClientAgentGui gui;
    private AID[] translators;
    ACLMessage msg;

    @Override
    public void setup() {
        gui = new ClientAgentGui(this);

        addBehaviour(new TickerBehaviour(this, 15000) {
            @Override
            protected void onTick() {

                if (!StringUtils.isEmpty(sourceLanguage) && !CollectionUtils.isEmpty(targetLanguages) &&
                        desiredRatePerWord > 0) {

                    System.out.println(myAgent.getName() + "Looking for translating from " + sourceLanguage + " to " + targetLanguages);

                    DFAgentDescription dad = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("translate-text");

                    dad.addServices(sd);

                    try {
                        DFAgentDescription[] searchResult = DFService.search(myAgent, dad);

                        translators = new AID[searchResult.length];

                        for (int i = 0; i < searchResult.length; i++) {
                            translators[i] = searchResult[i].getName();
                        }
                    } catch (FIPAException e) {
                        e.printStackTrace();
                    }

                    if(translators.length > 0) {
                        myAgent.addBehaviour(new FindTranslatorBehaviour());
                    }else
                    {
                        System.out.println("There is are no translators available at the moment!");
                    }
                }
            }
        });

    }

    private class FindTranslatorBehaviour extends Behaviour {
        int step = 0;

        @Override
        public void action() {
            switch(step) {

                case 0:
                    System.out.println(myAgent.getName() + ": Sending queries for offers");
                    msg = new ACLMessage(ACLMessage.CFP);//за перформатив за оферти използваме CFP

                    for (int i = 0; i < translators.length; i++) {//добавяме всичките получатали на това съобщение
                        msg.addReceiver(translators[i]);
                    }

                    msg.setContent(targetLanguages.toString());//като съдържание слагаме името на предмета който искаме да закупим

                    msg.setConversationId("start-item-trade");//слагаме id на разговора за да го иползваме за определене на отговор по това съобщение
                    msg.setReplyWith("start-" + System.currentTimeMillis());//слагаме допълнтелен уникален "таг" по който да се ориентираме за конкретния отговор

                    myAgent.send(msg);

                    break;
            }
        }

        public boolean done() {
            return true;
        }
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

}
