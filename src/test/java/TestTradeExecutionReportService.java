/**
 * Created by AA on 22-02-2017.
 */

import com.jpmc.trade.executor.Executor;
import com.jpmc.trade.executor.domain.Entity;
import com.jpmc.trade.executor.domain.TradeExecutionReport;
import com.jpmc.trade.executor.service.TradeExecutionReportService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

public class TestTradeExecutionReportService {

    private static TradeExecutionReportService service = TradeExecutionReportService.getInstance();
    private static HashMap<String, TradeExecutionReport> report = null;

    @BeforeClass
    public static void setUp() throws IOException {
        Executor.main(new String[0]);
        report = service.getReport();
    }

    @Test
    public void testSettlementDateAndValues() {
        TradeExecutionReport execReport = report.get("04 Jan 2016");
        Assert.assertTrue(execReport.getOutgoingAmount() == 20050.0);
        Assert.assertTrue(execReport.getIncomingAmount() == 0);
        HashMap<String, Entity> incomingEntities = execReport.getIncomingEntities();
        Assert.assertTrue(incomingEntities.size() == 0);
        HashMap<String, Entity> outgoingEntitites = execReport.getOutgoingEntities();
        Assert.assertTrue(outgoingEntitites.size() == 2);
    }

    @Test
    public void testRanking() {
        TradeExecutionReport execReport = report.get("04 Jan 2016");
        for(String entity : execReport.getOutgoingEntities().keySet()){
            Assert.assertTrue(execReport.getOutgoingEntities().get(entity).getRanking() == 1);
        }
    }

    @Test
    public void testValues() {
        TradeExecutionReport execReport = report.get("07 Jan 2016");
        Assert.assertTrue(execReport.getOutgoingAmount() == 298177.1724);
        Assert.assertTrue(execReport.getIncomingAmount() == 27274.5 );
        HashMap<String, Entity> incomingEntities = execReport.getIncomingEntities();
        Assert.assertTrue(incomingEntities.size() == 2);
        HashMap<String, Entity> outgoingEntitites = execReport.getOutgoingEntities();
        Assert.assertTrue(outgoingEntitites.size() == 4);
    }

    @Test
    public void testNull() {
        TradeExecutionReport execReport = report.get(null);
        Assert.assertTrue(execReport == null);
    }
}
