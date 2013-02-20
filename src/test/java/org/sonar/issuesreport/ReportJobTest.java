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
package org.sonar.issuesreport;

import org.sonar.issuesreport.report.IssuesReport;

import org.sonar.issuesreport.report.html.HTMLPrinter;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReportJobTest {

  @Test
  public void shouldBeDisabledByDefault() {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    HTMLPrinter printer = mock(HTMLPrinter.class);
    ReportJob job = new ReportJob(null, settings, mock(ModuleFileSystem.class), printer);

    job.executeOn(mock(Project.class), mock(SensorContext.class));

    verify(printer, never()).print(any(IssuesReport.class), any(File.class));
  }

  @Test
  public void shouldEnableHTMLReport() {
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    when(fs.workingDir()).thenReturn(new File("."));
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    settings.setProperty(IssuesReportConstants.HTML_REPORT_ENABLED_KEY, "true");
    HTMLPrinter printer = mock(HTMLPrinter.class);
    SonarIndex index = mock(SonarIndex.class);
    ReportJob job = new ReportJob(index, settings, fs, printer);

    job.executeOn(mock(Project.class), mock(SensorContext.class));

    verify(printer).print(any(IssuesReport.class), any(File.class));
  }

  @Test
  public void shouldPrintIntoDefaultReportFile() {
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    when(fs.workingDir()).thenReturn(new File("."));
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    HTMLPrinter printer = mock(HTMLPrinter.class);
    IssuesReport report = mock(IssuesReport.class);
    ReportJob job = new ReportJob(null, settings, fs, printer);

    File reportFile = job.printHTMLReport(report);

    assertThat(reportFile).isEqualTo(new File(".", "issues-report.html"));
  }

  @Test
  public void shouldConfigureReportLocation() throws IOException {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    IssuesReport report = mock(IssuesReport.class);
    File f = new File("target/path/to/report.html");
    settings.setProperty(IssuesReportConstants.HTML_REPORT_LOCATION_KEY, f.getCanonicalPath());
    HTMLPrinter printer = mock(HTMLPrinter.class);
    ReportJob job = new ReportJob(null, settings, mock(ModuleFileSystem.class), printer);

    File reportFile = job.printHTMLReport(report);

    assertThat(reportFile.getCanonicalFile()).isEqualTo(f.getCanonicalFile());
  }
}
