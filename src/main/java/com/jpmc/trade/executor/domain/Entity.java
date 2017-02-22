package com.jpmc.trade.executor.domain;

import java.util.Objects;

/**
 * Created by Arikt on 22-02-2017.
 */
public final class Entity implements Comparable{
    private final String entity;
    private double valuation;
    private int ranking;

    public Entity(String entity, double valuation) {
        this.entity = entity;
        this.valuation = valuation;
    }

    public Entity(String entity) {
        this(entity, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity1 = (Entity) o;
        return Objects.equals(entity, entity1.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity);
    }

    public String getEntity() {
        return entity;
    }

    public double getValuation() {
        return valuation;
    }

    public void setValuation(double valuation) {
        this.valuation = valuation;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    @Override
    public int compareTo(Object o) {
        //Multiplying by -1 to reverse sort so that higher valuation is at the top
        return Double.compare(getValuation(), ((Entity)o).getValuation()) * -1;
    }
}
