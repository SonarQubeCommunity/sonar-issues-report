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
package org.sonar.issuesreport.printer.console;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.Project;
import org.sonar.issuesreport.IssuesReportFakeUtils;
import org.sonar.issuesreport.IssuesReportPlugin;
import org.sonar.issuesreport.printer.console.ConsolePrinter.ConsoleLogger;
import org.sonar.issuesreport.report.IssuesReport;
import org.sonar.issuesreport.tree.ResourceNode;

import java.io.IOException;
import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ConsolePrinterTest {

  private Settings settings;
  private ConsolePrinter consolePrinter;

  @Before
  public void prepare() {
    settings = new Settings(new PropertyDefinitions(IssuesReportPlugin.class));
    consolePrinter = new ConsolePrinter(settings);
  }

  @Test
  public void shouldBeDisabledByDefault() {
    assertThat(consolePrinter.isEnabled()).isFalse();
  }

  @Test
  public void shouldEnableConsoleReport() {
    settings.setProperty(IssuesReportPlugin.CONSOLE_REPORT_ENABLED_KEY, "true");
    assertThat(consolePrinter.isEnabled()).isTrue();
  }

  @Test
  public void shouldGenerateReportWhenNoViolation() throws IOException {
    ConsolePrinter printer = new ConsolePrinter(new Settings());
    Project project = mock(Project.class);
    when(project.getAnalysisDate()).thenReturn(new Date());
    IssuesReport report = new IssuesReport();
    printer.print(report);
  }

  @Test
  public void shouldGenerateReportWhenNewViolation() throws IOException {
    ConsoleLogger logger = mock(ConsoleLogger.class);
    ConsolePrinter printer = new ConsolePrinter(new Settings(), logger);

    Project project = mock(Project.class);
    when(project.getAnalysisDate()).thenReturn(new Date());
    ResourceNode file = IssuesReportFakeUtils.fakeFile("com.foo.Bar");

    IssuesReport report = IssuesReportFakeUtils.sampleReportWith2IssuesPerFile(file);

    printer.print(report);

    verify(logger).log(contains("+1 issue"));
    verify(logger).log(contains("+1 blocking"));
  }
}
