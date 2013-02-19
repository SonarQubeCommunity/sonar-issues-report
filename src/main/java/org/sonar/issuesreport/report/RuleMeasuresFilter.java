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

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilter;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.RuleMeasure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RuleMeasuresFilter implements MeasuresFilter<Collection<RuleMeasure>> {

  private boolean isViolationsMetric(Metric metric) {
    return CoreMetrics.BLOCKER_VIOLATIONS.equals(metric) ||
      CoreMetrics.CRITICAL_VIOLATIONS.equals(metric) ||
      CoreMetrics.MAJOR_VIOLATIONS.equals(metric) ||
      CoreMetrics.MINOR_VIOLATIONS.equals(metric) ||
      CoreMetrics.INFO_VIOLATIONS.equals(metric);
  }

  private boolean apply(Measure measure) {
    return measure instanceof RuleMeasure
      && isViolationsMetric(measure.getMetric())
      && measure.getPersonId() == null
      && ((RuleMeasure) measure).getRule() != null;
  }

  public Collection<RuleMeasure> filter(Collection<Measure> measures) {
    if (measures == null) {
      return null;
    }
    List<RuleMeasure> result = new ArrayList<RuleMeasure>();
    for (Measure measure : measures) {
      if (apply(measure)) {
        result.add((RuleMeasure) measure);
      }
    }
    return result;
  }
}
