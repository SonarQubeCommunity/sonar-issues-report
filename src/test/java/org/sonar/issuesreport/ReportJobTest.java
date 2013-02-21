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
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.issuesreport.report.IssuesReport;
import org.sonar.issuesreport.report.console.ConsolePrinter;
import org.sonar.issuesreport.report.html.HTMLPrinter;

import java.io.File;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ReportJobTest {

  @Test
  public void shouldBeDisabledByDefault() {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    HTMLPrinter htmlPrinter = mock(HTMLPrinter.class);
    ConsolePrinter consolePrinter = mock(ConsolePrinter.class);
    ReportJob job = new ReportJob(null, settings, htmlPrinter, consolePrinter);

    job.executeOn(mock(Project.class), mock(SensorContext.class));

    verify(htmlPrinter, never()).writeToFile(any(IssuesReport.class), any(File.class));
    verify(consolePrinter, never()).printConsoleReport(any(IssuesReport.class));
  }

  @Test
  public void shouldEnableHTMLReport() {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    settings.setProperty(IssuesReportConstants.HTML_REPORT_ENABLED_KEY, "true");
    HTMLPrinter htmlPrinter = mock(HTMLPrinter.class);
    ConsolePrinter consolePrinter = mock(ConsolePrinter.class);
    SonarIndex index = mock(SonarIndex.class);
    ReportJob job = new ReportJob(index, settings, htmlPrinter, consolePrinter);

    job.executeOn(mock(Project.class), mock(SensorContext.class));

    verify(htmlPrinter).writeToFile(any(IssuesReport.class));
    verify(consolePrinter, never()).printConsoleReport(any(IssuesReport.class));
  }

  @Test
  public void shouldEnableConsoleReport() {
    Settings settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    settings.setProperty(IssuesReportConstants.CONSOLE_REPORT_ENABLED_KEY, "true");
    HTMLPrinter htmlPrinter = mock(HTMLPrinter.class);
    ConsolePrinter consolePrinter = mock(ConsolePrinter.class);
    SonarIndex index = mock(SonarIndex.class);
    ReportJob job = new ReportJob(index, settings, htmlPrinter, consolePrinter);

    job.executeOn(mock(Project.class), mock(SensorContext.class));

    verify(htmlPrinter, never()).writeToFile(any(IssuesReport.class));
    verify(consolePrinter).printConsoleReport(any(IssuesReport.class));
  }

}
