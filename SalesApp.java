package sales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SalesApp {
    SalesReportDao salesReportDao = new SalesReportDao();
    SalesActivityReport report = new SalesActivityReport();
    EcmService ecmService = new EcmService();
    public void generateSalesActivityReport(String salesId, int maxRow, boolean isNatTrade, boolean isSupervisor) {

        SalesDao salesDao = new SalesDao();
        List<String> headers = null;

        List<SalesReportData> filteredReportDataList = new ArrayList<SalesReportData>();

        if (salesId == null) {
            return;
        }

        Sales sales = salesDao.getSalesBySalesId(salesId);

        if (isToday(sales)) return;

        List<SalesReportData> reportDataList = filterReportDataList(isSupervisor, salesReportDao, filteredReportDataList, sales);

        List<SalesReportData> filterRowReportDataList = getFilteredReportDataList(maxRow, reportDataList);

        updateEcmService(isNatTrade, filterRowReportDataList);

    }

    public boolean isToday(Sales sales) {
        Date today = new Date();
        if (today.after(sales.getEffectiveTo())
                || today.before(sales.getEffectiveFrom())) {
            return false;
        }
        return true;
    }

    public List<SalesReportData> filterReportDataList(boolean isSupervisor, SalesReportDao salesReportDao, List<SalesReportData> filteredReportDataList, Sales sales) {
        List<SalesReportData> reportDataList = salesReportDao.getReportData(sales);

        for (SalesReportData data : reportDataList) {
            if ("SalesActivity".equalsIgnoreCase(data.getType())) {
                if (data.isConfidential()) {
                    if (isSupervisor) {
                        filteredReportDataList.add(data);
                    }
                } else {
                    filteredReportDataList.add(data);
                }
            }
        }
        return reportDataList;
    }

    public List<SalesReportData> getFilteredReportDataList(int maxRow, List<SalesReportData> reportDataList) {
        List<SalesReportData> filteredReportDataList;
        List<SalesReportData> tempList = new ArrayList<SalesReportData>();
        for (int i = 0; i < reportDataList.size() && i < maxRow; i++) {
            tempList.add(reportDataList.get(i));
        }
        filteredReportDataList = tempList;
        return filteredReportDataList;
    }

    public void updateEcmService(boolean isNatTrade, List<SalesReportData> reportDataList) {
        List<String> headers;
        if (isNatTrade) {
            headers = Arrays.asList("Sales ID", "Sales Name", "Activity", "Time");
        } else {
            headers = Arrays.asList("Sales ID", "Sales Name", "Activity", "Local Time");
        }

        report = this.generateReport(headers, reportDataList);
        ecmService.uploadDocument(report.toXml());
    }

    public SalesActivityReport generateReport(List<String> headers, List<SalesReportData> reportDataList) {
        // TODO Auto-generated method stub
        return null;
    }

}
