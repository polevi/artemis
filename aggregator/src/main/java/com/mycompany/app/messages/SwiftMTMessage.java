package com.mycompany.app.messages;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.jms.Message;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class SwiftMTMessage implements Serializable {

    @JsonIgnore
    Message message;

    Long id;
    LocalDate operdate;
    String body;
    
    public SwiftMTMessage(Long id, LocalDate operdate, String body) {
        this.id = id;
        this.operdate = operdate;
        this.body = body;
    }
}
