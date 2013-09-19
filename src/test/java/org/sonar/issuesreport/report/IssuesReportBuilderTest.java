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
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.issue.Issue;
import org.sonar.api.issue.ModuleIssues;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.issuesreport.IssuesReportFakeUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IssuesReportBuilderTest {

  private ModuleIssues moduleIssues;
  private SonarIndex sonarIndex;
  private RuleFinder ruleFinder;
  private IssuesReportBuilder builder;

  @Before
  public void prepare() {
    moduleIssues = mock(ModuleIssues.class);
    sonarIndex = mock(SonarIndex.class);
    ruleFinder = mock(RuleFinder.class);
    builder = new IssuesReportBuilder(sonarIndex, moduleIssues, ruleFinder);
  }

  @Test
  public void shouldNotFailWhenIssueOnUnknowResource() {
    Issue fakeIssue = IssuesReportFakeUtils.fakeIssue(false, RuleKey.of("foo", "bar"), "com.foo.Bar");
    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Collections.<Issue> emptyList());

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getResourceReports()).isEmpty();
  }

  @Test
  public void shouldNotFailWhenRuleNotFoundOnIssue() {
    Resource fakeFile = IssuesReportFakeUtils.fakeFile("com.foo.Bar");
    when(sonarIndex.getResources()).thenReturn(new HashSet<Resource>(Arrays.asList(fakeFile)));

    Issue fakeIssue = IssuesReportFakeUtils.fakeIssue(false, RuleKey.of("foo", "bar"), "com.foo.Bar");

    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Collections.<Issue> emptyList());

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getResourceReports()).isEmpty();
  }

  @Test
  public void shouldGenerateReportWithOneViolation() {
    Resource fakeFile = IssuesReportFakeUtils.fakeFile("project:com.foo.Bar");
    when(sonarIndex.getResources()).thenReturn(new HashSet<Resource>(Arrays.asList(fakeFile)));

    RuleKey ruleKey = RuleKey.of("foo", "bar");
    Issue fakeIssue = IssuesReportFakeUtils.fakeIssue(false, ruleKey, "project:com.foo.Bar");

    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Collections.<Issue> emptyList());

    Rule fakeRule = IssuesReportFakeUtils.fakeRule(ruleKey);
    when(ruleFinder.findByKey(eq(ruleKey))).thenReturn(fakeRule);

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getSummary().getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(report.getSummary().getTotal().getNewIssuesCount()).isEqualTo(0);
    assertThat(report.getSummary().getTotal().getResolvedIssuesCount()).isEqualTo(0);
    assertThat(report.getResourceReports()).hasSize(1);
    assertThat(report.getResourceReports().get(0).getName()).isEqualTo("foo.bar.Foo");
    assertThat(report.getResourceReports().get(0).getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(report.getResourceReports().get(0).getTotal().getNewIssuesCount()).isEqualTo(0);
    assertThat(report.getResourceReports().get(0).getTotal().getResolvedIssuesCount()).isEqualTo(0);
  }

  @Test
  public void shouldGenerateReportWithOneNewViolation() {
    Resource fakeFile = IssuesReportFakeUtils.fakeFile("project:com.foo.Bar");
    when(sonarIndex.getResources()).thenReturn(new HashSet<Resource>(Arrays.asList(fakeFile)));

    RuleKey ruleKey = RuleKey.of("foo", "bar");
    Issue fakeIssue = IssuesReportFakeUtils.fakeIssue(true, ruleKey, "project:com.foo.Bar");

    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Collections.<Issue> emptyList());

    Rule fakeRule = IssuesReportFakeUtils.fakeRule(ruleKey);
    when(ruleFinder.findByKey(eq(ruleKey))).thenReturn(fakeRule);

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getSummary().getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(report.getSummary().getTotal().getNewIssuesCount()).isEqualTo(1);
    assertThat(report.getSummary().getTotal().getResolvedIssuesCount()).isEqualTo(0);
    assertThat(report.getResourceReports()).hasSize(1);
    assertThat(report.getResourceReports().get(0).getName()).isEqualTo("foo.bar.Foo");
    assertThat(report.getResourceReports().get(0).getTotal().getCountInCurrentAnalysis()).isEqualTo(1);
    assertThat(report.getResourceReports().get(0).getTotal().getNewIssuesCount()).isEqualTo(1);
    assertThat(report.getResourceReports().get(0).getTotal().getResolvedIssuesCount()).isEqualTo(0);
  }

  @Test
  public void shouldGenerateReportWithOneNewViolationAndOneResolved() {
    Resource fakeFile = IssuesReportFakeUtils.fakeFile("project:com.foo.Bar");
    when(sonarIndex.getResources()).thenReturn(new HashSet<Resource>(Arrays.asList(fakeFile)));

    RuleKey ruleKey = RuleKey.of("foo", "bar");
    Issue fakeNewIssue = IssuesReportFakeUtils.fakeIssue(true, ruleKey, "project:com.foo.Bar");
    Issue fakeResolvedIssue = IssuesReportFakeUtils.fakeIssue(false, ruleKey, "project:com.foo.Bar");

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

  // SONARPLUGINS-2710
  @Test
  public void shouldEscapeHtmlInSourceCode() {
    Resource fakeFile = IssuesReportFakeUtils.fakeFile("project:com.foo.Bar");
    when(sonarIndex.getResources()).thenReturn(new HashSet<Resource>(Arrays.asList(fakeFile)));
    when(sonarIndex.getSource(fakeFile)).thenReturn("some\n&nbsp; <p> html");

    RuleKey ruleKey = RuleKey.of("foo", "bar");
    Issue fakeIssue = IssuesReportFakeUtils.fakeIssue(false, ruleKey, "project:com.foo.Bar");

    when(moduleIssues.issues()).thenReturn(Arrays.asList(fakeIssue));
    when(moduleIssues.resolvedIssues()).thenReturn(Collections.<Issue> emptyList());

    Rule fakeRule = IssuesReportFakeUtils.fakeRule(ruleKey);
    when(ruleFinder.findByKey(eq(ruleKey))).thenReturn(fakeRule);

    IssuesReport report = builder.buildReport(mock(Project.class));
    assertThat(report.getResourceReports().get(0).getSourceCode()).containsExactly("some", "&amp;nbsp; &lt;p&gt; html");
  }
}
