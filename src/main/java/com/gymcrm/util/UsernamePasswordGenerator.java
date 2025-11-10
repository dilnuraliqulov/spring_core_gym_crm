package com.gymcrm.util;

import java.security.SecureRandom;

public class UsernamePasswordGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    //Generate a random password of length 10
    public static char[] generatePassword(int length) {
        char[] password = new char[length];
        for (int i = 0; i < length; i++) {
            password[i] = (char) (33 + RANDOM.nextInt(94));
        }
        return password;
    }


    // Generate unique username based on firstName, lastName and existing usernames
    public static String generateUsername(String firstName, String lastName, java.util.Set<String> existingUsernames) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int counter = 1;

        while (existingUsernames.contains(username)) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }
}
