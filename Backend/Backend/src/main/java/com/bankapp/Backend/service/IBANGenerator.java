package com.bankapp.Backend.service;

import java.math.BigInteger;
import java.util.Random;

public class IBANGenerator {
    private static final String COUNTRY_CODE = "NL";
    private static final String BANK_CODE = "INBK"; // InBanking's bank code

    public static void main(String[] args) {
        String iban = generateDutchIBAN();
        System.out.println("Generated IBAN: " + iban);
    }

    public static String generateDutchIBAN() {
        String accountNumber = generateRandomAccountNumber();
        String ibanWithoutChecksum = COUNTRY_CODE + "00" + BANK_CODE + accountNumber;
        String checksum = calculateIBANChecksum(ibanWithoutChecksum);
        return COUNTRY_CODE + checksum + BANK_CODE + accountNumber;
    }

    private static String generateRandomAccountNumber() {
        Random rand = new Random();
        long number = Math.abs(rand.nextLong()) % 1_000_000_0000L; // Max 10 digits
        return String.format("%010d", number);
    }

    private static String calculateIBANChecksum(String iban) {
        // Move country code and checksum to the end
        String rearranged = iban.substring(4) + iban.substring(0, 4);

        // Replace letters with numbers (A=10, B=11, ..., Z=35)
        StringBuilder numericIBAN = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) {
                numericIBAN.append((int) c - 55);
            } else {
                numericIBAN.append(c);
            }
        }

        BigInteger ibanNumber = new BigInteger(numericIBAN.toString());
        int checksum = 98 - ibanNumber.mod(BigInteger.valueOf(97)).intValue();
        return String.format("%02d", checksum);
    }
}

