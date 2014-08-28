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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.issue.Issue;
import org.sonar.api.issue.ProjectIssues;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;
import org.sonar.issuesreport.tree.ResourceNode;
import org.sonar.issuesreport.tree.ResourceTree;

import javax.annotation.CheckForNull;

public class IssuesReportBuilder implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(IssuesReportBuilder.class);

  private final ProjectIssues moduleIssues;
  private final RuleFinder ruleFinder;
  private final ResourceTree resourceTree;

  public IssuesReportBuilder(ProjectIssues moduleIssues, RuleFinder ruleFinder, ResourceTree resourceTree) {
    this.moduleIssues = moduleIssues;
    this.ruleFinder = ruleFinder;
    this.resourceTree = resourceTree;
  }

  public IssuesReport buildReport(Project project) {
    IssuesReport issuesReport = new IssuesReport();
    issuesReport.setTitle(project.getName());
    issuesReport.setDate(project.getAnalysisDate());

    processIssues(issuesReport, moduleIssues.issues(), false);
    processIssues(issuesReport, moduleIssues.resolvedIssues(), true);

    return issuesReport;
  }

  private void processIssues(IssuesReport issuesReport, Iterable<Issue> issues, boolean resolved) {
    for (Issue issue : issues) {
      Rule rule = findRule(issue);
      RulePriority severity = RulePriority.valueOf(issue.severity());
      ResourceNode resource = resourceTree.getResource(issue.componentKey());
      if (!validate(issue, rule, resource)) {
        continue;
      }
      if (resolved) {
        issuesReport.addResolvedIssueOnResource(resource, issue, rule, severity);
      } else {
        issuesReport.addIssueOnResource(resource, issue, rule, severity);
      }
    }
  }

  private boolean validate(Issue issue, Rule rule, ResourceNode resource) {
    if (rule == null) {
      LOG.warn("Unknow rule for issue {}", issue);
      return false;
    }
    if (resource == null) {
      LOG.debug("Unknow resource with key {}", issue.componentKey());
      return false;
    }
    return true;
  }

  @CheckForNull
  private Rule findRule(Issue issue) {
    RuleKey ruleKey = issue.ruleKey();
    return ruleFinder.findByKey(ruleKey);
  }

}
