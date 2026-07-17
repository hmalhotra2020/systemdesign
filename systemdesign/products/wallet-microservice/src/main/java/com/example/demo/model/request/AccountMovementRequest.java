package com.example.demo.model.request;

import java.math.BigDecimal;

public class AccountMovementRequest {

    private BigDecimal amount;
    private String currency;
    private String description;
    private boolean allowDebit = true;
    private boolean allowCredit = true;
    private boolean blacklisted = false;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAllowDebit() {
        return allowDebit;
    }

    public void setAllowDebit(boolean allowDebit) {
        this.allowDebit = allowDebit;
    }

    public boolean isAllowCredit() {
        return allowCredit;
    }

    public void setAllowCredit(boolean allowCredit) {
        this.allowCredit = allowCredit;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }
}
