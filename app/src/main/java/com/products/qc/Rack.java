package com.products.qc;

public class Rack {

    String id = "";
    String intQty = "";
    String balanceQty = "";

    public Rack(){}

    public Rack(String id, String intQty, String balanceQty) {
        this.id = id;
        this.intQty = intQty;
        this.balanceQty = balanceQty;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntQty() {
        return intQty;
    }

    public void setIntQty(String intQty) {
        this.intQty = intQty;
    }

    public String getBalanceQty() {
        return balanceQty;
    }

    public void setBalanceQty(String balanceQty) {
        this.balanceQty = balanceQty;
    }
}
