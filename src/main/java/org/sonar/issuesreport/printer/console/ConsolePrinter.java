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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.rules.RulePriority;
import org.sonar.issuesreport.IssuesReportPlugin;
import org.sonar.issuesreport.printer.ReportPrinter;
import org.sonar.issuesreport.report.IssueVariation;
import org.sonar.issuesreport.report.IssuesReport;

public class ConsolePrinter implements ReportPrinter {
  private static final int LEFT_PAD = 10;

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
    return settings.getBoolean(IssuesReportPlugin.CONSOLE_REPORT_ENABLED_KEY);
  }

  @Override
  public void print(IssuesReport report) {
    StringBuilder sb = new StringBuilder();

    int newIssues = report.getSummary().getTotal().getNewIssuesCount();
    sb.append("\n\n-------------  Issues Report  -------------\n\n");
    if (newIssues > 0) {
      sb.append(StringUtils.leftPad("+" + newIssues, LEFT_PAD)).append(" issue" + (newIssues > 1 ? "s" : "")).append("\n\n");
      printNewIssues(sb, report, RulePriority.BLOCKER, "blocking");
      printNewIssues(sb, report, RulePriority.CRITICAL, "critical");
      printNewIssues(sb, report, RulePriority.MAJOR, "major");
      printNewIssues(sb, report, RulePriority.MINOR, "minor");
      printNewIssues(sb, report, RulePriority.INFO, "info");
    } else {
      sb.append("  No new issue").append("\n");
    }
    sb.append("\n-------------------------------------------\n\n");

    logger.log(sb.toString());
  }

  private void printNewIssues(StringBuilder sb, IssuesReport report, RulePriority severity, String severityLabel) {
    IssueVariation issueVariation = report.getSummary().getTotalBySeverity().get(severity.toString());
    if (issueVariation != null) {
      int issueCount = issueVariation.getNewIssuesCount();
      if (issueCount > 0) {
        sb.append(StringUtils.leftPad("+" + issueCount, LEFT_PAD)).append(" ").append(severityLabel).append("\n");
      }
    }
  }
}
