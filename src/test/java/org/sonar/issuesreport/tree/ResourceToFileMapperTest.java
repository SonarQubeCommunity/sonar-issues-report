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

import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.api.scan.filesystem.PathResolver;

import java.io.File;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourceToFileMapperTest {

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  private ModuleFileSystem fs;
  private PathResolver resolver;
  private ResourceToFileMapper resourceToFileMapper;

  @Before
  public void prepare() {
    fs = mock(ModuleFileSystem.class);
    resolver = new PathResolver();
    resourceToFileMapper = new ResourceToFileMapper(fs, resolver);
  }

  @Test
  public void should_execute_on_project() {
    assertThat(resourceToFileMapper.shouldExecuteOnProject(mock(Project.class))).isTrue();
  }

  @Test
  public void improve_coverage() {
    assertThat(resourceToFileMapper.toString()).isEqualTo("ResourceToFileMapper");
  }

  @Test
  public void should_locate_java_resources() throws Exception {

    File sourceFolder = temp.newFolder();
    File testFolder = temp.newFolder();

    File aJavaSource = new File(sourceFolder, "com/foo/Foo.java");
    FileUtils.write(aJavaSource, "foo");
    File aJavaTest = new File(testFolder, "com/foo/FooTest.java");
    FileUtils.write(aJavaTest, "foo test");

    when(fs.files(any(FileQuery.class))).thenReturn(Arrays.asList(aJavaSource)).thenReturn(Arrays.asList(aJavaTest));
    when(fs.sourceDirs()).thenReturn(Arrays.asList(sourceFolder));
    when(fs.testDirs()).thenReturn(Arrays.asList(testFolder));

    Project project = new Project("myproject").setConfiguration(new MapConfiguration(ImmutableMap.of("sonar.language", "java")));
    resourceToFileMapper.analyse(project, mock(SensorContext.class));

    assertThat(resourceToFileMapper.getResourceFile("myproject:com.foo.Foo")).isEqualTo(aJavaSource);
    assertThat(resourceToFileMapper.getResourceFile("myproject:com.foo.FooTest")).isEqualTo(aJavaTest);
  }

  @Test
  public void should_locate_other_resources() throws Exception {

    File sourceFolder = temp.newFolder();
    File testFolder = temp.newFolder();

    File aCSource = new File(sourceFolder, "com/foo/Foo.c");
    FileUtils.write(aCSource, "foo");
    File aCTest = new File(testFolder, "com/foo/FooTest.c");
    FileUtils.write(aCTest, "foo test");

    when(fs.files(any(FileQuery.class))).thenReturn(Arrays.asList(aCSource)).thenReturn(Arrays.asList(aCTest));
    when(fs.sourceDirs()).thenReturn(Arrays.asList(sourceFolder));
    when(fs.testDirs()).thenReturn(Arrays.asList(testFolder));

    Project project = new Project("myproject").setConfiguration(new MapConfiguration(ImmutableMap.of("sonar.language", "c")));
    resourceToFileMapper.analyse(project, mock(SensorContext.class));

    assertThat(resourceToFileMapper.getResourceFile("myproject:com/foo/Foo.c")).isEqualTo(aCSource);
    assertThat(resourceToFileMapper.getResourceFile("myproject:com/foo/FooTest.c")).isEqualTo(aCTest);
  }

  @Test
  public void should_not_fail_when_resource_not_found_in_source_dir() throws Exception {

    File sourceFolder = temp.newFolder();
    File testFolder = temp.newFolder();

    File externalFile = new File(temp.newFolder(), "another/file.c");
    File aCSource = new File(sourceFolder, "com/foo/Foo.c");
    FileUtils.write(aCSource, "foo");
    File aCTest = new File(testFolder, "com/foo/FooTest.c");
    FileUtils.write(aCTest, "foo test");

    when(fs.files(any(FileQuery.class))).thenReturn(Arrays.asList(aCSource, externalFile)).thenReturn(Arrays.asList(aCTest));
    when(fs.sourceDirs()).thenReturn(Arrays.asList(sourceFolder));
    when(fs.testDirs()).thenReturn(Arrays.asList(testFolder));

    Project project = new Project("myproject").setConfiguration(new MapConfiguration(ImmutableMap.of("sonar.language", "c")));
    resourceToFileMapper.analyse(project, mock(SensorContext.class));

    assertThat(resourceToFileMapper.getResourceFile("myproject:com/foo/Foo.c")).isEqualTo(aCSource);
  }
}
