package org.example.Util;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
public class MD5Util {
    public static String generateMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashText = number.toString(16);

            // Pad with leading zeros if the hash is not long enough
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }

            return hashText;
        } catch (NoSuchAlgorithmException e) {
            // Handle exception appropriately or throw a custom exception
            e.printStackTrace();
        }
        return null;
    }
}
