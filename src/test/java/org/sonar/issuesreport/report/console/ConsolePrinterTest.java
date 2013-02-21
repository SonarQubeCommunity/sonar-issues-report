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

import com.google.common.collect.Sets;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.RuleMeasure;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;
import org.sonar.api.violations.ViolationQuery;
import org.sonar.issuesreport.report.IssuesReport;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConsolePrinterTest {

  @Test
  public void shouldGenerateReportWhenNoViolation() throws IOException {
    ConsolePrinter printer = new ConsolePrinter();
    Project project = mock(Project.class);
    when(project.getAnalysisDate()).thenReturn(new Date());
    IssuesReport report = new IssuesReport(project, "Title", mock(SonarIndex.class));
    printer.printConsoleReport(report);
  }

  @Test
  public void shouldGenerateReportWhenNewViolation() throws IOException {
    ConsolePrinter printer = new ConsolePrinter();
    Project project = mock(Project.class);
    when(project.getAnalysisDate()).thenReturn(new Date());
    SonarIndex index = mock(SonarIndex.class);
    Resource file = mock(Resource.class);
    when(file.getScope()).thenReturn(Scopes.FILE);
    when(index.getVertices()).thenReturn(Sets.newHashSet(file));
    Rule rule = Rule.create("foo", "bar").setSeverity(RulePriority.BLOCKER);
    RuleMeasure measure = RuleMeasure.createForRule(CoreMetrics.BLOCKER_VIOLATIONS, rule, 2.0).setSeverity(RulePriority.BLOCKER);
    when(index.getMeasures(Mockito.eq(file), any(MeasuresFilter.class))).thenReturn(Arrays.asList(measure));
    Violation violation = mock(Violation.class);
    when(violation.isNew()).thenReturn(true);
    when(violation.getRule()).thenReturn(rule);
    when(index.getViolations(any(ViolationQuery.class))).thenReturn(Arrays.asList(violation));
    IssuesReport report = new IssuesReport(project, "Title", index);
    printer.printConsoleReport(report);
  }
}
