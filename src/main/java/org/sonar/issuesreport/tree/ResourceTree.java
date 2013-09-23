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

import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import javax.annotation.CheckForNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceTree implements Decorator {

  private static final Map<String, ResourceNode> resourceByKey = new HashMap<String, ResourceNode>();
  private final ModuleFileSystem fs;
  private final ResourceToFileMapper fileMapper;

  public ResourceTree(ModuleFileSystem fs, ResourceToFileMapper fileMapper) {
    this.fs = fs;
    this.fileMapper = fileMapper;
  }

  @Override
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  @Override
  public void decorate(Resource resource, DecoratorContext context) {
    if (!ResourceUtils.isPersistable(resource)) {
      return;
    }
    File path = fileMapper.getResourceFile(resource.getEffectiveKey());
    ResourceNode resourceNode = new ResourceNode(resource, path, fs.sourceCharset());
    resourceByKey.put(resource.getEffectiveKey(), resourceNode);
    for (DecoratorContext childContext : context.getChildren()) {
      Resource child = childContext.getResource();
      ResourceNode childNode = resourceByKey.get(child.getEffectiveKey());
      if (childNode != null) {
        resourceNode.addChild(childNode);
      }
    }
  }

  @CheckForNull
  public ResourceNode getResource(String componentKey) {
    return resourceByKey.get(componentKey);
  }

}
