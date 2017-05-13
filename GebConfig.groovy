@Grapes([
    @Grab("org.gebish:geb-core:1.1.1"),
    @Grab("com.codeborne:phantomjsdriver:1.4.2"),
    @Grab("org.seleniumhq.selenium:selenium-chrome-driver:3.4.0")
])

import org.openqa.selenium.chrome.ChromeDriver

System.setProperty("webdriver.chrome.driver", "./driver/chromedriver")

driver = {
   return new ChromeDriver()
}

waiting {
    timeout = 60
    retryInterval = 0.5
}

loginTwitterId = "****"
loginTwitterPassword = "****"
googleWebAplicationId = "****"
