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
package org.sonar.issuesreport.provider;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleNameProviderTest {

  private RuleNameProvider names;
  private RuleFinder ruleFinder;

  @Before
  public void prepare() {

    ruleFinder = mock(RuleFinder.class);
    names = new RuleNameProvider(ruleFinder);
  }

  @Test
  public void name_from_database_or_key() {
    when(ruleFinder.findByKey(RuleKey.of("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck"))).thenReturn(null);

    String ruleName = "RULE_NAME";
    Rule rule = Rule.create("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck", ruleName);

    assertThat(names.nameForHTML(rule)).isEqualTo(ruleName);
    assertThat(names.nameForHTML(RuleKey.of("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck"))).isEqualTo(
      "checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck");
    assertThat(names.nameForJS("checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck")).isEqualTo(
      "checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck");

    String dbName = "My Rule";
    when(ruleFinder.findByKey(RuleKey.of("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck"))).thenReturn(
      Rule.create("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck", dbName));

    assertThat(names.nameForHTML(rule)).isEqualTo(dbName);
    assertThat(names.nameForHTML(RuleKey.of("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck"))).isEqualTo(dbName);
    assertThat(names.nameForJS("checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck")).isEqualTo(dbName);
  }

  @Test
  public void should_escape_name() {

    Rule rule = Rule.create("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck");

    when(ruleFinder.findByKey(RuleKey.of("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck"))).thenReturn(
      Rule.create("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck", "Annotation 'Use' Style & co"));

    assertThat(names.nameForHTML(rule)).isEqualTo("Annotation 'Use' Style &amp; co");
    assertThat(names.nameForHTML(RuleKey.of("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck"))).isEqualTo(
      "Annotation 'Use' Style &amp; co");
    assertThat(names.nameForJS("checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck")).isEqualTo("Annotation \\'Use\\' Style & co");

  }

}
