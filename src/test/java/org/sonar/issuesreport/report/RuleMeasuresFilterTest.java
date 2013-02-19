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

import org.sonar.issuesreport.report.RuleMeasuresFilter;

import org.junit.Test;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.RuleMeasure;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class RuleMeasuresFilterTest {
  @Test
  public void filter() {
    RuleMeasuresFilter filter = new RuleMeasuresFilter();
    RuleMeasure severityViolations = RuleMeasure.createForPriority(CoreMetrics.VIOLATIONS, RulePriority.BLOCKER, 1.0);
    RuleMeasure ruleViolations = RuleMeasure.createForRule(CoreMetrics.VIOLATIONS, Rule.create("cobol", "one"), 20.0);
    RuleMeasure ruleBlockerViolations = RuleMeasure.createForRule(CoreMetrics.BLOCKER_VIOLATIONS, Rule.create("cobol", "one"), 15.0);
    RuleMeasure ruleCriticalViolations = RuleMeasure.createForRule(CoreMetrics.CRITICAL_VIOLATIONS, Rule.create("cobol", "one"), 5.0);
    List<Measure> measures = Arrays.asList(new Measure("ncloc"), severityViolations, ruleViolations, ruleBlockerViolations, ruleCriticalViolations);

    Collection<RuleMeasure> filtered = filter.filter(measures);

    assertThat(filtered).containsOnly(ruleBlockerViolations, ruleCriticalViolations);
  }
}
