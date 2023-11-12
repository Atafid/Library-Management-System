package com.example.tp_bibliotheque.Login;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

//CLASSE DES FONCTIONS UTILES A LA CONNEXION

public class LoginUtils {
    //*****************METHODES*****************//

    //Méthode static permettant de renvoyer un Salt aléatoire de 6 bits
    public static String Salt() {
        SecureRandom random = new SecureRandom();
        byte salt[] = new byte[6];
        random.nextBytes(salt);
        return(new String(salt, StandardCharsets.UTF_8));
    }

    //Méthode static calculant le hash d'un mot de passe
    public static String hashFunction(String password, String salt, String pepper, int incrementation) {
        try {
            //Hash à l'aide de l'algorithme SHA-256
            MessageDigest messDigest = MessageDigest.getInstance("SHA-256");

            //Hash du mot de passe avec le salt et le pepper
            byte[] hash = messDigest.digest((salt+password+pepper).getBytes(StandardCharsets.UTF_8));

            //Hashage en boucle
            for(int i=0; i<incrementation-1; i++) {
                hash = messDigest.digest(hash);
            }

            return(new String(hash, StandardCharsets.UTF_8));
        } catch(Exception e) {
            System.out.println(e);
            return(null);
        }
    }

    //Méthode static vérifiant la validité d'un mail
    public static boolean isValidMail(String mail) {
        return(mail.contains("@"));
    }

}
