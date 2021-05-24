package org.pu.jade.translators.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.pu.jade.translators.gui.ClientAgentGui;

import java.util.List;

import static org.pu.jade.translators.conf.Constants.COMMUNICATION_INIT_MESSAGE;

public class ClientAgent extends Agent {

    String sourceLanguage;
    List<String> targetLanguages;
    Double desiredRatePerWord;
    ClientAgentGui gui;
    private AID[] translators;
    ACLMessage msg;
    ACLMessage reply;
    MessageTemplate mt;
    int step = 0;

    @Override
    public void setup() {
        gui = new ClientAgentGui(this);

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {

                if (!StringUtils.isEmpty(sourceLanguage) && !CollectionUtils.isEmpty(targetLanguages) &&
                        desiredRatePerWord > 0) {

                    switch (step) {
                        case 0:
                            System.out.println(myAgent.getName() + ": Looking for translating from " + sourceLanguage + " to " + targetLanguages);

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
                                System.out.println(myAgent.getName() + ": Sending queries for offers");
                                msg = new ACLMessage(ACLMessage.CFP);

                                for (int i = 0; i < translators.length; i++) {
                                    msg.addReceiver(translators[i]);
                                }

                                msg.setContent(targetLanguages.toString());

                                msg.setConversationId(COMMUNICATION_INIT_MESSAGE);
                                msg.setReplyWith("start-" + System.currentTimeMillis());
                                step++;

                                myAgent.send(msg);

                                mt = MessageTemplate.and(MessageTemplate.MatchConversationId(COMMUNICATION_INIT_MESSAGE),//подогтваме темплейта който ще иползваме за получените отговори
                                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                            } else {
                                System.out.println("There is are no translators available at the moment!");
                            }

                            break;
                        case 1:
                            reply = myAgent.receive(mt);
                            if (reply != null) {
                                if (reply.getPerformative() == ACLMessage.PROPOSE) {
                                    System.out.println(reply.getContent());
                                }
                            }

                            break;
                    }
                }
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

}
