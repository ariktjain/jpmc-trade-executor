package com.jpmc.trade.executor.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * Created by Arikt on 22-02-2017.
 */
public final class TradeInstruction {
    private String entity;
    private char bsIndicator;
    private double agreedFx;
    private String currency;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
    private Date instructionDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm a z")
    private Date settlementDate;

    private int units;
    private double perUnitPrice;

    public TradeInstruction() {
    }

    public TradeInstruction(String entity, char bsIndicator, double agreedFx, String currency, Date instructionDate, Date settlementDate, int units, double perUnitPrice) {
        this.entity = entity;
        this.bsIndicator = bsIndicator;
        this.agreedFx = agreedFx;
        this.currency = currency;
        this.instructionDate = instructionDate;
        this.settlementDate = settlementDate;
        this.units = units;
        this.perUnitPrice = perUnitPrice;
    }

    public String getEntity() {
        return entity;
    }

    public char getBsIndicator() {
        return bsIndicator;
    }

    public double getAgreedFx() {
        return agreedFx;
    }

    public String getCurrency() {
        return currency;
    }

    public Date getInstructionDate() {
        return instructionDate;
    }

    public Date getSettlementDate() {
        return settlementDate;
    }

    public int getUnits() {
        return units;
    }

    public double getPerUnitPrice() {
        return perUnitPrice;
    }
}
