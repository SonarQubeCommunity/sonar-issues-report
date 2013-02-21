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
package org.sonar.issuesreport.report.console;

import com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.rules.RulePriority;
import org.sonar.issuesreport.report.IssuesReport;
import org.sonar.issuesreport.report.RuleStatus;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsolePrinter implements BatchExtension {

  private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(ConsolePrinter.class);

  public void printConsoleReport(IssuesReport report) {
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
