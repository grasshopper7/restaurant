package ristorante;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;

@RunWith(Cucumber.class)
@CucumberOptions( tags = "@ToRun",  plugin = { "summary",
		"progress" }, strict = true, snippets = SnippetType.CAMELCASE/* , dryRun = true */)
public class CukeRunnerIT {}
