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
import org.sonar.issuesreport.report.Printer;
import org.sonar.issuesreport.report.Report;
import org.sonar.issuesreport.report.RuleNames;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ReportJobTest {

  @Test
  public void shouldPrintIntoDefaultReportFile() {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    Printer printer = mock(Printer.class);
    Report report = mock(Report.class);
    ReportJob job = new ReportJob(null, settings, mock(RuleNames.class));

    File reportFile = job.printReport(report, printer);

    assertThat(reportFile).isEqualTo(new File(".", "issuesreport.html"));
  }

  @Test
  public void shouldConfigureReportLocation() {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    Report report = mock(Report.class);
    settings.setProperty(IssuesReportConstants.REPORT_DIR_KEY, "target/path/to/report");
    ReportJob job = new ReportJob(null, settings, mock(RuleNames.class));
    Printer printer = mock(Printer.class);

    File reportFile = job.printReport(report, printer);

    assertThat(reportFile).isEqualTo(new File("target/path/to/report", "issuesreport.html"));
  }
}
