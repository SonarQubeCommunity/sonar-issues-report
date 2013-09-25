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
package org.sonar.issuesreport.report;

import org.junit.Test;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class RuleReportComparatorTest {

  @Test
  public void testOrderOfRuleReport() {
    RuleReport oneNewMajorIssue = new RuleReport(new ReportRuleKey(Rule.create("foo", "bar"), RulePriority.MAJOR));
    oneNewMajorIssue.getTotal().incrementNewIssuesCount();

    RuleReport oneNewBlockerIssue = new RuleReport(new ReportRuleKey(Rule.create("foo", "bar"), RulePriority.BLOCKER));
    oneNewBlockerIssue.getTotal().incrementNewIssuesCount();

    RuleReport oneMajorIssue = new RuleReport(new ReportRuleKey(Rule.create("foo", "bar"), RulePriority.MAJOR));
    oneMajorIssue.getTotal().incrementCountInCurrentAnalysis();

    RuleReport oneMajorIssueAnotherRule = new RuleReport(new ReportRuleKey(Rule.create("foo", "baq"), RulePriority.MAJOR));
    oneMajorIssueAnotherRule.getTotal().incrementCountInCurrentAnalysis();

    RuleReport oneBlockerIssue = new RuleReport(new ReportRuleKey(Rule.create("foo", "bar"), RulePriority.BLOCKER));
    oneBlockerIssue.getTotal().incrementCountInCurrentAnalysis();

    RuleReport oneNewMajorIssueAnotherRule = new RuleReport(new ReportRuleKey(Rule.create("foo", "baq"), RulePriority.MAJOR));
    oneNewMajorIssueAnotherRule.getTotal().incrementNewIssuesCount();

    RuleReport twoNewMajorIssue = new RuleReport(new ReportRuleKey(Rule.create("foo", "bar"), RulePriority.MAJOR));
    twoNewMajorIssue.getTotal().incrementNewIssuesCount();
    twoNewMajorIssue.getTotal().incrementNewIssuesCount();

    List<RuleReport> reports = Arrays.asList(oneNewMajorIssue, oneNewBlockerIssue,
      oneMajorIssue, oneMajorIssueAnotherRule, oneBlockerIssue,
      oneNewMajorIssueAnotherRule,
      twoNewMajorIssue
      );
    Collections.sort(reports, new RuleReportComparator());

    assertThat(reports).containsSequence(
      // First highest new issue priority
      oneNewBlockerIssue,
      // Then for same severity the one with the most new issues
      twoNewMajorIssue,
      // Then for same severity and count order by rule key
      oneNewMajorIssueAnotherRule,
      oneNewMajorIssue,
      // Then for report without new issue compare by severity
      oneBlockerIssue,
      // Then rule key
      oneMajorIssueAnotherRule,
      oneMajorIssue
      );
  }
}
