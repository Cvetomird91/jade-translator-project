package org.pu.jade.translators.agents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.pu.jade.translators.models.ClientPreferences;
import org.pu.jade.translators.models.TranslatorProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.pu.jade.translators.conf.Constants.COMMUNICATION_INIT_MESSAGE;

public class ClientAgent extends Agent {

    private String sourceLanguage;
    private List<String> targetLanguages = new ArrayList<>();
    private Double desiredRatePerWord;
    private ClientAgentGui gui;
    private List<AID> translators = new ArrayList<>();
    private List<TranslatorProperties> correspondingTranslatorProperties = new ArrayList<>();
    private ACLMessage msg;
    private ACLMessage reply;
    private MessageTemplate mt;
    private int step = 0;
    private boolean notifiedForNoTranslators;
    private ObjectMapper objectMapper = new ObjectMapper();
    private boolean debug;

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
                            if (!notifiedForNoTranslators) {
                                System.out.println(myAgent.getName() + ": Looking for translating from " + sourceLanguage + " to " + targetLanguages);
                            }

                            DFAgentDescription dad = new DFAgentDescription();
                            ServiceDescription sd = new ServiceDescription();
                            sd.setType("translate-text");

                            dad.addServices(sd);

                            try {
                                DFAgentDescription[] searchResult = DFService.search(myAgent, dad);

                                for (int i = 0; i < searchResult.length; i++) {
                                    translators.add(searchResult[i].getName());
                                }
                            } catch (FIPAException e) {
                                e.printStackTrace();
                            }

                            if(translators.size() > 0) {
                                System.out.println(myAgent.getName() + ": Sending queries for offers");
                                msg = new ACLMessage(ACLMessage.CFP);

                                for (int i = 0; i < translators.size(); i++) {
                                    msg.addReceiver(translators.get(i));
                                }

                                ClientPreferences clientPreferences = ClientPreferences.builder()
                                        .sourceLanguage(sourceLanguage)
                                        .targetLanguages(targetLanguages)
                                        .build();

                                try {
                                    //System.out.println(objectMapper.writeValueAsString(clientLanguages));
                                    msg.setContent(objectMapper.writeValueAsString(clientPreferences));
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }

                                msg.setConversationId(COMMUNICATION_INIT_MESSAGE);
                                msg.setReplyWith("start-" + System.currentTimeMillis());

                                myAgent.send(msg);

                                mt = MessageTemplate.and(MessageTemplate.MatchConversationId(COMMUNICATION_INIT_MESSAGE),
                                        MessageTemplate.MatchInReplyTo(msg.getReplyWith()));

                                step++;

                            } else {
                                if (!notifiedForNoTranslators) {
                                    System.out.println("There are no translators available at the moment!");
                                    notifiedForNoTranslators = true;
                                }
                            }

                            break;
                        case 1:
                            reply = myAgent.receive(mt);
                            if (reply != null) {
                                if (reply.getPerformative() == ACLMessage.PROPOSE) {
                                    System.out.println(reply.getContent());
                                    translators.remove(reply.getSender());
                                    TranslatorProperties translatorProperties;
                                    try {
                                        translatorProperties = objectMapper.readValue(reply.getContent(), TranslatorProperties.class);
                                        translatorProperties.setAgentAid(reply.getSender());

                                        if (translatorProperties.getSpokenLanguages().contains(sourceLanguage)) {
                                            List<String> targetLanguagesSupportedByTranslator = translatorProperties.getSpokenLanguages()
                                                    .stream()
                                                    .filter((lang) -> targetLanguages.contains(lang) && lang != sourceLanguage)
                                                    .collect(Collectors.toList());

                                            if(!CollectionUtils.isEmpty(targetLanguagesSupportedByTranslator)) {
                                                correspondingTranslatorProperties.add(translatorProperties);
                                            }

                                        }
                                    } catch (JsonProcessingException e) {
                                        e.printStackTrace();
                                    }

                                    if (CollectionUtils.isEmpty(translators)) {
                                        step++;
                                    }
                                }
                            }

                            break;
                        case 2:
                            if (!debug) {
                                System.out.println(correspondingTranslatorProperties);
                                debug = true;
                            }
//                                    msg = new ACLMessage(ACLMessage.CONFIRM);
//                                    msg.setContent("Success!");
//
//                                    msg.setConversationId(COMMUNICATION_INIT_MESSAGE);
//                                    msg.setReplyWith("start-" + System.currentTimeMillis());
//
//                                    msg.addReceiver(reply.getSender());
//                                    myAgent.send(msg);

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
