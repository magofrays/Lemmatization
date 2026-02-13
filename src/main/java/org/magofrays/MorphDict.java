package org.magofrays;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MorphDict {
    public static class LemmataDesc{
        public String alias;
        public String description;
    }

    private final DocumentBuilderFactory documentBuilderFactory;

    @Value("@{app.dict.filename}")
    private String filename;

    private final Map<String, String> lemmata = new HashMap<>(); // Лемма <-> Часть речи
    private final Map<String, String> form = new HashMap<>(); // Словоформы от леммы
    private final Map<String, LemmataDesc> grammemes = new HashMap<>(); // Код части речи <-> расшифровка

    @PostConstruct
    @SneakyThrows
    public void init(){
        var documentBuilder = documentBuilderFactory.newDocumentBuilder().parse(filename);
        var grammemeList = documentBuilder.getElementsByTagName("grammemes");
        var lemmaList = documentBuilder.getElementsByTagName("lemmata");

    }


    public Map<String, String> getLemmata() {
        return lemmata;
    }
}
