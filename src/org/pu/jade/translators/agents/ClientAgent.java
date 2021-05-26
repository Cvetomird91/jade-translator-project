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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.pu.jade.translators.conf.Constants.COMMUNICATION_INIT_MESSAGE;

public class ClientAgent extends Agent {

    private String sourceLanguage;
    private List<String> targetLanguages = new ArrayList<>();
    private Double desiredRatePerWord;
    private Integer wordCount;
    private ClientAgentGui gui;
    private Set<AID> translators = new HashSet<>();
    private List<TranslatorProperties> correspondingTranslatorProperties = new ArrayList<>();
    private ACLMessage msg;
    private ACLMessage reply;
    private MessageTemplate mt;
    private int step = 0;
    private boolean notifiedForNoTranslators;
    private ObjectMapper objectMapper = new ObjectMapper();
    private ClientPreferences clientPreferences;
    private static final Double affordablePriceMargin = 1.1;

    //todo: remove when you finish step 2
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

                                for (AID translator : translators) {
                                    msg.addReceiver(translator);
                                }

                                clientPreferences = ClientPreferences.builder()
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

                            List<TranslatorProperties> fullMatch = correspondingTranslatorProperties
                                    .stream()
                                    .filter((agent) -> agent.getSpokenLanguages()
                                            .containsAll(clientPreferences.getTargetLanguages()))
                                    .collect(Collectors.toList());

                            //check if any of the translators that are fluent in all the desired languages at once
                            //have a feasable rate
                            for (int i = 0; i < fullMatch.size(); i++) {
                                if(desiredRatePerWord >= fullMatch.get(i).getRatePerWord()) {
                                    acceptOffer(myAgent, fullMatch.get(i).getAgentAid(), "Agreed to have the text translated to "
                                            + clientPreferences.getTargetLanguages() + " for " + fullMatch.get(i).getRatePerWord() + " per word.");
                                    myAgent.doDelete();
                                    break;
                                }

                                if (desiredRatePerWord < fullMatch.get(i).getRatePerWord()) {
                                    //check if offering 10% more would fit the budget
                                    if (desiredRatePerWord * affordablePriceMargin >= fullMatch.get(i).getRatePerWord()) {
                                        acceptOffer(myAgent, fullMatch.get(i).getAgentAid(), "Agreed to have the text translated to "
                                                + clientPreferences.getTargetLanguages() + " for " + fullMatch.get(i).getRatePerWord() + " per word.");
                                        myAgent.doDelete();
                                        break;
                                    }

                                    if(wordCount > fullMatch.get(i).getWordLimitForDiscount()) {
                                        double newPrice = fullMatch.get(i).getRatePerWord() * (1 - fullMatch.get(i).getDiscountPercentage()/100);
                                        if (newPrice < desiredRatePerWord) {
                                            acceptOffer(myAgent, fullMatch.get(i).getAgentAid(), "Agreed to have the text translated to "
                                                    + clientPreferences.getTargetLanguages() + " for " + newPrice + " per word.");
                                            myAgent.doDelete();
                                            break;
                                        }
                                    }
                                }

                                correspondingTranslatorProperties.remove(fullMatch.get(i));
                            }

                            //if full language match deals don't match the requirements work with individual
                            //translators
                            List<String> handledLanguages = new ArrayList<>();
                            targetLanguages.forEach((lang) -> {
                                List<TranslatorProperties> matchingAgents = correspondingTranslatorProperties
                                        .stream()
                                        .filter((agent) -> agent.getSpokenLanguages()
                                                .contains(lang))
                                        .collect(Collectors.toList());

                                matchingAgents.forEach((agent) -> {
                                    if(desiredRatePerWord >= agent.getRatePerWord()) {
                                        acceptOffer(myAgent, agent.getAgentAid(), "Agreed to have the text translated to "
                                                + lang + " for " + agent.getRatePerWord() + " per word.");
                                        handledLanguages.add(lang);
                                        return;
                                    }

                                    if (desiredRatePerWord < agent.getRatePerWord()) {
                                        //check if offering 10% more would fit the budget
                                        if (desiredRatePerWord * affordablePriceMargin >= agent.getRatePerWord()) {
                                            acceptOffer(myAgent, agent.getAgentAid(), "Agreed to have the text translated to "
                                                    + lang + " for " + agent.getRatePerWord() + " per word.");
                                            handledLanguages.add(lang);
                                            return;
                                        }

                                        if(wordCount > agent.getWordLimitForDiscount()) {
                                            double newPrice = agent.getRatePerWord() * (1 - agent.getDiscountPercentage()/100);
                                            if (newPrice < desiredRatePerWord) {
                                                acceptOffer(myAgent, agent.getAgentAid(), "Agreed to have the text translated to "
                                                        + lang + " for " + newPrice + " per word.");
                                                handledLanguages.add(lang);
                                                return;
                                            }
                                        }
                                    }
                                });
                            });

                            targetLanguages.removeAll(handledLanguages);
                            if (targetLanguages.isEmpty()) {
                                myAgent.doDelete();
                            } else {
                                step = 0;
                                notifiedForNoTranslators = false;
                            }

                            break;
                    }
                }
            }
        });
    }

    private void acceptOffer(Agent myAgent, AID receiverAID, String message) {
        msg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
        msg.setContent(message);

        msg.setConversationId(COMMUNICATION_INIT_MESSAGE);
        msg.setReplyWith("start-" + System.currentTimeMillis());

        msg.addReceiver(receiverAID);
        myAgent.send(msg);
    }

    private void rejectOffer(Agent myAgent, AID receiverAID) {
        msg = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
        msg.setContent("Success!");

        msg.setConversationId(COMMUNICATION_INIT_MESSAGE);
        msg.setReplyWith("start-" + System.currentTimeMillis());

        msg.addReceiver(receiverAID);
        myAgent.send(msg);
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

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

}
