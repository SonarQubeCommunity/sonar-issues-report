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
package org.sonar.issuesreport.printer.html;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.issuesreport.IssuesReportConstants;
import org.sonar.issuesreport.IssuesReportFakeUtils;
import org.sonar.issuesreport.IssuesReportPlugin;
import org.sonar.issuesreport.report.IssuesReport;
import org.sonar.issuesreport.report.RuleNames;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class HTMLPrinterTest {

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();
  private Settings settings;
  private HtmlPrinter htmlPrinter;

  @Before
  public void prepare() {
    settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    htmlPrinter = new HtmlPrinter(mock(RuleNames.class), mock(ModuleFileSystem.class), settings);
  }

  @Test
  public void shouldBeDisabledByDefault() {
    assertThat(htmlPrinter.isEnabled()).isFalse();
  }

  @Test
  public void shouldEnableHTMLReport() {
    settings.setProperty(IssuesReportConstants.HTML_REPORT_ENABLED_KEY, "true");
    assertThat(htmlPrinter.isEnabled()).isTrue();
  }

  @Test
  public void shouldGenerateEmptyReport() throws IOException {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    RuleNames ruleNames = mock(RuleNames.class);
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    when(fs.sourceCharset()).thenReturn(Charsets.UTF_8);
    HtmlPrinter printer = new HtmlPrinter(ruleNames, fs, settings);
    Project project = mock(Project.class);
    when(project.getAnalysisDate()).thenReturn(new Date());
    IssuesReport report = new IssuesReport();
    report.setTitle("Fake report");
    report.setDate(new Date());
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
    HtmlPrinter printer = new HtmlPrinter(ruleNames, fs, settings);
    HtmlPrinter spy = spy(printer);
    doNothing().when(spy).writeToFile(any(IssuesReport.class), any(File.class));

    spy.print(mock(IssuesReport.class));

    verify(spy).writeToFile(any(IssuesReport.class), eq(new File(".", "issues-report.html")));
  }

  @Test
  public void shouldConfigureReportLocation() throws IOException {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    File reportFile = new File("target/path/to/report.html");
    settings.setProperty(IssuesReportConstants.HTML_REPORT_LOCATION_KEY, reportFile.getAbsolutePath());

    RuleNames ruleNames = mock(RuleNames.class);
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    HtmlPrinter printer = new HtmlPrinter(ruleNames, fs, settings);
    HtmlPrinter spy = spy(printer);
    doNothing().when(spy).writeToFile(any(IssuesReport.class), any(File.class));

    spy.print(mock(IssuesReport.class));

    verify(spy).writeToFile(any(IssuesReport.class), eq(reportFile.getAbsoluteFile()));
  }

  @Test
  public void shouldGenerateReportWithNewViolation() throws IOException {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    File reportDir = temp.newFolder();
    File reportFile = new File(reportDir, "report.html");
    settings.setProperty(IssuesReportConstants.HTML_REPORT_LOCATION_KEY, reportFile.getAbsolutePath());

    RuleNames ruleNames = mock(RuleNames.class);
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    when(fs.sourceCharset()).thenReturn(Charsets.UTF_8);

    HtmlPrinter printer = new HtmlPrinter(ruleNames, fs, settings);

    Project project = mock(Project.class);
    when(project.getAnalysisDate()).thenReturn(new Date());
    Resource file = IssuesReportFakeUtils.fakeFile("com.foo.Bar");

    SonarIndex index = mock(SonarIndex.class);
    when(index.getVertices()).thenReturn(Sets.newHashSet(file));

    when(ruleNames.name(eq(RuleKey.of("foo", "bar")))).thenReturn("My Rule");
    when(ruleNames.name(any(org.sonar.api.rules.Rule.class))).thenReturn("My Rule");
    when(ruleNames.name("foo:bar")).thenReturn("My Rule");

    IssuesReport report = IssuesReportFakeUtils.sampleReportWith2Issues(file);

    printer.print(report);

    assertThat(reportFile).exists();
  }
}
