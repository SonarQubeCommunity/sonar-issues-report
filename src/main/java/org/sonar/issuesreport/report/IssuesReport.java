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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class IssuesReport {

  private final Project project;
  private final String title;
  private final SortedSet<ResourceStatus> fileStatuses;
  private final ResourceStatus total;

  public IssuesReport(Project project, String title, SonarIndex index) {
    this(project, title, index, getFiles(index));
  }

  IssuesReport(Project project, String title, SonarIndex index, Collection<Resource> resources) {
    this.project = project;
    this.title = title;
    this.fileStatuses = new TreeSet<ResourceStatus>();
    for (Resource resource : resources) {
      ResourceStatus newResourceStatus = newResourceStatus(index, resource);
      if (newResourceStatus.getValue() > 0) {
        fileStatuses.add(newResourceStatus);
      }
    }
    total = newTotalStatus(fileStatuses);
  }

  private static List<Resource> getFiles(SonarIndex index) {
    List<Resource> files = Lists.newArrayList();
    for (Resource resource : index.getResources()) {
      if (Scopes.isFile(resource)) {
        files.add(resource);
      }
    }
    return files;
  }

  public Set<ResourceStatus> getFileStatuses() {
    return fileStatuses;
  }

  public List<ResourceStatus> getStatuses() {
    List<ResourceStatus> result = Lists.newArrayList();
    result.add(total);
    if (fileStatuses.size() > 1) {
      result.addAll(fileStatuses);
    }
    return result;
  }

  public Project getProject() {
    return project;
  }

  public Date getDate() {
    return project.getAnalysisDate();
  }

  public String getTitle() {
    return title;
  }

  public ResourceStatus getTotal() {
    return total;
  }

  public SortedSet<Rule> getRules() {
    // This method is executed to create the select-box of rule filtering.
    // Rules are sorted by name
    SortedSet<Rule> rules = Sets.newTreeSet(new RuleComparatorByName());
    for (ResourceStatus resourceStatus : fileStatuses) {
      for (Violation violation : resourceStatus.getViolations()) {
        rules.add(violation.getRule());
      }
    }
    return rules;
  }

  static ResourceStatus newResourceStatus(SonarIndex index, Resource resource) {
    ResourceStatus status = new ResourceStatus(index, resource);
    status.setViolations(index.getViolations(resource));
    status.setRuleMeasures(index.getMeasures(resource, new RuleMeasuresFilter()));
    return status;
  }

  static ResourceStatus newTotalStatus(Collection<ResourceStatus> fileStatuses) {
    ResourceStatus status = new ResourceStatus();
    Map<RuleStatus.Key, RuleStatus> map = Maps.newHashMap();
    for (ResourceStatus fileStatus : fileStatuses) {
      for (RuleStatus ruleStatus : fileStatus.getRuleStatuses()) {
        RuleStatus rs = map.get(ruleStatus.getKey());
        if (rs == null) {
          rs = new RuleStatus(ruleStatus);
          map.put(ruleStatus.getKey(), rs);
        } else {
          rs.addRuleStatus(ruleStatus);
        }
      }
    }
    status.setRuleStatuses(map.values());
    return status;
  }
}
