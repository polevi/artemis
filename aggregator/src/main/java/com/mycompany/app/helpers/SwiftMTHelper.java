package com.mycompany.app.helpers;

import java.io.StringWriter;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mycompany.app.messages.SwiftMTMessage;

@Component
public class SwiftMTHelper {

    private static final StringBuilder mt103 = new StringBuilder()
        .append("{1:F01BANKBEBBAXXX1234567890}{2:O1031130050901BANKBEBBAXXX12345678900509011311N}{3:{108:MT103}}")
        .append("{4:")
        .append(":20:REFERENCE12345")
        .append(":23B:CRED")
        .append(":32A:230501EUR123456,78")
        .append(":50A:/12345678901234567890")
        .append("MR. JOHN DOE")
        .append(":59:/23456789012345678901")
        .append("MS. JANE SMITH")
        .append(":70:INVOICE 987654")
        .append(":71A:SHA")
        .append("-}");

    public static String createMT103(int message_id) {
        return mt103.toString().replace("REFERENCE12345", String.valueOf(message_id));
    }

    public static int parseMessageId(String mt) {
        int idx1 = mt.indexOf(":20:") + 4;
        int idx2 = mt.indexOf(":23B:");
        return Integer.parseInt(mt.substring(idx1, idx2));
    }

    public static String createED503(List<SwiftMTMessage> items, Long edno) throws RuntimeException {
        Encoder encoder = Base64.getEncoder();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element rootElement = doc.createElement("root");
            rootElement.setAttribute("edno", edno.toString());
            doc.appendChild(rootElement);
            Element itemsElement = doc.createElement("items");
            rootElement.appendChild(itemsElement);

            for(SwiftMTMessage m: items) {
                Element itemElement = doc.createElement("item");
                itemElement.setAttribute("id", m.getId().toString());
                itemElement.setAttribute("body", encoder.encodeToString(m.getBody().getBytes()));
                itemsElement.appendChild(itemElement);
            }

            TransformerFactory transformerFactory =  TransformerFactory.newInstance();
            Transformer transformer;
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            
            try(StringWriter w = new StringWriter()) {
                StreamResult result =  new StreamResult(w);
                transformer.transform(source, result);
                return w.toString();
            }
        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }        
    }
}
