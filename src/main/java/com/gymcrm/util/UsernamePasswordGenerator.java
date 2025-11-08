package com.gymcrm.util;

import java.security.SecureRandom;

public class UsernamePasswordGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    //Generate a random password of length 10
    public static String generatePassword() {
        int length = 10;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
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
