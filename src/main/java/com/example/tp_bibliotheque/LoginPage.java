package com.example.tp_bibliotheque;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class LoginPage {
    protected static String Salt() {
        SecureRandom random = new SecureRandom();
        byte salt[] = new byte[6];
        random.nextBytes(salt);
        return(new String(salt, StandardCharsets.UTF_8));
    }

    protected static String hashFunction(String password, String salt, String pepper, int incrementation) {
        try {
            MessageDigest messDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messDigest.digest((salt+password+pepper).getBytes(StandardCharsets.UTF_8));

            for(int i=0; i<incrementation-1; i++) {
                hash = messDigest.digest(hash);
            }

            return(new String(hash, StandardCharsets.UTF_8));
        } catch(Exception e) {
            System.out.println(e);
            return(null);
        }
    }

    protected static boolean isValidMail(String mail) {
        return(mail.contains("@"));
    }

}
