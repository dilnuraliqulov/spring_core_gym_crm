package com.gymcrm.bdd.component;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/gymcrm/component")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.gymcrm.bdd.component,com.gymcrm.bdd.common"
)
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@component")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty,summary,html:target/cucumber/component.html,json:target/cucumber/component.json"
)
public class GymCrmComponentCucumberTest {
}