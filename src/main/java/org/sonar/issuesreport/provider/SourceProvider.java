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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.task.TaskExtension;
import org.sonar.issuesreport.tree.ResourceNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SourceProvider implements TaskExtension {

  private static final Logger LOG = LoggerFactory.getLogger(SourceProvider.class);

  public SourceProvider() {
  }

  public List<String> getEscapedSource(ResourceNode resource) {
    File path = resource.getPath();
    if (path == null || path.isDirectory()) {
      // Folder
      return Collections.emptyList();
    }
    try {
      List<String> lines = FileUtils.readLines(path, resource.getEncoding().toString());
      List<String> escapedLines = new ArrayList<String>(lines.size());
      for (String line : lines) {
        escapedLines.add(StringEscapeUtils.escapeHtml(line));
      }
      return escapedLines;
    } catch (IOException e) {
      LOG.warn("Unable to read source code of resource {}", resource, e);
      return Collections.emptyList();
    }
  }

}
