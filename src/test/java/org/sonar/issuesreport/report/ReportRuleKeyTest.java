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
import org.sonar.api.rules.RulePriority;

import static org.fest.assertions.Assertions.assertThat;

public class ReportRuleKeyTest {

  @Test
  public void testEquals() {
    ReportRuleKey reportRuleKey = new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "bar"), RulePriority.BLOCKER);
    assertThat(reportRuleKey).isNotEqualTo("another object");
    assertThat(reportRuleKey).isEqualTo(reportRuleKey);
    assertThat(reportRuleKey).isEqualTo(new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "bar"), RulePriority.BLOCKER));
    assertThat(reportRuleKey).isNotEqualTo(new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "bar"), RulePriority.MAJOR));
    assertThat(reportRuleKey).isNotEqualTo(new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "bar2"), RulePriority.BLOCKER));
  }

  @Test
  public void testCompare() {
    ReportRuleKey reportRuleKey = new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "bar"), RulePriority.MAJOR);

    ReportRuleKey other = new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "bar"), RulePriority.MAJOR);
    assertThat(reportRuleKey.compareTo(other)).isEqualTo(0);

    other = new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "bar"), RulePriority.MINOR);
    assertThat(reportRuleKey.compareTo(other)).isLessThan(0);

    other = new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "bar"), RulePriority.BLOCKER);
    assertThat(reportRuleKey.compareTo(other)).isGreaterThan(0);

    other = new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "baq"), RulePriority.MAJOR);
    assertThat(reportRuleKey.compareTo(other)).isGreaterThan(0);

    other = new ReportRuleKey(org.sonar.api.rules.Rule.create("foo", "bas"), RulePriority.MAJOR);
    assertThat(reportRuleKey.compareTo(other)).isLessThan(0);
  }

}
