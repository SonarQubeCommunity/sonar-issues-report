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

import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.issuesreport.report.IssuesReport;
import org.sonar.issuesreport.report.RuleStatus;
import org.sonar.issuesreport.report.html.HTMLPrinter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportJob implements PostJob {

  private static final Logger LOG = LoggerFactory.getLogger(ReportJob.class);

  private final SonarIndex index;
  private final Settings settings;
  private final ModuleFileSystem fs;
  private final HTMLPrinter htmlPrinter;

  private IssuesReport report;

  public ReportJob(SonarIndex index, Settings settings, ModuleFileSystem fs, HTMLPrinter htmlPrinter) {
    this.index = index;
    this.settings = settings;
    this.fs = fs;
    this.htmlPrinter = htmlPrinter;
  }

  public void executeOn(Project project, SensorContext context) {
    if (settings.getBoolean(IssuesReportConstants.HTML_REPORT_ENABLED_KEY)) {
      printHTMLReport(getIssuesReport(project));
    }
    if (settings.getBoolean(IssuesReportConstants.CONSOLE_REPORT_ENABLED_KEY)) {
      printConsoleReport(getIssuesReport(project));
    }
  }

  private IssuesReport getIssuesReport(Project project) {
    if (report == null) {
      report = new IssuesReport(project, project.getName(), index);
    }
    return report;
  }

  File printHTMLReport(IssuesReport report) {
    String reportFileStr = settings.getString(IssuesReportConstants.HTML_REPORT_LOCATION_KEY);
    File reportFile = new File(reportFileStr);
    if (!reportFile.isAbsolute()) {
      reportFile = new File(fs.workingDir(), reportFileStr);
    }
    File parentDir = reportFile.getParentFile();
    try {
      FileUtils.forceMkdir(parentDir);
    } catch (IOException e) {
      throw new IllegalStateException("Fail to create the directory " + parentDir, e);
    }
    LOG.debug("Generating HTML Report to: " + reportFile.getAbsolutePath());
    htmlPrinter.print(report, reportFile);
    LOG.info("HTML Report generated: " + reportFile.getAbsolutePath());
    return reportFile;
  }

  private void printConsoleReport(IssuesReport report) {
    int newViolations = 0;
    Map<RulePriority, AtomicInteger> variationBySeverity = Maps.newHashMap();
    for (RulePriority rulePriority : RulePriority.values()) {
      variationBySeverity.put(rulePriority, new AtomicInteger(0));
    }
    for (RuleStatus ruleStatus : report.getTotal().getRuleStatuses()) {
      variationBySeverity.get(ruleStatus.getSeverity()).addAndGet(ruleStatus.getVariation());
      newViolations += ruleStatus.getVariation();
    }
    LOG.info("-------------  Issues Report  -------------");
    if (newViolations > 0) {
      LOG.info(newViolations + " new violation" + (newViolations > 1 ? "s" : ""));
      printNewViolations(variationBySeverity, RulePriority.BLOCKER, "blocking");
      printNewViolations(variationBySeverity, RulePriority.CRITICAL, "critical");
      printNewViolations(variationBySeverity, RulePriority.MAJOR, "major");
      printNewViolations(variationBySeverity, RulePriority.MINOR, "minor");
      printNewViolations(variationBySeverity, RulePriority.INFO, "info");
    }
    else {
      LOG.info("No new violation");
    }
    LOG.info("-------------------------------------------");
  }

  private void printNewViolations(Map<RulePriority, AtomicInteger> variationBySeverity, RulePriority severity, String severityLabel) {
    int violationCount = variationBySeverity.get(severity).get();
    if (violationCount > 0) {
      LOG.info("+{} {} violation" + (violationCount > 1 ? "s" : ""), violationCount, severityLabel);
    }
  }

}
