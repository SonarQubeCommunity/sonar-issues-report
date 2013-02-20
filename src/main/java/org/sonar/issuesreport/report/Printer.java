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

import com.google.common.collect.Maps;
import freemarker.log.Logger;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Map;

public class Printer {

  public void print(HTMLReport report, File toFile, RuleNames ruleNames) {
    Writer writer = null;
    try {
      Logger.selectLoggerLibrary(Logger.LIBRARY_NONE);
      freemarker.template.Configuration cfg = new freemarker.template.Configuration();
      cfg.setClassForTemplateLoading(HTMLReport.class, "");
      cfg.setObjectWrapper(new DefaultObjectWrapper());

      Map<String, Object> root = Maps.newHashMap();
      root.put("report", report);
      root.put("ruleNames", ruleNames);

      Template template = cfg.getTemplate("issuesreport.ftl");
      writer = new FileWriter(toFile);
      template.process(root, writer);
      writer.flush();

      copyDependencies(toFile.getParentFile());

    } catch (Exception e) {
      throw new IllegalStateException("Fail to generate HTML Issues Report to: " + toFile, e);

    } finally {
      IOUtils.closeQuietly(writer);
    }
  }

  void copyDependencies(File toDir) throws URISyntaxException, IOException {
    File target = new File(toDir, "issuesreport_files");
    FileUtils.forceMkdir(target);

    // I don't know how to extract a directory from classpath, that's why an exhaustive list of files
    // is provided here :
    copyDependency(target, "BLOCKER.png");
    copyDependency(target, "CRITICAL.png");
    copyDependency(target, "MAJOR.png");
    copyDependency(target, "MINOR.png");
    copyDependency(target, "INFO.png");
    copyDependency(target, "favicon.ico");
    copyDependency(target, "CLA.png");
    copyDependency(target, "prototypejs.js");
    copyDependency(target, "sep12.png");
    copyDependency(target, "sonar.css");
    copyDependency(target, "sonar.png");
  }

  private void copyDependency(File target, String filename) {
    InputStream input = null;
    FileOutputStream output = null;
    try {
      input = getClass().getResourceAsStream("/org/sonar/issuesreport/report/issuesreport_files/" + filename);
      output = new FileOutputStream(new File(target, filename));
      IOUtils.copy(input, output);

    } catch (IOException e) {
      throw new IllegalStateException("Fail to copy file " + filename + " to " + target, e);
    } finally {
      IOUtils.closeQuietly(input);
      IOUtils.closeQuietly(output);
    }
  }
}
