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

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.issuesreport.tree.ResourceNode;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SourceProviderTest {

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  // SONARPLUGINS-2710
  @Test
  public void shouldEscapeHtmlInSourceCode() throws Exception {
    SourceProvider sourceProvider = new SourceProvider();
    ResourceNode resource = mock(ResourceNode.class);

    File fakeFile = temp.newFile();
    FileUtils.write(fakeFile, "some\n&nbsp; <p> html", Charsets.UTF_8);

    when(resource.getPath()).thenReturn(fakeFile);
    when(resource.getEncoding()).thenReturn(Charsets.UTF_8);

    assertThat(sourceProvider.getEscapedSource(resource)).containsExactly("some", "&amp;nbsp; &lt;p&gt; html");
  }

  @Test
  public void shouldReturnEmptySourceForFolder() throws Exception {
    SourceProvider sourceProvider = new SourceProvider();
    ResourceNode resource = mock(ResourceNode.class);

    File fakeFolder = temp.newFolder();

    when(resource.getPath()).thenReturn(fakeFolder);
    when(resource.getEncoding()).thenReturn(Charsets.UTF_8);

    assertThat(sourceProvider.getEscapedSource(resource)).isEmpty();
  }

  @Test
  public void shouldReturnEmptySourceForModules() throws Exception {
    SourceProvider sourceProvider = new SourceProvider();
    ResourceNode resource = mock(ResourceNode.class);

    when(resource.getPath()).thenReturn(null);
    when(resource.getEncoding()).thenReturn(Charsets.UTF_8);

    assertThat(sourceProvider.getEscapedSource(resource)).isEmpty();
  }

}
