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
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.task.TaskExtension;

import javax.annotation.CheckForNull;

public class RuleNameProvider implements TaskExtension {
  private RuleFinder ruleFinder;

  public RuleNameProvider(RuleFinder ruleFinder) {
    this.ruleFinder = ruleFinder;
  }

  @CheckForNull
  private String nameFromDB(RuleKey ruleKey) {
    Rule r = ruleFinder.findByKey(ruleKey);
    return r != null ? r.getName() : null;
  }

  public String nameForHTML(RuleKey ruleKey) {
    String name = nameFromDB(ruleKey);
    return StringEscapeUtils.escapeHtml(name != null ? name : ruleKey.toString());
  }

  public String nameForJS(String ruleKey) {
    String name = nameFromDB(RuleKey.parse(ruleKey));
    return StringEscapeUtils.escapeJavaScript(name != null ? name : ruleKey.toString());
  }

  public String nameForHTML(Rule rule) {
    String name = nameFromDB(RuleKey.of(rule.getRepositoryKey(), rule.getKey()));
    return StringEscapeUtils.escapeHtml(name != null ? name : rule.getName());
  }

}
