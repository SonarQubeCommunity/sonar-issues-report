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
package org.sonar.issuesreport.printer.console;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.rules.RulePriority;
import org.sonar.issuesreport.IssuesReportConstants;
import org.sonar.issuesreport.printer.ReportPrinter;
import org.sonar.issuesreport.report.IssueVariation;
import org.sonar.issuesreport.report.IssuesReport;

public class ConsolePrinter implements ReportPrinter {

  public static class ConsoleLogger {
    private static final Logger LOG = LoggerFactory.getLogger(ConsolePrinter.class);

    public void log(String msg) {
      LOG.info(msg);
    }
  }

  private Settings settings;
  private ConsoleLogger logger;

  public ConsolePrinter(Settings settings) {
    this(settings, new ConsoleLogger());
  }

  @VisibleForTesting
  public ConsolePrinter(Settings settings, ConsoleLogger logger) {
    this.settings = settings;
    this.logger = logger;
  }

  @Override
  public boolean isEnabled() {
    return settings.getBoolean(IssuesReportConstants.CONSOLE_REPORT_ENABLED_KEY);
  }

  @Override
  public void print(IssuesReport report) {
    int newIssues = report.getSummary().getTotal().getNewIssuesCount();
    logger.log("-------------  Issues Report  -------------");
    if (newIssues > 0) {
      logger.log(newIssues + " new issue" + (newIssues > 1 ? "s" : ""));
      printNewIssues(report, RulePriority.BLOCKER, "blocking");
      printNewIssues(report, RulePriority.CRITICAL, "critical");
      printNewIssues(report, RulePriority.MAJOR, "major");
      printNewIssues(report, RulePriority.MINOR, "minor");
      printNewIssues(report, RulePriority.INFO, "info");
    } else {
      logger.log("No new issue");
    }
    logger.log("-------------------------------------------");
  }

  private void printNewIssues(IssuesReport report, RulePriority severity, String severityLabel) {
    IssueVariation issueVariation = report.getSummary().getTotalBySeverity().get(severity.toString());
    if (issueVariation != null) {
      int issueCount = issueVariation.getNewIssuesCount();
      if (issueCount > 0) {
        logger.log(String.format("+%s %s issue" + (issueCount > 1 ? "s" : ""), String.valueOf(issueCount), severityLabel));
      }
    }
  }
}
