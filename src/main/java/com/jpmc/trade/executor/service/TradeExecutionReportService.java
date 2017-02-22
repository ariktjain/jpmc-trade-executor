package com.jpmc.trade.executor.service;

import com.jpmc.trade.executor.domain.Entity;
import com.jpmc.trade.executor.domain.TradeExecutionReport;
import com.jpmc.trade.executor.domain.TradeInstruction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Arikt on 22-02-2017.
 * Test assumption : Single threaded and hence not using ConcurrentHashMap
 * Classic singleton implementation assuming the environment is single threaded
 */
public class TradeExecutionReportService {

    private enum DAY_OF_WEEK {
        SUNDAY(1),
        MONDAY(2),
        TUESDAY(3),
        WEDNESDAY(4),
        THURSDAY(5),
        FRIDAY(6),
        SATURDAY(7);

        private final int value;

        DAY_OF_WEEK(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

    /**
     * Test assumption : Single threaded and hence not using ConcurrentHashMap
     */
    private static HashMap<String, TradeExecutionReport> report;
    private static TradeExecutionReportService instance = null;

    private TradeExecutionReportService() {
        report = new HashMap();
    }

    public static TradeExecutionReportService getInstance() {
        if (instance == null) {
            instance = new TradeExecutionReportService();
        }
        return instance;
    }

    public void addTradeInstructionToReport(TradeInstruction tradeInstruction) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(tradeInstruction.getSettlementDate());
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if(tradeInstruction.getCurrency().equals("AED") || tradeInstruction.getCurrency().equals("SAR"))
        {
            if(dayOfWeek == DAY_OF_WEEK.FRIDAY.getValue())
                calendar.add(Calendar.DATE, 2);
            else if(dayOfWeek == DAY_OF_WEEK.SATURDAY.getValue())
                calendar.add(Calendar.DATE, 1);
        }
        else
        {
            if(dayOfWeek == DAY_OF_WEEK.SATURDAY.getValue())
                calendar.add(Calendar.DATE, 2);
            else if(dayOfWeek == DAY_OF_WEEK.SUNDAY.getValue())
                calendar.add(Calendar.DATE, 1);
        }

        String settlementDate = dateFormat.format(calendar.getTime());
        if (settlementDate != null)
            if (report.containsKey(settlementDate))
                report.get(settlementDate).addTradeInstruction(tradeInstruction);
            else
                report.put(settlementDate, new TradeExecutionReport(tradeInstruction));
    }

    public void evaluateEntityRankings() {
        for (String settlementDate : report.keySet()) {
            TradeExecutionReport executionReport = report.get(settlementDate);
            executionReport.evaluateRanking(executionReport.getIncomingEntities());
            executionReport.evaluateRanking(executionReport.getOutgoingEntities());
        }
    }

    public HashMap<String, TradeExecutionReport> getReport() {
        return report;
    }
}
