package sales;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SalesAppTest {
    @Mock
    SalesReportDao salesReportDao;
    @Mock
    SalesActivityReport salesActivityReport;
    @Spy
    EcmService spyEcmService;
    @InjectMocks
    SalesApp injectSalesApp = new SalesApp();

    //	@Test
//	public void testGenerateReport() {
//
//		SalesApp salesApp = new SalesApp();
//		salesApp.generateSalesActivityReport("DUMMY", 1000, false, false);
//	}
    @Test
    public void testIsToday() {
        //given
        SalesApp spySalesApp = spy(SalesApp.class);
        Sales spySales = spy(Sales.class);
        Date curDate = new Date();
        //when
        doReturn(new Date(curDate.getTime() + 24 * 60 * 60 * 1000)).when(spySales).getEffectiveTo();
        doReturn(new Date(curDate.getTime() - 24 * 60 * 60 * 1000)).when(spySales).getEffectiveFrom();
        boolean isToday = spySalesApp.isToday(spySales);
        //then
        Assert.assertTrue(isToday);
    }

    @Test
    public void testFilterReportDataList() {
        //given
        Sales spySales = spy(Sales.class);
        boolean isSupervisor = false;
        SalesReportData salesReportData = spy(SalesReportData.class);
        List<SalesReportData> salesReportDataList = Arrays.asList(salesReportData);
        ArrayList<SalesReportData> filteredReportDataList = new ArrayList<>();

        //when
        when(salesReportData.getType()).thenReturn("SalesActivity");
        when(salesReportData.isConfidential()).thenReturn(false);
        when(salesReportDao.getReportData(spySales)).thenReturn(salesReportDataList);
        injectSalesApp.filterReportDataList(isSupervisor, salesReportDao, filteredReportDataList, spySales);

        //then
        Assert.assertEquals(1, filteredReportDataList.size());
        Assert.assertEquals("SalesActivity", filteredReportDataList.get(0).getType());
    }

    @Test
    public void testGetFilteredReportDataList() {
        //given
        SalesApp spySalesApp = spy(SalesApp.class);
        int maxRow = 5;
        SalesReportData salesReportData = spy(SalesReportData.class);
        List<SalesReportData> reportDataList = Arrays.asList(
                salesReportData,
                salesReportData,
                salesReportData,
                salesReportData,
                salesReportData,
                salesReportData);
        //when

        List<SalesReportData> filterRowReportDataList = spySalesApp.getFilteredReportDataList(maxRow, reportDataList);
        //then
        Assert.assertEquals(5, filterRowReportDataList.size());
    }

    @Test
    public void testUpdateEcmService() {
        //given
        List<SalesReportData> salesReportDataList = new ArrayList<>();
        SalesApp salesApp = spy(injectSalesApp);
        //when
        doReturn(salesActivityReport).when(salesApp).generateReport(Arrays.asList("Sales ID", "Sales Name", "Activity", "Local Time"),salesReportDataList);
        when(salesActivityReport.toXml()).thenReturn("Hello");
        //then
        salesApp.updateEcmService(false,salesReportDataList);
        verify(spyEcmService,times(1)).uploadDocument("Hello");

    }
}
