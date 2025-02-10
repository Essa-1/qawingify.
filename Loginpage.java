package assignment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class Loginpage {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));  // Increased timeout
        driver.get("https://sakshingp.github.io/assignment/login.html");
    }

    @Test(priority = 1)
    public void testLogin() {
        // Perform login
        driver.findElement(By.id("username")).sendKeys("testuser");
        driver.findElement(By.id("password")).sendKeys("testpass");
        driver.findElement(By.id("log-in")).click();
        
        // Verify login successful by checking presence of transaction table
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("transactionsTable")));
        Assert.assertTrue(driver.findElement(By.id("transactionsTable")).isDisplayed(), "Login failed");
    }

    @Test(priority = 2, dependsOnMethods = "testLogin")
    public void testAmountSorting() {
        // Click the AMOUNT header to sort
        WebElement amountHeader = driver.findElement(By.id("amount"));
        amountHeader.click();
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='transactionsTable']//td[5]")));

        // Fetch all transaction amounts after sorting
        List<WebElement> amountElements = driver.findElements(By.xpath("//table[@id='transactionsTable']//td[5]"));

        // Extract amounts as a list of doubles, ensuring only valid numeric values are processed
        List<Double> actualAmounts = amountElements.stream()
                .map(e -> e.getText().trim().replace("$", "").replace(",", "")) // Trim whitespace, remove "$" and ","
                .filter(text -> !text.isEmpty() && text.matches("-?\\d+(\\.\\d+)?")) // Ensure valid numeric format
                .map(Double::parseDouble) // Convert to Double
                .collect(Collectors.toList());

        // Copy the actual list and sort it to compare
        List<Double> sortedAmounts = actualAmounts.stream().sorted().collect(Collectors.toList());

        // Validate sorting
        Assert.assertEquals(actualAmounts, sortedAmounts, "Transactions are NOT sorted correctly.");
    }

    }

