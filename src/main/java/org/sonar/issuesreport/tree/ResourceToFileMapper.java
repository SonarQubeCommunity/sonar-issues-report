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

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.database.model.ResourceModel;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.JavaFile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.api.scan.filesystem.PathResolver;
import org.sonar.api.scan.filesystem.PathResolver.RelativePath;

import javax.annotation.CheckForNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceToFileMapper implements Sensor {

  private static Map<String, File> fileByResourceKey = new HashMap<String, File>();

  private ModuleFileSystem fs;

  private PathResolver pathResolver;

  public ResourceToFileMapper(ModuleFileSystem fs, PathResolver pathResolver) {
    this.fs = fs;
    this.pathResolver = pathResolver;
  }

  /**
   * Generally this method should not be overridden in subclasses, but if it is, then it should be executed anyway (see SONAR-3419).
   */
  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  @Override
  public void analyse(Project project, SensorContext context) {
    parseDirs(project, context, fs.files(FileQuery.onSource()), fs.sourceDirs(), false);
    parseDirs(project, context, fs.files(FileQuery.onTest()), fs.testDirs(), true);
  }

  protected void parseDirs(Project project, SensorContext context, List<File> files, List<File> sourceDirs, boolean unitTest) {
    for (File file : files) {
      String resourceKey = resolveComponent(file, sourceDirs, project, unitTest);
      if (resourceKey != null) {
        fileByResourceKey.put(resourceKey, file);
      }
    }
  }

  /**
   * This method is necessary because Java resources are not treated as every other resource...
   * Copied from org.sonar.plugins.core.issue.ignore.scanner.SourceScanner
   */
  private String resolveComponent(File inputFile, List<File> sourceDirs, Project project, boolean isTest) {
    Resource resource = null;

    if (Java.KEY.equals(project.getLanguageKey()) && Java.isJavaFile(inputFile)) {

      resource = JavaFile.fromIOFile(inputFile, sourceDirs, isTest);
    } else {
      RelativePath relativePath = pathResolver.relativePath(sourceDirs, inputFile);
      if (relativePath != null) {
        resource = new org.sonar.api.resources.File(relativePath.path());
      }
    }

    if (resource == null) {
      return null;
    } else {
      return createKey(project, resource);
    }
  }

  /**
   * Copied from {@link org.sonar.core.component.ComponentKeys#createKey(Project, Resource)}
   */
  private static String createKey(Project project, Resource resource) {
    String key = resource.getKey();
    if (!StringUtils.equals(Scopes.PROJECT, resource.getScope())) {
      // not a project nor a library
      key = new StringBuilder(ResourceModel.KEY_SIZE)
        .append(project.getKey())
        .append(':')
        .append(resource.getKey())
        .toString();
    }
    return key;
  }

  @CheckForNull
  public File getResourceFile(String resourceKey) {
    return fileByResourceKey.get(resourceKey);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }

}
