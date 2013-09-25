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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.issue.Issue;
import org.sonar.api.issue.ProjectIssues;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.issuesreport.IssuesReportFakeUtils;
import org.sonar.issuesreport.tree.ResourceNode;
import org.sonar.issuesreport.tree.ResourceTree;

import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IssuesReportBuilderTest {

  private ProjectIssues moduleIssues;
  private RuleFinder ruleFinder;
  private IssuesReportBuilder builder;
  private ResourceTree resourceTree;

  @Before
  public void prepare() {
    moduleIssues = mock(ProjectIssues.class);
    ruleFinder = mock(RuleFinder.class);
    resourceTree = mock(ResourceTree.class);
    builder = new IssuesReportBuilder(moduleIssues, ruleFinder, resourceTree);
  }

  @Test
  public void shouldNotFailWhenIssueOnUnknowResource() {
    Issue fakeIssue = IssuesReportFakeUtils.fakeIssue(false, RuleKey.of("foo", "bar"), "com.foo.Bar", null);
    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Collections.<Issue> emptyList());

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getResourceReports()).isEmpty();
  }

  @Test
  public void shouldNotFailWhenRuleNotFoundOnIssue() {
    ResourceNode fakeFile = IssuesReportFakeUtils.fakeFile("com.foo.Bar");
    when(resourceTree.getResource("com.foo.Bar")).thenReturn(fakeFile);

    Issue fakeIssue = IssuesReportFakeUtils.fakeIssue(false, RuleKey.of("foo", "bar"), "com.foo.Bar", null);

    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Collections.<Issue> emptyList());

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getResourceReports()).isEmpty();
  }

  @Test
  public void shouldGenerateReportWithOneViolation() {
    ResourceNode fakeFile = IssuesReportFakeUtils.fakeFile("project:com.foo.Bar");
    when(resourceTree.getResource("project:com.foo.Bar")).thenReturn(fakeFile);

    RuleKey ruleKey = RuleKey.of("foo", "bar");
    Issue fakeIssue = IssuesReportFakeUtils.fakeIssue(false, ruleKey, "project:com.foo.Bar", 4);

    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Collections.<Issue> emptyList());

    Rule fakeRule = IssuesReportFakeUtils.fakeRule(ruleKey);
    when(ruleFinder.findByKey(eq(ruleKey))).thenReturn(fakeRule);

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getSummary().getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(report.getSummary().getTotal().getNewIssuesCount()).isEqualTo(0);
    assertThat(report.getSummary().getTotal().getResolvedIssuesCount()).isEqualTo(0);
    assertThat(report.getResourceReports()).hasSize(1);
    ResourceReport resourceReport = report.getResourceReports().get(0);
    assertThat(resourceReport.getName()).isEqualTo("foo.bar.Foo");
    assertThat(resourceReport.getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(resourceReport.getTotal().getNewIssuesCount()).isEqualTo(0);
    assertThat(resourceReport.getTotal().getResolvedIssuesCount()).isEqualTo(0);

    assertThat(resourceReport.isDisplayableLine(1, false)).isEqualTo(false);
    assertThat(resourceReport.isDisplayableLine(2, false)).isEqualTo(false);
    assertThat(resourceReport.isDisplayableLine(2, true)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(3, false)).isEqualTo(false);
    assertThat(resourceReport.isDisplayableLine(3, true)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(4, false)).isEqualTo(false);
    assertThat(resourceReport.isDisplayableLine(4, true)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(5, false)).isEqualTo(false);
    assertThat(resourceReport.isDisplayableLine(5, true)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(6, false)).isEqualTo(false);
    assertThat(resourceReport.isDisplayableLine(6, true)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(7, false)).isEqualTo(false);
  }

  @Test
  public void shouldGenerateReportWithOneNewViolation() {
    ResourceNode fakeFile = IssuesReportFakeUtils.fakeFile("project:com.foo.Bar");
    when(resourceTree.getResource("project:com.foo.Bar")).thenReturn(fakeFile);

    RuleKey ruleKey = RuleKey.of("foo", "bar");
    Issue fakeIssue = IssuesReportFakeUtils.fakeIssue(true, ruleKey, "project:com.foo.Bar", 4);

    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Collections.<Issue> emptyList());

    Rule fakeRule = IssuesReportFakeUtils.fakeRule(ruleKey);
    when(ruleFinder.findByKey(eq(ruleKey))).thenReturn(fakeRule);

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getSummary().getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(report.getSummary().getTotal().getNewIssuesCount()).isEqualTo(1);
    assertThat(report.getSummary().getTotal().getResolvedIssuesCount()).isEqualTo(0);
    assertThat(report.getResourceReports()).hasSize(1);
    ResourceReport resourceReport = report.getResourceReports().get(0);
    assertThat(resourceReport.getName()).isEqualTo("foo.bar.Foo");
    assertThat(resourceReport.getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(resourceReport.getTotal().getNewIssuesCount()).isEqualTo(1);
    assertThat(resourceReport.getTotal().getResolvedIssuesCount()).isEqualTo(0);

    assertThat(resourceReport.isDisplayableLine(null, false)).isEqualTo(false);
    assertThat(resourceReport.isDisplayableLine(0, false)).isEqualTo(false);
    assertThat(resourceReport.isDisplayableLine(1, false)).isEqualTo(false);
    assertThat(resourceReport.isDisplayableLine(2, false)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(3, false)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(4, false)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(5, false)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(6, false)).isEqualTo(true);
    assertThat(resourceReport.isDisplayableLine(7, false)).isEqualTo(false);
  }

  @Test
  public void shouldGenerateReportWithOneNewViolationAndOneResolved() {
    ResourceNode fakeFile = IssuesReportFakeUtils.fakeFile("project:com.foo.Bar");
    when(resourceTree.getResource("project:com.foo.Bar")).thenReturn(fakeFile);

    RuleKey ruleKey = RuleKey.of("foo", "bar");
    Issue fakeNewIssue = IssuesReportFakeUtils.fakeIssue(true, ruleKey, "project:com.foo.Bar", null);
    Issue fakeResolvedIssue = IssuesReportFakeUtils.fakeIssue(false, ruleKey, "project:com.foo.Bar", null);

    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeNewIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Arrays.asList(fakeResolvedIssue));

    Rule fakeRule = IssuesReportFakeUtils.fakeRule(ruleKey);
    when(ruleFinder.findByKey(eq(ruleKey))).thenReturn(fakeRule);

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getSummary().getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(report.getSummary().getTotal().getNewIssuesCount()).isEqualTo(1);
    assertThat(report.getSummary().getTotal().getResolvedIssuesCount()).isEqualTo(1);
    assertThat(report.getResourceReports()).hasSize(1);
    assertThat(report.getResourceReports().get(0).getName()).isEqualTo("foo.bar.Foo");
    assertThat(report.getResourceReports().get(0).getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(report.getResourceReports().get(0).getTotal().getNewIssuesCount()).isEqualTo(1);
    assertThat(report.getResourceReports().get(0).getTotal().getResolvedIssuesCount()).isEqualTo(1);
  }

}
