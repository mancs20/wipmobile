package com.products.qc;

public class Rack {

    String id = "";
    String intQty = "";
    String balanceQty = "";
    String lot="";
    String size="";

    public Rack(){}

    public Rack(String id, String intQty, String balanceQty, String lot, String size) {
        this.id = id;
        this.intQty = intQty;
        this.balanceQty = balanceQty;
        this.lot=lot;
        this.size=size;

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

    public String getLot(){return lot;}

    public String getSize(){return size;}

    public void setIntQty(String intQty) {
        this.intQty = intQty;
    }

    public String getBalanceQty() {
        return balanceQty;
    }

    public void setBalanceQty(String balanceQty) {
        this.balanceQty = balanceQty;
    }

    public void setLot(String lot){this.lot=lot;}

    public void setSize(String size){this.size=size;}
}
