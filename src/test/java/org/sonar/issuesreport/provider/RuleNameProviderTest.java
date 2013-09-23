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

import org.sonar.issuesreport.provider.RuleNameProvider;

import org.junit.Test;
import org.sonar.api.i18n.I18n;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;

import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RuleNameProviderTest {
  @Test
  public void name_from_database_or_key() {
    String propertyKey = "rule.checkstyle.com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck.name";
    I18n i18n = mock(I18n.class);
    when(i18n.message(Locale.ENGLISH, propertyKey, null)).thenReturn(null);
    RuleNameProvider names = new RuleNameProvider(i18n);

    String ruleName = "RULE_NAME";
    Rule rule = Rule.create("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck", ruleName);

    assertThat(names.name(rule)).isEqualTo(ruleName);
    assertThat(names.name(RuleKey.of("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck"))).isEqualTo(
      "checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck");
    assertThat(names.name("checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck")).isEqualTo(
      "checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck");
  }

  @Test
  public void name_from_bundle() {
    String propertyKey = "rule.checkstyle.com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck.name";
    I18n i18n = mock(I18n.class);
    when(i18n.message(Locale.ENGLISH, propertyKey, null)).thenReturn("Annotation Use Style");
    RuleNameProvider names = new RuleNameProvider(i18n);

    Rule rule = Rule.create("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck");
    assertThat(names.name(rule)).isEqualTo("Annotation Use Style");
    assertThat(names.name(RuleKey.of("checkstyle", "com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck"))).isEqualTo("Annotation Use Style");
    assertThat(names.name("checkstyle:com.puppycrawl.tools.checkstyle.checks.annotation.AnnotationUseStyleCheck")).isEqualTo("Annotation Use Style");
  }
}
