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

import com.google.common.collect.ImmutableList;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.issuesreport.printer.console.ConsolePrinter;
import org.sonar.issuesreport.printer.html.HtmlPrinter;
import org.sonar.issuesreport.provider.RuleNameProvider;
import org.sonar.issuesreport.provider.SourceProvider;
import org.sonar.issuesreport.report.IssuesReportBuilder;
import org.sonar.issuesreport.tree.ResourceTree;

import java.util.List;

@Properties({
  @Property(key = IssuesReportPlugin.HTML_REPORT_ENABLED_KEY, name = "Enable HTML report", description = "Set this to true to generate an HTML report",
    type = PropertyType.BOOLEAN, defaultValue = "false"),
  @Property(key = IssuesReportPlugin.HTML_REPORT_LOCATION_KEY, name = "HTML Report location",
    description = "Location of the generated report. Can be absolute or relative to working directory",
    type = PropertyType.STRING, defaultValue = IssuesReportPlugin.HTML_REPORT_LOCATION_DEFAULT, global = false, project = false),
  @Property(key = IssuesReportPlugin.HTML_REPORT_NAME_KEY, name = "HTML Report name",
    description = "Name of the generated report. Will be suffixed by .html or -light.html",
    type = PropertyType.STRING, defaultValue = IssuesReportPlugin.HTML_REPORT_NAME_DEFAULT, global = false, project = false),
  @Property(key = IssuesReportPlugin.CONSOLE_REPORT_ENABLED_KEY, name = "Enable console report", description = "Set this to true to generate a report in console output",
    type = PropertyType.BOOLEAN, defaultValue = "false"),
  @Property(key = IssuesReportPlugin.HTML_REPORT_LIGHTMODE_ONLY, name = "Html report in light mode only", project = true,
    description = "Set this to true to only generate the new issues report (light report)",
    type = PropertyType.BOOLEAN, defaultValue = "false")})
public final class IssuesReportPlugin extends SonarPlugin {

  public static final String HTML_REPORT_ENABLED_KEY = "sonar.issuesReport.html.enable";
  public static final String HTML_REPORT_LOCATION_KEY = "sonar.issuesReport.html.location";
  public static final String HTML_REPORT_LOCATION_DEFAULT = "issues-report";
  public static final String HTML_REPORT_NAME_KEY = "sonar.issuesReport.html.name";
  public static final String HTML_REPORT_NAME_DEFAULT = "issues-report";
  public static final String HTML_REPORT_LIGHTMODE_ONLY = "sonar.issuesReport.lightModeOnly";

  public static final String CONSOLE_REPORT_ENABLED_KEY = "sonar.issuesReport.console.enable";

  public List getExtensions() {
    return ImmutableList.of(
      ReportJob.class,
      IssuesReportBuilder.class,
      RuleNameProvider.class,
      SourceProvider.class,
      ResourceTree.class,
      HtmlPrinter.class,
      ConsolePrinter.class);
  }
}
