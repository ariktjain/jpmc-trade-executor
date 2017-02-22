package com.jpmc.trade.executor.domain;

import java.util.*;

/**
 * Test assumption : Single threaded and hence not using ConcurrentHashMap
 * Created by AA on 22-02-2017.
 */
public final class TradeExecutionReport {

    private enum TradeType {
        BUY('B'),
        SELL('S');

        private final int value;

        TradeType(char value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    /**
     * Amount in USD settled incoming everyday
     * Can be replaced with BigDecimal if the numbers are too large
     */
    private double incomingAmount;
    /**
     * Amount in USD settled outgoing everyday
     * Can be replaced with BigDecimal if the numbers are too large
     */
    private double outgoingAmount;
    /**
     * Ranking of entities based on incoming amount
     * Test assumption : Single threaded and hence not using ConcurrentHashMap
     */
    private HashMap<String, Entity> incomingEntities;
    /**
     * Ranking of entities based on incoming amount
     * Test assumption : Single threaded and hence not using ConcurrentHashMap
     */
    private HashMap<String, Entity> outgoingEntities;

    /**
     * Instantiates an object with parameters
     *
     * @param tradeInstruction Trade instruction sent by JPMC client
     */
    public TradeExecutionReport(TradeInstruction tradeInstruction) {
        incomingEntities = new HashMap();
        outgoingEntities = new HashMap();
        addTradeInstruction(tradeInstruction);
    }

    public double getIncomingAmount() {
        return incomingAmount;
    }

    public void setIncomingAmount(double incomingAmount) {
        this.incomingAmount = incomingAmount;
    }

    public double getOutgoingAmount() {
        return outgoingAmount;
    }

    public void setOutgoingAmount(double outgoingAmount) {
        this.outgoingAmount = outgoingAmount;
    }

    /**
     * No need to deep clone as test suggests a sigle threaded environment
     * @return Map of Entity and the valuation
     */
    public HashMap<String, Entity> getIncomingEntities() {
        return incomingEntities;
    }

    public HashMap<String, Entity> getOutgoingEntities() {
        return outgoingEntities;
    }

    public void addTradeInstruction(TradeInstruction tradeInstruction) {
        double tradeAmountInUSD = tradeInstruction.getPerUnitPrice() * tradeInstruction.getUnits() * tradeInstruction.getAgreedFx();
        if (TradeType.BUY.getValue() == tradeInstruction.getBsIndicator()) {
            setOutgoingAmount(getOutgoingAmount() + tradeAmountInUSD);
            setEntityValuation(tradeInstruction.getEntity(), tradeAmountInUSD, getOutgoingEntities());

        } else if (TradeType.SELL.getValue() == tradeInstruction.getBsIndicator()) {
            setIncomingAmount(getIncomingAmount() + tradeAmountInUSD);
            setEntityValuation(tradeInstruction.getEntity(), tradeAmountInUSD, getIncomingEntities());
        }
    }

    public void evaluateRanking(HashMap<String, Entity> entities) {
        List<Entity> entityList = new ArrayList(entities.values());
        Collections.sort(entityList);
        double previousValuation = 0;
        for (int i = 1, j = 1; i <= entityList.size(); i++) {
            Entity entity = entityList.get(i - 1);
            if (previousValuation > entity.getValuation())
                j = j + 1;
            entity.setRanking(j);
            entities.put(entity.getEntity(), entity);
            previousValuation = entity.getValuation();
        }
    }

    private void setEntityValuation(String entityName, double tradeAmountInUSD, HashMap<String, Entity> entities) {
        Entity entity = null;
        if (entities.containsKey(entityName)) {
            entity = entities.get(entityName);
            entity.setValuation(entity.getValuation() + tradeAmountInUSD);
        } else {
            entity = new Entity(entityName, tradeAmountInUSD);
            entities.put(entityName, entity);
        }
    }
}
