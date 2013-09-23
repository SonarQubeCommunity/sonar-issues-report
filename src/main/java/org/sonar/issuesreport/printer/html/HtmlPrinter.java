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
package org.sonar.issuesreport.printer.html;

import com.google.common.collect.Maps;
import freemarker.log.Logger;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.sonar.issuesreport.IssuesReportConstants;
import org.sonar.issuesreport.printer.ReportPrinter;
import org.sonar.issuesreport.provider.RuleNameProvider;
import org.sonar.issuesreport.provider.SourceProvider;
import org.sonar.issuesreport.report.IssuesReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Map;

public class HtmlPrinter implements ReportPrinter {

  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(HtmlPrinter.class);

  private final RuleNameProvider ruleNameProvider;
  private final ModuleFileSystem fs;
  private Settings settings;

  private SourceProvider sourceProvider;

  public HtmlPrinter(RuleNameProvider ruleNameProvider, SourceProvider sourceProvider, ModuleFileSystem fs, Settings settings) {
    this.ruleNameProvider = ruleNameProvider;
    this.sourceProvider = sourceProvider;
    this.fs = fs;
    this.settings = settings;
  }

  @Override
  public boolean isEnabled() {
    return settings.getBoolean(IssuesReportConstants.HTML_REPORT_ENABLED_KEY);
  }

  @Override
  public void print(IssuesReport report) {
    String reportFileStr = settings.getString(IssuesReportConstants.HTML_REPORT_LOCATION_KEY);
    File reportFile = new File(reportFileStr);
    if (!reportFile.isAbsolute()) {
      reportFile = new File(fs.workingDir(), reportFileStr);
    }
    File parentDir = reportFile.getParentFile();
    try {
      FileUtils.forceMkdir(parentDir);
    } catch (IOException e) {
      throw new IllegalStateException("Fail to create the directory " + parentDir, e);
    }
    LOG.debug("Generating HTML Report to: " + reportFile.getAbsolutePath());
    writeToFile(report, reportFile);
    LOG.info("HTML Issues Report generated: " + reportFile.getAbsolutePath());
  }

  public void writeToFile(IssuesReport report, File toFile) {
    Writer writer = null;
    FileOutputStream fos = null;
    try {
      Logger.selectLoggerLibrary(Logger.LIBRARY_NONE);
      freemarker.template.Configuration cfg = new freemarker.template.Configuration();
      cfg.setClassForTemplateLoading(HtmlPrinter.class, "");
      // cfg.setObjectWrapper(new DefaultObjectWrapper());

      Map<String, Object> root = Maps.newHashMap();
      root.put("report", report);
      root.put("ruleNameProvider", ruleNameProvider);
      root.put("sourceProvider", sourceProvider);

      Template template = cfg.getTemplate("issuesreport.ftl");
      fos = new FileOutputStream(toFile);
      writer = new OutputStreamWriter(fos, fs.sourceCharset());
      template.process(root, writer);
      writer.flush();

      copyDependencies(toFile.getParentFile());

    } catch (Exception e) {
      throw new IllegalStateException("Fail to generate HTML Issues Report to: " + toFile, e);

    } finally {
      IOUtils.closeQuietly(writer);
      IOUtils.closeQuietly(fos);
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
    copyDependency(target, "sonarqube-24x100.png");
  }

  private void copyDependency(File target, String filename) {
    InputStream input = null;
    OutputStream output = null;
    try {
      input = getClass().getResourceAsStream("/org/sonar/issuesreport/printer/html/issuesreport_files/" + filename);
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
