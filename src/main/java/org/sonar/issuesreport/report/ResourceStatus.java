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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.measures.RuleMeasure;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.Violation;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class ResourceStatus implements Comparable<ResourceStatus> {
  private SonarIndex index;
  private Resource resource;
  private int value;
  private int variation;
  private List<RuleStatus> ruleStatuses;
  private List<Violation> sortedViolationsByLine;
  private ListMultimap<Integer, Violation> violationsPerLine;
  private Map<Rule, List<Violation>> violationsByRule;

  public ResourceStatus(SonarIndex index, Resource resource) {
    this.index = index;
    this.resource = resource;
  }

  /**
   * For total synthesis, displayed in the top of the report
   */
  public ResourceStatus() {
  }

  public Resource getResource() {
    return resource;
  }

  public String getKey() {
    return (resource != null ? resource.getKey() : "total");
  }

  public String getName() {
    return (resource != null ? resource.getName() : null);
  }

  public int getValue() {
    return value;
  }

  public int getVariation() {
    return variation;
  }

  public List<RuleStatus> getRuleStatuses() {
    return ruleStatuses;
  }

  public void setRuleMeasures(Collection<RuleMeasure> collection) {
    this.ruleStatuses = Lists.newArrayList();
    for (RuleMeasure ruleMeasure : collection) {
      if (ruleMeasure.getValue() != null) {
        ruleStatuses.add(new RuleStatus(ruleMeasure, violationsByRule.get(ruleMeasure.getRule())));
      }
    }
    // rules are ordered by number of violations
    Collections.sort(ruleStatuses, new RuleStatusComparatorByValue());
    computeRuleValues();
  }

  public void setRuleStatuses(Collection<RuleStatus> collection) {
    // rules are ordered by number of violations
    this.ruleStatuses = Lists.newArrayList(collection);
    Collections.sort(ruleStatuses, new RuleStatusComparatorByValue());
    computeRuleValues();
  }

  void computeRuleValues() {
    value = 0;
    variation = 0;
    for (RuleStatus ruleStatus : ruleStatuses) {
      value += ruleStatus.getValue();
      variation += ruleStatus.getVariation();
    }
  }

  public List<String> getLines() throws IOException {
    String source = StringUtils.defaultString(index.getSource(resource));
    String escapedSource = StringEscapeUtils.escapeHtml(source);
    return IOUtils.readLines(new StringReader(escapedSource));
  }

  public boolean isDisplayableLine(Integer lineId) {
    if (lineId == null || lineId < 1) {
      return false;
    }
    return hasViolations(lineId - 2) || hasViolations(lineId - 1) || hasViolations(lineId) || hasViolations(lineId + 1) || hasViolations(lineId + 2);
  }

  private boolean hasViolations(Integer lineId) {
    List<Violation> violations = getViolations(lineId);
    return violations != null && !violations.isEmpty();
  }

  public List<Violation> getViolations(int lineId) {
    return violationsPerLine.get(lineId);
  }

  public List<Violation> getViolations() {
    // it's important to return an ordered list of violations because the report generates fake ids. Order must be the same
    // in the method getViolations(int)
    return sortedViolationsByLine;
  }

  public void setViolations(List<Violation> violations) {
    sortedViolationsByLine = Lists.newArrayList(violations);
    Collections.sort(sortedViolationsByLine, new ViolationComparatorByLine());
    violationsPerLine = ArrayListMultimap.create();
    violationsByRule = Maps.newHashMap();
    for (Violation violation : sortedViolationsByLine) {
      Integer lineId = (Integer) ObjectUtils.defaultIfNull(violation.getLineId(), 0);
      violationsPerLine.put(lineId, violation);
      if (!violationsByRule.containsKey(violation.getRule())) {
        violationsByRule.put(violation.getRule(), new LinkedList<Violation>());
      }
      violationsByRule.get(violation.getRule()).add(violation);
    }
  }

  public int compareTo(ResourceStatus other) {
    if (resource.equals(other.getResource())) {
      return 0;
    }
    return ObjectUtils.compare(resource.getName(), other.getResource().getName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResourceStatus that = (ResourceStatus) o;
    return resource.equals(that.resource);
  }

  @Override
  public int hashCode() {
    return resource.hashCode();
  }

}
