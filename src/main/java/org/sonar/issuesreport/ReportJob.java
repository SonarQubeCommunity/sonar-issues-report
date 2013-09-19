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

import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.issuesreport.printer.ReportPrinter;
import org.sonar.issuesreport.report.IssuesReport;
import org.sonar.issuesreport.report.IssuesReportBuilder;

public class ReportJob implements PostJob {

  private IssuesReportBuilder builder;
  private ReportPrinter[] printers;

  public ReportJob(IssuesReportBuilder builder, ReportPrinter[] printers) {
    this.builder = builder;
    this.printers = printers;
  }

  public void executeOn(Project project, SensorContext context) {
    // For performance only initialize IssuesReport if there is on Printer enabled
    IssuesReport report = null;
    for (ReportPrinter printer : printers) {
      if (printer.isEnabled()) {
        if (report == null) {
          report = builder.buildReport(project);
        }
        printer.print(report);
      }
    }
  }

}
