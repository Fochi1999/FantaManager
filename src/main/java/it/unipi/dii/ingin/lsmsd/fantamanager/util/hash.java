package it.unipi.dii.ingin.lsmsd.fantamanager.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class hash {
    public static String MD5(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(password.getBytes());
        BigInteger bigInt = new BigInteger(1, hash);
        String hashString = bigInt.toString(16);
        while (hashString.length() < 32) {
            hashString = "0" + hashString;
        }
        return hashString;
    }
}
