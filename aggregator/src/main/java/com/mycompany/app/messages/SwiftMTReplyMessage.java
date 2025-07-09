package com.mycompany.app.messages;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class SwiftMTReplyMessage implements Serializable {

    int status;
    String errorDescription;
    
    public SwiftMTReplyMessage(int status, String errorDescription) {
        this.status = status;
        this.errorDescription = errorDescription;
    }
}
