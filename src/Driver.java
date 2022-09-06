import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class Driver {

    // TODO: CHANGE THESE ==============================================================================================
    private final static String chromeDriverPath = "[PATH TO THIS PROJECT]\\driver\\chrome-driver-105.exe";
    private final static String username = "[YOUR USER HERE]";
    private final static String password = "[YOUR PASSWORD HERE]";
    // TODO: CHANGE THESE ==============================================================================================

    private final static String loginButton_Xpath = "//*[@id=\"loginTable\"]/tbody/tr[5]/td[2]/input";
    private final static String studentEmploymentLink_Xpath = "//*[@id=\"app-links\"]/ul/li[9]/a";
    private final static String enterTimeWorkedLink_Xpath = "//*[@id=\"main\"]/div[6]/a";
    private final static String confirmTimeButton_Xpath = "//*[@id=\"timeSaveOrAddId\"]";
    private final static String cancelButton_Xpath = "//*[@id=\"addTimeWorkedForm\"]/div[6]/div[2]/button[2]";
    private final static String continueButton_Xpath = "//*[@id=\"continueId\"]";
    private final static String commentBox_Xpath = "//*[@id=\"comments\"]";

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        // Instantiate a ChromeDriver class.
        WebDriver driver = new ChromeDriver();

        // Maximize the browser
        driver.manage().window().maximize();

        // Launch Website
        driver.get("https://eservices.minnstate.edu/student-portal/secure/dashboard.do?campusid=071&tokenTicket=%2FihrQ4QFMipD59hq2iKURiDLV3MB");

        // Log into E-services
        WebElement user = driver.findElement(By.xpath("//*[@id=\"userName\"]"));
        WebElement pass = driver.findElement(By.xpath("//*[@id=\"password\"]"));
        user.sendKeys(username);
        pass.sendKeys(password);
        driver.findElement(By.xpath(loginButton_Xpath)).click();

        // Open Student Employment then Enter Hours Link
        driver.findElement(By.xpath(studentEmploymentLink_Xpath)).click();
        driver.findElement(By.xpath(enterTimeWorkedLink_Xpath)).click();

        /* ====== Date Selection ===== TODO: CHANGE THIS ARRAY FOR YOUR SPECIFIC SCHEDULE
         * E-services work weeks start from Wednesday to the next week's Tuesday
         *
         * Indexes of valid work days in order:
         * 0  - Wednesday
         * 1  - Thursday
         * 2  - Friday
         * 5  - Monday
         * 6  - Tuesday
         * 7  - Wednesday
         * 8  - Thursday
         * 9  - Friday
         * 12 - Monday
         * 13 - Tuesday
         *
         * */
        int[] daysWorked = { 0,1,2,5,6,7,8,9,12,13 };

        //
        /* ===== Hours Worked ===== TODO: CHANGE THIS ARRAY FOR YOUR SPECIFIC SCHEDULE
         * Total size of list -- NOT the total of combined hours -- must match the size of daysWorked list.
         *
         * Enter your hours to match the indexes of your days worked. Indexes of both arrays should match for script
         * to work properly.
         *
         * */
        int[] hoursWorked = { 7,2,2,7,2,7,2,2,7,2 };

        for (int i = 0; i < daysWorked.length; i++) {

            // Begin Adding Time before submitting
            driver.findElement(By.xpath("//*[@id=\"addTime\"]")).click();
            Select date = new Select(driver.findElement(By.xpath("//*[@id=\"date\"]")));
            date.selectByIndex(daysWorked[i]);

            // Always select Midnight first
            Select startTime = new Select(driver.findElement(By.xpath("//*[@id=\"startTime\"]")));
            startTime.selectByIndex(0);

            // Select correct endTime to match hours worked for this day
            Select endTime = new Select(driver.findElement(By.xpath("//*[@id=\"endTime\"]")));
            endTime.selectByVisibleText(String.format("%d:00 AM", hoursWorked[i]));

            // Add "Work" into comments
            WebElement comment = driver.findElement(By.xpath());
            comment.sendKeys("Work");

            // Add hours
            driver.findElement(By.xpath("//*[@id=\"timeSaveOrAddId\"]")).click();

            // --- Special cases --- //

            // Holiday hours
            if (driver.getPageSource().contains("Time has been entered on a holiday. Is that accurate?")){
                System.out.println("DAY IS A HOLIDAY, ADDING HOURS ANYWAY, DOUBLE CHECK BEFORE SUBMITTING");

                WebElement comment_ = driver.findElement(By.xpath(commentBox_Xpath));
                comment_.clear();
                comment_.sendKeys("Work, HOLIDAY - MANAGER PLEASE DOUBLE CHECK WITH STUDENT");

                driver.findElement(By.xpath()).click(continueButton_Xpath);
            }

            // Ten or more hours
            else if (driver.getPageSource().contains("Ten or more hours have been reported for this day.")) {
                System.out.println("OVER 10 HOURS ENTERED, ADDING HOURS ANYWAY");

                WebElement comment_ = driver.findElement(By.xpath(commentBox_Xpath));
                comment_.clear();
                comment_.sendKeys("Work, WORKING OVER 10 HOURS");

                driver.findElement(By.xpath(continueButton_Xpath)).click();
            }

            // Overlapping hours
            else if(driver.getPageSource().contains("overlaps with an existing time")){
                System.out.println("EXISTING TIME ALREADY ADDED, SKIPPING ADDING THIS DATE'S HOURS");
                driver.findElement(By.xpath(cancelButton_Xpath)).click();
            }
        }
//        // Wait 30 seconds to allow user to Certify and Submit the hours
//        driver.wait(15000);
//        driver.close();
    }
}
