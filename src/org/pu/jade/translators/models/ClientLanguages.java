package org.pu.jade.translators.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClientLanguages {
    String sourceLanguage;
    List<String> targetLanguages;
}
