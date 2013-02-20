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
package org.sonar.issuesreport;

import org.sonar.issuesreport.report.html.HTMLPrinter;
import org.sonar.issuesreport.report.html.HTMLReport;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.SonarIndex;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.api.scan.filesystem.ModuleFileSystem;

import java.io.File;
import java.io.IOException;

public class ReportJob implements PostJob {

  private static final Logger LOG = LoggerFactory.getLogger(ReportJob.class);

  private final SonarIndex index;
  private final Settings settings;
  private final ModuleFileSystem fs;
  private final HTMLPrinter htmlPrinter;

  public ReportJob(SonarIndex index, Settings settings, ModuleFileSystem fs, HTMLPrinter htmlPrinter) {
    this.index = index;
    this.settings = settings;
    this.fs = fs;
    this.htmlPrinter = htmlPrinter;
  }

  public void executeOn(Project project, SensorContext context) {
    if (settings.getBoolean(IssuesReportConstants.HTML_REPORT_ENABLED_KEY)) {
      HTMLReport report = new HTMLReport(project, project.getName(), index);
      printHTMLReport(report);
    }
  }

  File printHTMLReport(HTMLReport report) {
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
    htmlPrinter.print(report, reportFile);
    LOG.info("HTML Report generated: " + reportFile.getAbsolutePath());
    return reportFile;
  }

}
