package com.mycompany.app.messages;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.jms.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class SwiftMTMessage implements Serializable {

    private static final long serialVersionUID = 1;

    transient Message message;

    Long id;
    LocalDate operdate;
    String body;
    
    public SwiftMTMessage(Long id, LocalDate operdate, String body) {
        this.id = id;
        this.operdate = operdate;
        this.body = body;
    }
}
