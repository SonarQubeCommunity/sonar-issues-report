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
import org.sonar.api.rules.Violation;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ViolationComparatorByLineTest {

  private ViolationComparatorByLine comparator;
  private Violation v1;
  private Violation v2;

  @Before
  public void prepare() {
    comparator = new ViolationComparatorByLine();
    v1 = mock(Violation.class);
    v2 = mock(Violation.class);
  }

  @Test
  public void compareByLineEquals() {
    when(v1.getLineId()).thenReturn(1);
    when(v2.getLineId()).thenReturn(1);
    assertThat(comparator.compare(v1, v2)).isEqualTo(0);
  }

  @Test
  public void compareByLineSup() {
    when(v1.getLineId()).thenReturn(1);
    when(v2.getLineId()).thenReturn(2);
    assertThat(comparator.compare(v1, v2)).isLessThan(0);
  }

  @Test
  public void compareByLineInf() {
    when(v1.getLineId()).thenReturn(2);
    when(v2.getLineId()).thenReturn(1);
    assertThat(comparator.compare(v1, v2)).isGreaterThan(0);
  }
}
