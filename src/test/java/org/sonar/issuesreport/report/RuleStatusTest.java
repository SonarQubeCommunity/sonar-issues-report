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
import org.sonar.api.measures.RuleMeasure;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.Violation;

import java.util.ArrayList;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleStatusTest {

  @Before
  public void prepare() {
  }

  @Test
  public void testAttributes() {
    Rule rule = Rule.create();
    RuleMeasure ruleMeasure = mock(RuleMeasure.class);
    when(ruleMeasure.getSeverity()).thenReturn(RulePriority.BLOCKER);
    when(ruleMeasure.getRule()).thenReturn(rule);
    RuleStatus r1 = new RuleStatus(ruleMeasure, new ArrayList<Violation>());
    assertThat(r1.getRule()).isEqualTo(rule);
    assertThat(r1.getSeverity()).isEqualTo(RulePriority.BLOCKER);
    assertThat(r1.getValue()).isEqualTo(0);
    assertThat(r1.getVariation()).isEqualTo(0);
    assertThat(r1.getKey()).isNotNull();
  }

  @Test
  public void testCreateFromRuleStatus() {
    Rule rule = Rule.create();
    RuleMeasure ruleMeasure = mock(RuleMeasure.class);
    when(ruleMeasure.getSeverity()).thenReturn(RulePriority.BLOCKER);
    when(ruleMeasure.getRule()).thenReturn(rule);
    RuleStatus r1 = new RuleStatus(ruleMeasure, new ArrayList<Violation>());
    RuleStatus r2 = new RuleStatus(r1);
    assertThat(r2.getRule()).isEqualTo(r1.getRule());
    assertThat(r2.getSeverity()).isEqualTo(r1.getSeverity());
    assertThat(r2.getValue()).isEqualTo(r1.getValue());
    assertThat(r2.getVariation()).isEqualTo(r1.getVariation());
    assertThat(r2.getKey()).isEqualTo(r1.getKey());
  }

  @Test
  public void testAddRuleStatus() {
    Rule rule = Rule.create();
    RuleMeasure ruleMeasure = mock(RuleMeasure.class);
    when(ruleMeasure.getSeverity()).thenReturn(RulePriority.BLOCKER);
    when(ruleMeasure.getRule()).thenReturn(rule);
    when(ruleMeasure.getIntValue()).thenReturn(2);
    RuleStatus r1 = new RuleStatus(ruleMeasure, new ArrayList<Violation>());
    assertThat(r1.getValue()).isEqualTo(2);
    assertThat(r1.getVariation()).isEqualTo(0);

    Violation v1 = mock(Violation.class);
    when(v1.isNew()).thenReturn(false);
    Violation v2 = mock(Violation.class);
    when(v2.isNew()).thenReturn(true);
    RuleStatus r2 = new RuleStatus(ruleMeasure, Arrays.asList(v1, v2));
    assertThat(r2.getVariation()).isEqualTo(1);

    r1.addRuleStatus(r2);
    assertThat(r1.getValue()).isEqualTo(4);
    assertThat(r1.getVariation()).isEqualTo(1);
  }

  @Test
  public void testEquals() {
    Rule rule = Rule.create();
    RuleMeasure ruleMeasure = mock(RuleMeasure.class);
    when(ruleMeasure.getSeverity()).thenReturn(RulePriority.BLOCKER);
    when(ruleMeasure.getRule()).thenReturn(rule);
    RuleStatus r1 = new RuleStatus(ruleMeasure, new ArrayList<Violation>());
    RuleStatus r2 = new RuleStatus(ruleMeasure, new ArrayList<Violation>());
    assertThat(r1.equals(r1)).isTrue();
    assertThat(r1.equals(r2)).isTrue();
    assertThat(r1.equals(null)).isFalse();
    assertThat(r1.equals(rule)).isFalse();
  }

  @Test
  public void testHashCode() {
    Rule rule = Rule.create();
    RuleMeasure ruleMeasure = mock(RuleMeasure.class);
    when(ruleMeasure.getSeverity()).thenReturn(RulePriority.BLOCKER);
    when(ruleMeasure.getRule()).thenReturn(rule);
    RuleStatus r1 = new RuleStatus(ruleMeasure, new ArrayList<Violation>());
    assertThat(r1.hashCode()).isGreaterThan(0);
  }

  @Test
  public void testNewViolations() {
    Rule rule = Rule.create();
    RuleMeasure ruleMeasure = mock(RuleMeasure.class);
    when(ruleMeasure.getSeverity()).thenReturn(RulePriority.BLOCKER);
    when(ruleMeasure.getRule()).thenReturn(rule);
    RuleStatus r1 = new RuleStatus(ruleMeasure, new ArrayList<Violation>());
    assertThat(r1.getVariation()).isEqualTo(0);

    Violation v1 = mock(Violation.class);
    when(v1.isNew()).thenReturn(false);
    r1 = new RuleStatus(ruleMeasure, Arrays.asList(v1));
    assertThat(r1.getVariation()).isEqualTo(0);

    Violation v2 = mock(Violation.class);
    when(v2.isNew()).thenReturn(true);
    r1 = new RuleStatus(ruleMeasure, Arrays.asList(v1, v2));
    assertThat(r1.getVariation()).isEqualTo(1);
  }
}
