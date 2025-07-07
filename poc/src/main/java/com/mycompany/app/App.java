package com.mycompany.app;

import java.io.StringWriter;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        createXML();
    }

    void createXML() {
        Encoder encoder = Base64.getEncoder();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElement = doc.createElement("root");
            doc.appendChild(rootElement);
            Element itemsElement = doc.createElement("items");
            rootElement.appendChild(itemsElement);
            for (int i=0; i<5; i++) {
                Element itemElement = doc.createElement("item");
                itemElement.setAttribute("id", "1");
                itemElement.setAttribute("body", encoder.encodeToString("hello".getBytes()));
                itemsElement.appendChild(itemElement);
            }

            TransformerFactory transformerFactory =  TransformerFactory.newInstance();
            Transformer transformer;
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StringWriter w = new StringWriter();
            StreamResult result =  new StreamResult(w);
            transformer.transform(source, result);
            log.info(w.toString());
        }
        catch(Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
