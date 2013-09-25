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

import java.io.Serializable;
import java.util.Comparator;

public class RuleReportComparator implements Comparator<RuleReport>, Serializable {
  @Override
  public int compare(RuleReport o1, RuleReport o2) {
    if (o1.getTotal().getNewIssuesCount() == 0 && o2.getTotal().getNewIssuesCount() == 0) {
      // Compare with severity then name
      return o1.getReportRuleKey().compareTo(o2.getReportRuleKey());
    } else if (o1.getTotal().getNewIssuesCount() > 0 && o2.getTotal().getNewIssuesCount() > 0) {
      // Compare with severity then number of new issues then name
      if (o1.getSeverity().equals(o2.getSeverity()) && o2.getTotal().getNewIssuesCount() != o1.getTotal().getNewIssuesCount()) {
        return o2.getTotal().getNewIssuesCount() - o1.getTotal().getNewIssuesCount();
      } else {
        return o1.getReportRuleKey().compareTo(o2.getReportRuleKey());
      }
    } else {
      // Compare with number of new issues
      return o2.getTotal().getNewIssuesCount() - o1.getTotal().getNewIssuesCount();
    }
  }
}
