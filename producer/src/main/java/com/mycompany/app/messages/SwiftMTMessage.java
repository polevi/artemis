package com.mycompany.app.messages;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mycompany.app.converters.Base64Serializer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class SwiftMTMessage implements Serializable  {

    private Long id;
    private LocalDate operdate;

    @JsonSerialize( using = Base64Serializer.class)
    private String body;

    public SwiftMTMessage(Long id, LocalDate operdate, String body) {
        this.id = id;
        this.operdate = operdate;
        this.body = body;
    }
}
