package com.mycompany.app.swiftmt;

import org.springframework.stereotype.Component;

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

    public String createMT103(int message_id) {
        return mt103.toString().replace("REFERENCE12345", String.valueOf(message_id));
    }

    public int parseMessageId(String mt) {
        int idx1 = mt.indexOf(":20:") + 4;
        int idx2 = mt.indexOf(":23B:");
        return Integer.parseInt(mt.substring(idx1, idx2));
    }
}
