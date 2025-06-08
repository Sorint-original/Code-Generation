package com.bankapp.Backend.service;

import java.math.BigInteger;
import java.util.Random;

public class IBANGenerator {

    private static final String COUNTRY_CODE = "NL";
    private static final String BANK_CODE = "INBK"; // InBanking's bank code
    private final Random random = new Random();

    public String generateDutchIBAN() {
        String accountNumber = generateRandomAccountNumber();
        String ibanWithoutChecksum = COUNTRY_CODE + "00" + BANK_CODE + accountNumber;
        String checksum = calculateIBANChecksum(ibanWithoutChecksum);
        return COUNTRY_CODE + checksum + BANK_CODE + accountNumber;
    }

    private String generateRandomAccountNumber() {
        long number = Math.abs(random.nextLong()) % 1_000_000_0000L; // Max 10 digits
        return String.format("%010d", number);
    }

    private String calculateIBANChecksum(String iban) {
        String rearranged = iban.substring(4) + iban.substring(0, 4);

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
