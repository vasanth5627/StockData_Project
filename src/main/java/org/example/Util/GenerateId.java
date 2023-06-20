package org.example.Util;

public class GenerateId {
    public static String generateId(String field, String exchange, String companySymobl) {
        String input = field + exchange + companySymobl;
        return MD5Util.generateMD5(input);
    }
}
