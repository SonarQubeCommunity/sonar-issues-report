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

import org.sonar.api.issue.Issue;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.issuesreport.report.IssuesReport;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IssuesReportFakeUtils {

  public static IssuesReport sampleReportWith2Issues(Resource file) {
    Rule rule = fakeRule(RuleKey.of("foo", "bar"));

    Issue issue1 = fakeIssue(true, RuleKey.of("foo", "bar"), "com.foo.Bar");
    Issue issue2 = fakeIssue(false, RuleKey.of("foo", "bar"), "com.foo.Bar");

    IssuesReport report = new IssuesReport();
    report.setTitle("Fake report");
    report.setDate(new Date());
    report.addResource(file, Arrays.asList("foo", "bar"));
    report.addIssueOnResource(file, issue1, rule, RulePriority.BLOCKER);
    report.addIssueOnResource(file, issue2, rule, RulePriority.BLOCKER);

    return report;
  }

  public static Issue fakeIssue(boolean isNew, RuleKey ruleKey, String componentKey) {
    Issue issue = mock(Issue.class);
    when(issue.isNew()).thenReturn(isNew);
    when(issue.line()).thenReturn(1);
    when(issue.ruleKey()).thenReturn(ruleKey);
    when(issue.severity()).thenReturn("BLOCKER");
    when(issue.creationDate()).thenReturn(new Date());
    when(issue.componentKey()).thenReturn(componentKey);
    return issue;
  }

  public static Rule fakeRule(RuleKey ruleKey) {
    Rule rule = Rule.create(ruleKey.repository(), ruleKey.rule());
    return rule;
  }

  public static Resource fakeFile(String effectiveKey) {
    Resource file = mock(Resource.class);
    when(file.getScope()).thenReturn(Scopes.FILE);
    when(file.getName()).thenReturn("Foo");
    when(file.getLongName()).thenReturn("foo.bar.Foo");
    when(file.getEffectiveKey()).thenReturn(effectiveKey);
    return file;
  }

}
