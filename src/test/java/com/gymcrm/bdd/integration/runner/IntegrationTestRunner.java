package com.gymcrm.bdd.integration.runner;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/gymcrm/integration")
@ConfigurationParameter(
        key = "cucumber.glue",
        value = "com.gymcrm.bdd.integration"
)
@ConfigurationParameter(
        key = "cucumber.plugin",
        value = "pretty,html:target/cucumber-integration-reports/report.html"
)
@ConfigurationParameter(
        key = "cucumber.filter.tags",
        value = "@integration"
)
public class IntegrationTestRunner {
}