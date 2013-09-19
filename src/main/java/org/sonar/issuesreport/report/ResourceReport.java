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

import com.google.common.collect.Maps;
import org.sonar.api.issue.Issue;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class ResourceReport {
  private final Resource resource;
  private final IssueVariation total = new IssueVariation();

  private List<Issue> issues = new ArrayList<Issue>();
  private Map<Integer, List<Issue>> issuesPerLine = Maps.newHashMap();
  private Map<Rule, AtomicInteger> issuesByRule = Maps.newHashMap();
  private Map<RulePriority, AtomicInteger> issuesBySeverity = Maps.newHashMap();

  private final List<String> sourceCode;

  public ResourceReport(Resource resource, List<String> sourceCode) {
    this.resource = resource;
    this.sourceCode = sourceCode;
  }

  public Resource getResource() {
    return resource;
  }

  public String getName() {
    return resource.getLongName();
  }

  public IssueVariation getTotal() {
    return total;
  }

  public List<Issue> getIssues() {
    return issues;
  }

  public Map<Integer, List<Issue>> getIssuesPerLine() {
    return issuesPerLine;
  }

  public List<Issue> getIssuesAtLine(int lineId) {
    if (issuesPerLine.containsKey(lineId)) {
      return issuesPerLine.get(lineId);
    }
    return Collections.emptyList();
  }

  public void addIssue(Issue issue, Rule rule, RulePriority severity) {
    issues.add(issue);
    int line = issue.line() != null ? issue.line() : 0;
    if (!issuesPerLine.containsKey(line)) {
      issuesPerLine.put(line, new ArrayList<Issue>());
    }
    issuesPerLine.get(line).add(issue);
    if (!issuesByRule.containsKey(rule)) {
      issuesByRule.put(rule, new AtomicInteger());
    }
    issuesByRule.get(rule).incrementAndGet();
    if (!issuesBySeverity.containsKey(severity)) {
      issuesBySeverity.put(severity, new AtomicInteger());
    }
    issuesBySeverity.get(severity).incrementAndGet();
  }

  public List<String> getSourceCode() {
    return sourceCode;
  }

  public boolean isDisplayableLine(Integer lineId) {
    if (lineId == null || lineId < 1) {
      return false;
    }
    return hasViolations(lineId - 2) || hasViolations(lineId - 1) || hasViolations(lineId) || hasViolations(lineId + 1) || hasViolations(lineId + 2);
  }

  private boolean hasViolations(Integer lineId) {
    List<Issue> issues = issuesPerLine.get(lineId);
    return issues != null && !issues.isEmpty();
  }

}
