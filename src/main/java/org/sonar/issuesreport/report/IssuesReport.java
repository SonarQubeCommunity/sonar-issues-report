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
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.issuesreport.tree.ResourceNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class IssuesReport {

  public static final int TOO_MANY_ISSUES_THRESHOLD = 1000;
  private String title;
  private Date date;
  private final ReportSummary summary = new ReportSummary();
  private final Map<ResourceNode, ResourceReport> resourceReportsByResource = Maps.newLinkedHashMap();

  public IssuesReport() {
  }

  public ReportSummary getSummary() {
    return summary;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Map<ResourceNode, ResourceReport> getResourceReportsByResource() {
    return resourceReportsByResource;
  }

  public List<ResourceReport> getResourceReports() {
    return new ArrayList<ResourceReport>(resourceReportsByResource.values());
  }

  public List<ResourceNode> getResourcesWithReport() {
    return new ArrayList<ResourceNode>(resourceReportsByResource.keySet());
  }

  public void addIssueOnResource(ResourceNode resource, Issue issue, Rule rule, RulePriority severity) {
    addResource(resource);
    getSummary().addIssue(issue, rule, severity);
    resourceReportsByResource.get(resource).addIssue(issue, rule, RulePriority.valueOf(issue.severity()));
  }

  public void addResolvedIssueOnResource(ResourceNode resource, Issue issue, Rule rule, RulePriority severity) {
    addResource(resource);
    getSummary().addResolvedIssue(issue, rule, severity);
    resourceReportsByResource.get(resource).addResolvedIssue(issue, rule, RulePriority.valueOf(issue.severity()));
  }

  private void addResource(ResourceNode resource) {
    if (!resourceReportsByResource.containsKey(resource)) {
      resourceReportsByResource.put(resource, new ResourceReport(resource));
    }
  }

}
