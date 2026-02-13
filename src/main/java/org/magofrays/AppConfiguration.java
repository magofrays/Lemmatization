package org.magofrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.parsers.DocumentBuilderFactory;

@Configuration
public class AppConfiguration {

    @Bean
    DocumentBuilderFactory documentBuilderFactory(){
        return DocumentBuilderFactory.newInstance();
    }

}
