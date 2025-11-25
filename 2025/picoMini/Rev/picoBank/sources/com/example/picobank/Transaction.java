package com.example.picobank;

public class Transaction {
    private String amount;
    private String date;
    private boolean incoming;
    private String name;

    public Transaction(String name2, String date2, String amount2, boolean incoming2) {
        this.name = name2;
        this.date = date2;
        this.amount = amount2;
        this.incoming = incoming2;
    }

    public String getName() {
        return this.name;
    }

    public String getDate() {
        return this.date;
    }

    public String getAmount() {
        return this.amount;
    }

    public boolean isIncoming() {
        return this.incoming;
    }
}
