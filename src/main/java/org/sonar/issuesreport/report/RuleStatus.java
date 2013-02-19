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

import org.apache.commons.lang.ObjectUtils;
import org.sonar.api.measures.RuleMeasure;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;

import java.util.Comparator;
import java.util.List;

public final class RuleStatus {

  public static final class Key {
    private Rule rule;
    private RulePriority severity;

    private Key(Rule rule, RulePriority severity) {
      this.rule = rule;
      this.severity = severity;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Key that = (Key) o;
      return ObjectUtils.equals(rule, that.rule) && ObjectUtils.equals(severity, that.severity);
    }

    @Override
    public int hashCode() {
      int result = rule.hashCode();
      result = 31 * result + severity.hashCode();
      return result;
    }
  }

  public static final class ComparatorByValue implements Comparator<RuleStatus> {
    public int compare(RuleStatus m1, RuleStatus m2) {
      return ObjectUtils.compare(m2.getValue(), m1.getValue());
    }
  }

  private final Key key;
  private final Rule rule;
  private final RulePriority severity;
  private int value;
  private int variation;

  public RuleStatus(RuleMeasure ruleMeasure, List<Violation> violations) {
    this.rule = ruleMeasure.getRule();
    this.severity = ruleMeasure.getSeverity();
    this.key = new Key(this.rule, this.severity);
    this.value = ruleMeasure.getIntValue();
    this.variation = 0;
    for (Violation v : violations) {
      if (v.isNew()) {
        this.variation++;
      }
    }
  }

  public RuleStatus(RuleStatus status) {
    this.rule = status.getRule();
    this.severity = status.getSeverity();
    this.key = new Key(this.rule, this.severity);
    this.value = status.getValue();
    this.variation = status.getVariation();
  }

  public void addRuleStatus(RuleStatus status) {
    this.value += status.getValue();
    this.variation += status.getVariation();
  }

  public Key getKey() {
    return key;
  }

  public Rule getRule() {
    return rule;
  }

  public int getValue() {
    return value;
  }

  public int getVariation() {
    return variation;
  }

  public RulePriority getSeverity() {
    return severity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RuleStatus that = (RuleStatus) o;
    return key.equals(that.key);

  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }
}
