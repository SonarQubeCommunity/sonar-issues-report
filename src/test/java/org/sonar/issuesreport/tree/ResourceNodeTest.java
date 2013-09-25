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
package org.sonar.issuesreport.tree;

import com.google.common.base.Charsets;
import org.junit.Test;
import org.sonar.api.resources.Project;

import static org.fest.assertions.Assertions.assertThat;

public class ResourceNodeTest {

  @Test
  public void test_equals() {
    ResourceNode r = new ResourceNode(new Project("myProject"), null, Charsets.UTF_8);
    assertThat(r).isEqualTo(r);
    assertThat(r).isNotEqualTo(null);
    assertThat(r).isNotEqualTo("another object");
    assertThat(r).isEqualTo(new ResourceNode(new Project("myProject"), null, Charsets.UTF_8));
  }

  @Test
  public void test_hashCode() {
    ResourceNode r = new ResourceNode(new Project("myProject"), null, Charsets.UTF_8);
    assertThat(r.hashCode()).isEqualTo("myProject".hashCode());

    r = new ResourceNode(new Project(null), null, Charsets.UTF_8);
    assertThat(r.hashCode()).isEqualTo(0);
  }
}
