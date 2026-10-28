package model;

public interface InterestBearing {

    double interestRate();

    default double yearlyInterest() {
        return getBalance() * interestRate() / 100.0;
    }

    long getBalance();
}