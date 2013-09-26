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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Simplified version of {@link org.sonar.api.resources.Resource}
 *
 */
public class ResourceNode {

  private final String key;
  private final String name;
  private final String longName;
  private final List<ResourceNode> children = new ArrayList<ResourceNode>();
  private ResourceNode parent;
  private final File path;
  private final Charset encoding;
  private final String scope;

  public ResourceNode(Resource sonarResource, @Nullable File path, Charset encoding) {
    this.path = path;
    this.encoding = encoding;
    this.key = sonarResource.getEffectiveKey();
    this.name = sonarResource.getName();
    this.longName = sonarResource.getLongName();
    this.scope = sonarResource.getScope();
  }

  public String getKey() {
    return key;
  }

  public String getScope() {
    return scope;
  }

  public boolean isRootModule() {
    return getParent() == null;
  }

  public String getName() {
    if (isRootModule()) {
      // Root module
      return longName;
    }
    return getModulePrefix(this) + longName;
  }

  private String getModulePrefix(ResourceNode resource) {
    if (resource.isRootModule()) {
      return "";
    } else if (Scopes.PROJECT.equals(resource.getScope())) {
      return getModulePrefix(resource.getParent()) + resource.name + " - ";
    } else {
      return getModulePrefix(resource.getParent());
    }
  }

  @CheckForNull
  public File getPath() {
    return path;
  }

  @CheckForNull
  public ResourceNode getParent() {
    return parent;
  }

  public void addChild(ResourceNode child) {
    children.add(child);
    child.parent = this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    ResourceNode rhs = (ResourceNode) obj;
    return new EqualsBuilder().append(this.key, rhs.key).isEquals();
  }

  @Override
  public int hashCode() {
    return key != null ? key.hashCode() : 0;
  }

  public Charset getEncoding() {
    return encoding;
  }

}
