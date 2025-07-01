package com.mycompany.app.messages;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class SwiftMTMessage implements Serializable  {
    private Integer id;
    private LocalDate operdate;
    private String body;

    public SwiftMTMessage(Integer id, LocalDate operdate, String body) {
        this.id = id;
        this.operdate = operdate;
        this.body = body;
    }
}
