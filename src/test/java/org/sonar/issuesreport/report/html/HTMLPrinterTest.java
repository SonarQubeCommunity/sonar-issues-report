/*
 * Sonar :: Issues Report :: Plugin
 * Copyright (C) 2013 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.issuesreport.report.html;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.RuleMeasure;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.api.violations.ViolationQuery;
import org.sonar.issuesreport.IssuesReportConstants;
import org.sonar.issuesreport.IssuesReportPlugin;
import org.sonar.issuesreport.report.IssuesReport;
import org.sonar.issuesreport.report.RuleNames;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class HTMLPrinterTest {

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Test
  public void shouldGenerateReport() throws IOException {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    RuleNames ruleNames = mock(RuleNames.class);
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    when(fs.sourceCharset()).thenReturn(Charsets.UTF_8);
    HTMLPrinter printer = new HTMLPrinter(ruleNames, fs, settings);
    Project project = mock(Project.class);
    when(project.getAnalysisDate()).thenReturn(new Date());
    IssuesReport report = new IssuesReport(project, "Title", mock(SonarIndex.class));
    File reportDir = temp.newFolder();
    File reportFile = new File(reportDir, "report.html");
    printer.writeToFile(report, reportFile);
    assertThat(reportFile).exists();
  }

  @Test
  public void shouldPrintIntoDefaultReportFile() {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    RuleNames ruleNames = mock(RuleNames.class);
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    when(fs.workingDir()).thenReturn(new File("."));
    HTMLPrinter printer = new HTMLPrinter(ruleNames, fs, settings);
    HTMLPrinter spy = spy(printer);
    doNothing().when(spy).writeToFile(any(IssuesReport.class), any(File.class));

    File reportFile = spy.writeToFile(mock(IssuesReport.class));

    assertThat(reportFile).isEqualTo(new File(".", "issues-report.html"));
  }

  @Test
  public void shouldConfigureReportLocation() throws IOException {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    File f = new File("target/path/to/report.html");
    settings.setProperty(IssuesReportConstants.HTML_REPORT_LOCATION_KEY, f.getCanonicalPath());

    RuleNames ruleNames = mock(RuleNames.class);
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    HTMLPrinter printer = new HTMLPrinter(ruleNames, fs, settings);
    HTMLPrinter spy = spy(printer);
    doNothing().when(spy).writeToFile(any(IssuesReport.class), any(File.class));

    File reportFile = spy.writeToFile(mock(IssuesReport.class));

    assertThat(reportFile.getCanonicalFile()).isEqualTo(f.getCanonicalFile());
  }

  @Test
  public void shouldGenerateReportWhenNewViolation() throws IOException {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    RuleNames ruleNames = mock(RuleNames.class);
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    when(fs.sourceCharset()).thenReturn(Charsets.UTF_8);
    HTMLPrinter printer = new HTMLPrinter(ruleNames, fs, settings);

    Project project = mock(Project.class);
    when(project.getAnalysisDate()).thenReturn(new Date());
    SonarIndex index = mock(SonarIndex.class);
    Resource file = mock(Resource.class);
    when(file.getScope()).thenReturn(Scopes.FILE);
    when(file.getName()).thenReturn("Foo.java");
    when(index.getVertices()).thenReturn(Sets.newHashSet(file));
    org.sonar.api.rules.Rule rule = org.sonar.api.rules.Rule.create("foo", "bar").setSeverity(RulePriority.BLOCKER);
    rule.setId(1);
    when(ruleNames.name(rule)).thenReturn("My Rule");
    RuleMeasure measure = RuleMeasure.createForRule(CoreMetrics.BLOCKER_VIOLATIONS, rule, 2.0).setSeverity(RulePriority.BLOCKER);
    when(index.getMeasures(Mockito.eq(file), any(MeasuresFilter.class))).thenReturn(Arrays.asList(measure));
    Violation violation = mock(Violation.class);
    when(violation.isNew()).thenReturn(true);
    when(violation.getRule()).thenReturn(rule);
    when(violation.getSeverity()).thenReturn(RulePriority.BLOCKER);
    when(index.getViolations(any(ViolationQuery.class))).thenReturn(Arrays.asList(violation));
    IssuesReport report = new IssuesReport(project, "Title", index);

    File reportDir = temp.newFolder();
    File reportFile = new File(reportDir, "report.html");
    printer.writeToFile(report, reportFile);
  }
}
