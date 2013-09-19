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
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.BatchExtension;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.issue.Issue;
import org.sonar.api.issue.ModuleIssues;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RulePriority;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class IssuesReportBuilder implements BatchExtension {

  private static final Logger LOG = LoggerFactory.getLogger(IssuesReportBuilder.class);

  private SonarIndex index;
  private ModuleIssues moduleIssues;
  private RuleFinder ruleFinder;

  public IssuesReportBuilder(SonarIndex index, ModuleIssues moduleIssues, RuleFinder ruleFinder) {
    this.index = index;
    this.moduleIssues = moduleIssues;
    this.ruleFinder = ruleFinder;
  }

  public IssuesReport buildReport(Project project) {
    Map<String, Resource> resourcesByKey = Maps.newHashMap();
    Collection<Resource> resources = index.getResources();
    for (Resource resource : resources) {
      // Don't want to deal with libraries, methods and classes
      if (ResourceUtils.isPersistable(resource) && !ResourceUtils.isLibrary(resource)) {
        resourcesByKey.put(resource.getEffectiveKey(), resource);
      }
    }

    IssuesReport issuesReport = new IssuesReport();
    issuesReport.setTitle(project.getName());
    issuesReport.setDate(project.getAnalysisDate());

    processIssues(resourcesByKey, issuesReport, moduleIssues.issues(), false);
    processIssues(resourcesByKey, issuesReport, moduleIssues.resolvedIssues(), true);

    return issuesReport;
  }

  private void processIssues(Map<String, Resource> resourcesByKey, IssuesReport issuesReport, Iterable<Issue> issues, boolean resolved) {
    for (Issue issue : issues) {
      Rule rule = findRule(issue);
      if (rule == null) {
        LOG.warn("Unknow rule for issue {}", issue);
        continue;
      }
      RulePriority severity = RulePriority.valueOf(issue.severity());
      Resource resource = resourcesByKey.get(issue.componentKey());
      if (resource == null) {
        LOG.warn("Unknow resource with key {}", issue.componentKey());
        continue;
      }
      issuesReport.addResource(resource, getSourceCode(resource));
      if (resolved) {
        issuesReport.addResolvedIssueOnResource(resource, issue, rule, severity);
      } else {
        issuesReport.addIssueOnResource(resource, issue, rule, severity);
      }
    }
  }

  private List<String> getSourceCode(Resource resource) {
    String source = StringUtils.defaultString(index.getSource(resource));
    String escapedSource = StringEscapeUtils.escapeHtml(source);
    try {
      return IOUtils.readLines(new StringReader(escapedSource));
    } catch (IOException e) {
      LOG.warn("Unable to read source code of resource {}", resource, e);
      return Collections.emptyList();
    }
  }

  private Rule findRule(Issue issue) {
    RuleKey ruleKey = issue.ruleKey();
    Rule rule = ruleFinder.findByKey(ruleKey);
    return rule;
  }

}
