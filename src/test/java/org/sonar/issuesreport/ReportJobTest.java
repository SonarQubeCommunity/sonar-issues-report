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

import org.junit.Test;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.issuesreport.report.HTMLReport;
import org.sonar.issuesreport.report.Printer;
import org.sonar.issuesreport.report.RuleNames;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReportJobTest {

  @Test
  public void shouldPrintIntoDefaultReportFile() {
    ModuleFileSystem fs = mock(ModuleFileSystem.class);
    when(fs.workingDir()).thenReturn(new File("."));
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    Printer printer = mock(Printer.class);
    HTMLReport report = mock(HTMLReport.class);
    ReportJob job = new ReportJob(null, settings, mock(RuleNames.class), fs);

    File reportFile = job.printHTMLReport(report, printer);

    assertThat(reportFile).isEqualTo(new File(".", "issues-report.html"));
  }

  @Test
  public void shouldConfigureReportLocation() throws IOException {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    HTMLReport report = mock(HTMLReport.class);
    File f = new File("target/path/to/report.html");
    settings.setProperty(IssuesReportConstants.HTML_REPORT_LOCATION_KEY, f.getCanonicalPath());
    ReportJob job = new ReportJob(null, settings, mock(RuleNames.class), mock(ModuleFileSystem.class));
    Printer printer = mock(Printer.class);

    File reportFile = job.printHTMLReport(report, printer);

    assertThat(reportFile.getCanonicalFile()).isEqualTo(f.getCanonicalFile());
  }
}
