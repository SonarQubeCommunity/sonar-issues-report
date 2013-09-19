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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.issuesreport.printer.ReportPrinter;
import org.sonar.issuesreport.report.IssuesReport;
import org.sonar.issuesreport.report.IssuesReportBuilder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReportJobTest {

  private IssuesReportBuilder issuesReportBuilder;
  private ReportPrinter printer1;
  private ReportPrinter printer2;
  private ReportJob job;

  @Before
  public void prepare() {
    issuesReportBuilder = mock(IssuesReportBuilder.class);
    when(issuesReportBuilder.buildReport(any(Project.class))).thenReturn(new IssuesReport());
    printer1 = mock(ReportPrinter.class);
    when(printer1.isEnabled()).thenReturn(false);
    printer2 = mock(ReportPrinter.class);
    when(printer2.isEnabled()).thenReturn(false);

    job = new ReportJob(issuesReportBuilder, new ReportPrinter[] {printer1, printer2});
  }

  @Test
  public void shouldNotBuildReportWhenNoPrinterEnabled() {
    when(printer1.isEnabled()).thenReturn(false);
    when(printer2.isEnabled()).thenReturn(false);

    job.executeOn(mock(Project.class), mock(SensorContext.class));

    verify(issuesReportBuilder, never()).buildReport(any(Project.class));
    verify(printer1, never()).print(any(IssuesReport.class));
    verify(printer2, never()).print(any(IssuesReport.class));
  }

  @Test
  public void shouldPrintOnlyOnEnabledPrinter() {
    when(printer1.isEnabled()).thenReturn(true);
    when(printer2.isEnabled()).thenReturn(false);

    job.executeOn(mock(Project.class), mock(SensorContext.class));

    verify(issuesReportBuilder, only()).buildReport(any(Project.class));
    verify(printer1, times(1)).print(any(IssuesReport.class));
    verify(printer2, never()).print(any(IssuesReport.class));
  }

  @Test
  public void shouldBuildReportOnlyOnceWhenTwoPrintersEnabled() {
    when(printer1.isEnabled()).thenReturn(true);
    when(printer2.isEnabled()).thenReturn(true);

    job.executeOn(mock(Project.class), mock(SensorContext.class));

    verify(issuesReportBuilder, only()).buildReport(any(Project.class));
    verify(printer1, times(1)).print(any(IssuesReport.class));
    verify(printer2, times(1)).print(any(IssuesReport.class));
  }

}
