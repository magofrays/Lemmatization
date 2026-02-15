package org.magofrays;

import jakarta.annotation.PostConstruct;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class MorphDict {

    @AllArgsConstructor
    public static class LemmataDesc{
        public String alias;
        public String description;

        @Override
        public String toString() {
            return description;
        }
    }

    @Value("${app.dict.filename}")
    private String filename;
    @Value("${app.dict.lemmas-count}")
    private Long lemmasCount;

    @Getter
    private final Map<String, List<String>> lemmata = new HashMap<>(); // Лемма <-> Часть речи
    @Getter
    private final Map<String, List<String>> formata = new HashMap<>(); // Форма <-> Часть речи
    @Getter
    private final Map<String, String> form = new HashMap<>(); // Словоформа <-> Лемма
    @Getter
    private final Map<String, LemmataDesc> grammemes = new HashMap<>(); // Код части речи <-> расшифровка



    @PostConstruct
    @SneakyThrows
    public void init(){
        ClassPathResource resource = new ClassPathResource(filename);

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

        try(InputStream is = resource.getInputStream()) {
            BufferedInputStream bis = new BufferedInputStream(is, 1024 * 1024);
            XMLStreamReader reader = factory.createXMLStreamReader(bis);

            List<String> lemmasG = new ArrayList<>();
            boolean insideL = false;
            String currentLemma = null;
            int lemmaCount = 0;
            int formCount = 0;

            while (reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String elementName = reader.getLocalName();
                        if ("grammeme".equals(elementName)) {
                            String name = null;
                            String alias = null;
                            String description = null;
                            while (reader.hasNext()) {
                                event = reader.next();
                                if (event == XMLStreamConstants.START_ELEMENT) {
                                    String tag = reader.getLocalName();
                                    if ("name".equals(tag)) {
                                        name = reader.getElementText();
                                    } else if ("alias".equals(tag)) {
                                        alias = reader.getElementText();
                                    } else if ("description".equals(tag)) {
                                        description = reader.getElementText();
                                    }
                                } else if (event == XMLStreamConstants.END_ELEMENT &&
                                        "grammeme".equals(reader.getLocalName())) {
                                    break;
                                }
                            }

                            if (name != null && alias != null && description != null) {
                                grammemes.put(name, new LemmataDesc(alias, description));
                            }
                        }

                        if ("l".equals(elementName)) {
                            currentLemma = reader.getAttributeValue(null, "t");
                            lemmasG = new ArrayList<>();
                            insideL = true;
                        }
                        if("g".equals(elementName)){
                            if(insideL){
                                String gValue = reader.getAttributeValue(null, "v");
                                lemmasG.add(gValue);
                            }
                        }

                        if ("f".equals(elementName) && currentLemma != null) {
                            String wordForm = reader.getAttributeValue(null, "t");
                            if (wordForm != null) {
                                form.put(wordForm, currentLemma);
                                formCount++;
                            }
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        if("l".equals(reader.getLocalName())){
                            lemmata.put(currentLemma, lemmasG);
                            insideL = false;
                        }
                        if ("lemma".equals(reader.getLocalName())) {
                            lemmaCount++;
                            currentLemma = null;

                            if (lemmaCount % 10000 == 0) {
                                System.out.printf("Обработано %d лемм, %d форм%n", lemmaCount, formCount);
                            }
                            if(lemmaCount >= lemmasCount){
                                System.out.printf("Парсинг завершен! Лемм: %d, Форм: %d, Граммем: %d%n",
                                        lemmaCount, form.size(), grammemes.size());
                                reader.close();
                                return;
                            }
                        }
                        break;
                }
            }

            reader.close();
            System.out.printf("Парсинг завершен! Лемм: %d, Форм: %d, Граммем: %d%n",
                    lemmaCount, form.size(), grammemes.size());
        }
    }
}