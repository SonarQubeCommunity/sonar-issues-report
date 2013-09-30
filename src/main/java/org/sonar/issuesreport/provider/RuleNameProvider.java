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

import org.apache.commons.lang.StringEscapeUtils;
import org.sonar.api.i18n.I18n;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.task.TaskExtension;

import java.util.Locale;

public class RuleNameProvider implements TaskExtension {
  private static final String RULE_PREFIX = "rule.";
  private static final String NAME_SUFFIX = ".name";
  private I18n i18n;

  public RuleNameProvider(I18n i18n) {
    this.i18n = i18n;
  }

  private String name(RuleKey ruleKey) {
    String name = message(ruleKey.repository(), ruleKey.rule(), Locale.ENGLISH, NAME_SUFFIX);
    return name != null ? name : ruleKey.toString();
  }

  public String nameForHTML(RuleKey ruleKey) {
    return StringEscapeUtils.escapeHtml(name(ruleKey));
  }

  public String nameForJS(String ruleKey) {
    return StringEscapeUtils.escapeJavaScript(name(RuleKey.parse(ruleKey)));
  }

  public String nameForHTML(Rule rule) {
    String name = message(rule.getRepositoryKey(), rule.getKey(), Locale.ENGLISH, NAME_SUFFIX);
    return StringEscapeUtils.escapeHtml(name != null ? name : rule.getName());
  }

  String message(String repositoryKey, String ruleKey, Locale locale, String suffix) {
    String propertyKey = new StringBuilder().append(RULE_PREFIX).append(repositoryKey).append(".").append(ruleKey).append(suffix).toString();
    return i18n.message(locale, propertyKey, null);
  }
}
