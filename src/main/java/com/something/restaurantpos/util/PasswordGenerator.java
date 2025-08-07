package com.something.restaurantpos.util;

import java.security.SecureRandom;

public class PasswordGenerator {
    
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final String ALL_CHARS = LOWERCASE + NUMBERS;
    
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword() {
        return generatePassword(8);
    }

    public static String generatePassword(int length) {
        if (length < 6) {
            length = 6; // Đảm bảo tối thiểu 6 ký tự
        }
        
        StringBuilder password = new StringBuilder(length);
        
        // Đảm bảo có ít nhất 1 chữ cái và 1 số
        password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        password.append(NUMBERS.charAt(RANDOM.nextInt(NUMBERS.length())));
        
        // Thêm các ký tự ngẫu nhiên còn lại
        for (int i = 2; i < length; i++) {
            password.append(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }
        
        // Trộn lại các ký tự để tăng tính ngẫu nhiên
        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}
