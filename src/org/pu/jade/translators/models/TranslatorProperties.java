package org.pu.jade.translators.models;

import jade.core.AID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorProperties {
    private List<String> spokenLanguages;
    private Double ratePerWord;
    private Double discountPercentage;
    private Double wordLimitForDiscount;
    private AID agentAid;
}
