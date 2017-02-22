package com.jpmc.trade.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpmc.trade.executor.domain.Entity;
import com.jpmc.trade.executor.domain.TradeExecutionReport;
import com.jpmc.trade.executor.domain.TradeInstruction;
import com.jpmc.trade.executor.service.TradeExecutionReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by Arikt on 22-02-2017.
 */
public class Executor {

    private static Log log = LogFactory.getLog(Executor.class);
    private static TradeExecutionReportService service = TradeExecutionReportService.getInstance();

    public static void main(String[] args) throws IOException {
        log.info("Starting to execute trades, report will be generated at the end of the process");
        log.info("Reading trade instructions");
        TradeInstruction[] tradeInstructions = readTradeInstructions("/trade-instructions.json");

        log.info("Executing trade instructions");
        executeTrades(tradeInstructions);
        log.info("Evaluating entity rankings");
        service.evaluateEntityRankings();
        log.info("Generating reports");
        generateReport();
    }

    private static void generateReport() {
        HashMap<String, TradeExecutionReport> report = service.getReport();
        for (String settlementDate : report.keySet()) {
            System.out.println();
            System.out.format("%25s%25s%25s", "Settlement Date", "Incoming Amount(USD)", "Outgoing Amount(USD)");
            System.out.println();
            System.out.format("%25s%25s%25s", "-------------------------", "-------------------------", "-------------------------");
            TradeExecutionReport executionReport = report.get(settlementDate);
            executionReport.getIncomingEntities();
            System.out.println();
            System.out.format("%25s%25s%25s", settlementDate, executionReport.getIncomingAmount(), executionReport.getOutgoingAmount());
            System.out.println();
            printReport(executionReport.getIncomingEntities(), "Incoming");
            printReport(executionReport.getOutgoingEntities(), "Outgoing");
        }
    }

    private static void executeTrades(TradeInstruction[] tradeInstructions) {
        for (TradeInstruction tradeInstruction : tradeInstructions) {
            service.addTradeInstructionToReport(tradeInstruction);
        }
    }

    private static void printReport(HashMap<String, Entity> entities, String direction) {
        int count = 0;
        for (String entityName : entities.keySet()) {
            Entity entity = entities.get(entityName);
            if (entity != null) {
                if (count == 0) {
                    System.out.println();
                    System.out.format("%25s%25s%25s", "Entity", "Ranking", direction + " Valuation");
                    System.out.println();
                    System.out.format("%25s%25s%25s", "--------", "--------", "------------------");
                    count++;
                }
                System.out.println();
                System.out.format("%25s%25s%25s", entity.getEntity(), entity.getRanking(), entity.getValuation());
            }
        }
        if(count > 0)
            System.out.println();
    }

    private static TradeInstruction[] readTradeInstructions(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TradeInstruction[] tradeInstructions = mapper.readValue(Executor.class.getResourceAsStream(path), TradeInstruction[].class);
        System.out.format("%15s%15s%15s%15s%20s%20s%15s%20s", "Entity", "Buy/Sell", "AgreedFx", "Currency", "InstructionDate", "SettlementDate", "Units", "Price per unit");
        System.out.println();
        System.out.format("%15s%15s%15s%15s%20s%20s%15s%20s", "---------------", "---------------", "---------------", "---------------", "--------------------", "--------------------", "---------------", "--------------------");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        for (TradeInstruction tradeInstruction : tradeInstructions) {
            System.out.println();
            System.out.format("%15s%15s%15s%15s%20s%20s%15s%20s", tradeInstruction.getEntity(), tradeInstruction.getBsIndicator() + "",
                    tradeInstruction.getAgreedFx() + "", tradeInstruction.getCurrency(), dateFormat.format(tradeInstruction.getInstructionDate()),
                    dateFormat.format(tradeInstruction.getSettlementDate()), tradeInstruction.getUnits() + "", tradeInstruction.getPerUnitPrice() + "");
        }
        System.out.println();
        return tradeInstructions;
    }
}
