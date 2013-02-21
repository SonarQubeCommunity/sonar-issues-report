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
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.issuesreport.report.IssuesReport;
import org.sonar.issuesreport.report.console.ConsolePrinter;
import org.sonar.issuesreport.report.html.HTMLPrinter;

public class ReportJob implements PostJob {

  private final SonarIndex index;
  private final Settings settings;
  private final HTMLPrinter htmlPrinter;
  private final ConsolePrinter consolePrinter;

  private IssuesReport report;

  public ReportJob(SonarIndex index, Settings settings, HTMLPrinter htmlPrinter, ConsolePrinter consolePrinter) {
    this.index = index;
    this.settings = settings;
    this.htmlPrinter = htmlPrinter;
    this.consolePrinter = consolePrinter;
  }

  public void executeOn(Project project, SensorContext context) {
    if (settings.getBoolean(IssuesReportConstants.HTML_REPORT_ENABLED_KEY)) {
      htmlPrinter.writeToFile(getIssuesReport(project));
    }
    if (settings.getBoolean(IssuesReportConstants.CONSOLE_REPORT_ENABLED_KEY)) {
      consolePrinter.printConsoleReport(getIssuesReport(project));
    }
  }

  private IssuesReport getIssuesReport(Project project) {
    if (report == null) {
      report = new IssuesReport(project, project.getName(), index);
    }
    return report;
  }

}
