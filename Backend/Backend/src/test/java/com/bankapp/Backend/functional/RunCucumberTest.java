package com.bankapp.Backend.functional;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/com/bankapp/Backend/functional/features",
        glue = "com.bankapp.Backend.functional.steps",
        plugin = {"pretty", "summary", "html:target/cucumber-report.html"}
)
public class RunCucumberTest {

}
