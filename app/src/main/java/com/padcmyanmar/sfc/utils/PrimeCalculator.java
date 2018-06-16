package com.padcmyanmar.sfc.utils;

public class PrimeCalculator {

    public static String calcPrime(int... numbers) {
        String primes = "";
        for (int i = 0; i < numbers.length; i++) {
            if (isPrime(numbers[i])) {
                primes = primes + String.valueOf(numbers[i]) + ",";
            }
        }
        return primes;
    }

    private static boolean isPrime(int number) {
        if (number == 2) {
            return true;
        } else {
            for (int i = 2; i < number; i++) {
                if (number % i == 0) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }
}
