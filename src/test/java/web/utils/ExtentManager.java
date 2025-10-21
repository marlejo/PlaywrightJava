package web.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {

    private static ExtentReports extent;
    private static ExtentSparkReporter sparkReporter;

    public static ExtentReports getInstance(){
        try {
            if (extent == null){
                sparkReporter = new ExtentSparkReporter("test-output/ExtentReport.html");
                extent = new ExtentReports();
                extent.attachReporter(sparkReporter);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize ExtentReports", e);
        }
        return extent;
    }
}
