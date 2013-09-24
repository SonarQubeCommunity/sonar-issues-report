<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Issues report of ${report.getTitle()}</title>
  <link href="issuesreport_files/sonar.css" media="all" rel="stylesheet" type="text/css">
  <link rel="shortcut icon" type="image/x-icon" href="issuesreport_files/favicon.ico">
  <script type="text/javascript" src="issuesreport_files/jquery.min.js"></script>
  <script type="text/javascript">
    var issuesPerResource = [
    <#list report.getResourceReports() as resourceReport>
      [
        <#assign issues=resourceReport.getIssues()>
        <#list issues as issue>
          <#if complete || issue.isNew()>
          {'i': ${issue_index?c}, 'r': 'R${issue.ruleKey()}', 'l': ${(issue.line()!0)?c}, 'new': ${issue.isNew()?string}, 's': '${issue.severity()}'}<#if issue_has_next>,</#if>
          </#if>
        </#list>
      ]
      <#if resourceReport_has_next>,</#if>
    </#list>
    ];
    var nbResources = ${report.getResourcesWithReport()?size};
    var separators = new Array();

    function showLine(fileIndex, lineId) {
      var elt = $('#' + fileIndex + 'L' + lineId);
      if (elt != null) {
        elt.show();
      }
      elt = $('#' + fileIndex + 'LV' + lineId);
      if (elt != null) {
        elt.show();
      }
    }

    /* lineIds must be sorted */
    function showLines(fileIndex, lineIds) {
      var lastSeparatorId = 9999999;
      for (var lineIndex = 0; lineIndex < lineIds.length; lineIndex++) {
        var lineId = lineIds[lineIndex];
        if (lineId > 0) {
          if (lineId > lastSeparatorId) {
            var separator = $(fileIndex + 'S' + lastSeparatorId);
            if (separator != null) {
              separator.addClass('visible');
              separators.push(separator);
            }
          }

          for (var i = -2; i < 3; ++i) {
            showLine(fileIndex, lineId + i);
          }
          lastSeparatorId = lineId + 2;
        }
      }
    }
     function hideAll() {
       $('tr.row').hide();
       $('div.issue').hide();
       for (var separatorIndex = 0; separatorIndex < separators.length; separatorIndex++) {
         separators[separatorIndex].removeClass('visible');
       }
       separators.length = 0;
       $('.sources td.ko').removeClass('ko');
     }

     function showIssues(fileIndex, issues) {
       $.each(issues, function(index, issue) {
         $('#' + fileIndex + 'V' + issue['i']).show();
         $('#' + fileIndex + 'L' + issue['l'] + ' td.line').addClass('ko');
       });
     }


    function refreshFilters() {
      <#if complete>
      var onlyNewIssues = $('#new_filter').is(':checked');
      <#else>
      var onlyNewIssues = true;
      </#if>
      var ruleFilter = $('#rule_filter').val();

      hideAll();
      if (onlyNewIssues) {
        $('.all').hide();
        $('.new').show();
      } else {
        $('.new').hide();
        $('.all').show();
      }
      for (var resourceIndex = 0; resourceIndex < nbResources; resourceIndex++) {
        var filteredIssues = $.grep(issuesPerResource[resourceIndex], function(v) {
              return (!onlyNewIssues || v['new']) && (ruleFilter == '' || v['r'] == ruleFilter || v['s'] == ruleFilter);
            }
        );

        var linesToDisplay = $.map(filteredIssues, function(v, i) {
          return v['l'];
        });

        linesToDisplay.sort();// the showLines() requires sorted ids
        showLines(resourceIndex, linesToDisplay);
        showIssues(resourceIndex, filteredIssues);
      }
    }
  </script>
</head>
<body>
<div id="reportHeader">
  <div id="logo"><img src="issuesreport_files/sonarqube-24x100.png" alt="SonarQube"/></div>
  <div class="title">Issues Report</div>
  <div class="subtitle">${report.getTitle()} - ${report.getDate()?datetime}</div>
</div>

<div id="content">

  <#if !complete>
  <div class="banner">Short report: only new issues are displayed</div>
  </#if>

  <div id="summary">
  <table width="100%">
    <tbody>
    <tr>
    <#if complete>
      <#assign size = '33'>
    <#else>
      <#assign size = '50'>
    </#if>
    <#if complete>
      <td align="center" width="${size}%"><span class="big">${report.getSummary().getTotal().getCountInCurrentAnalysis()?c}</span> <br/>Issues</td>
    </#if>
      <td align="center" width="${size}%">
      <#if report.getSummary().getTotal().getResolvedIssuesCount() gt 0>
        <span class="big better">${report.getSummary().getTotal().getResolvedIssuesCount()?c}</span>
      <#else>
        <span class="big">0</span>
      </#if>
        <br/>Resolved issues
      </td>
      <td align="center" width="${size}%">
      <#if report.getSummary().getTotal().getNewIssuesCount() gt 0>
        <span class="big worst">${report.getSummary().getTotal().getNewIssuesCount()?c}</span>
      <#else>
        <span class="big">0</span>
      </#if>
        <br/>New issues
      </td>
    </tr>
    </tbody>
  </table>
  <table width="100%" class="data">
    <#if complete>
      <#assign defaultVisibility = ''>
    <#else>
      <#assign defaultVisibility = 'display: none;'>
    </#if>
    <thead>
    <tr class="total">
      <th colspan="2" align="left">
          <a href="#" onclick="$('.rule-details').toggle(); return false;"  style="color: black">Issues per Rule</a>
      </th>
      <th class="rule-details" style="${defaultVisibility}" align="right" width="1%" nowrap>Issues</th>
      <th class="rule-details" style="${defaultVisibility}" align="right" width="1%" nowrap>Resolved issues</th>
      <th class="rule-details" style="${defaultVisibility}" align="right" width="1%" nowrap>New issues</th>
    </tr>
    </thead>
    <tbody style="${defaultVisibility}" class="rule-details">
      <#list report.getSummary().getRuleReports() as ruleReport>
        <#if complete || (ruleReport.getTotal().getNewIssuesCount() > 0)>
          <#assign trCss = (ruleReport_index % 2 == 0)?string("even","odd")>
          <#if ruleReport.getTotal().getNewIssuesCount() = 0>
          <#assign trCss = trCss + ' all'>
          <#else>
          <#assign trCss = trCss + ' new all'>
          </#if>
      <tr class="${trCss}">
        <td width="20">
          <img alt="${ruleReport.getSeverity()}" title="${ruleReport.getSeverity()}" src="issuesreport_files/${ruleReport.getSeverity()}.png">
        </td>
        <td align="left">
          ${ruleNameProvider.name(ruleReport.getRule())}
        </td>
        <td align="right">
          ${ruleReport.getTotal().getCountInCurrentAnalysis()?c}
        </td>
        <td align="right">
          <#if ruleReport.getTotal().getResolvedIssuesCount() gt 0>
            <span class="better">-${ruleReport.getTotal().getResolvedIssuesCount()?c}</span>
          <#else>
            <span>0</span>
          </#if>
        </td>
        <td align="right">
          <#if ruleReport.getTotal().getNewIssuesCount() gt 0>
            <span class="worst">+${ruleReport.getTotal().getNewIssuesCount()?c}</span>
          <#else>
            <span>0</span>
          </#if>
        </td>
      </tr>
        </#if>
      </#list>
    </tbody>
  </table>
  </div>

  <br/>

  <div class="banner">
  <#if complete>
    <input type="checkbox" id="new_filter" onclick="refreshFilters()" checked="checked" /> <label for="new_filter">Only NEW
    issues</label>
    &nbsp;&nbsp;&nbsp;&nbsp;
  </#if>

    <select id="rule_filter" onchange="refreshFilters()">
      <option value="" selected>Filter by:</option>
      <optgroup label="Severity">
      <#assign severities = report.getSummary().getTotalBySeverity()>
      <#list severities?keys as severity>
        <option value="${severity}" class="all">
          ${severity?lower_case?cap_first}
          (${severities[severity].getCountInCurrentAnalysis()?c})
        </option>
        <#if complete || (severities[severity].getNewIssuesCount() > 0)>
        <option value="${severity}" class="new">
          ${severity?lower_case?cap_first}
          (${severities[severity].getNewIssuesCount()?c})
        </option>
        </#if>
      </#list>
      </optgroup>
      <optgroup label="Rule">
      <#assign rules = report.getSummary().getTotalByRuleKey()>
      <#list rules?keys as ruleKey>
        <option value="R${ruleKey}" class="all">
          ${ruleNameProvider.name(ruleKey)}
          (${rules[ruleKey].getCountInCurrentAnalysis()?c})
        </option>
        <#if complete || (rules[ruleKey].getNewIssuesCount() > 0)>
        <option value="R${ruleKey}" class="new">
          ${ruleNameProvider.name(ruleKey)}
          (${rules[ruleKey].getNewIssuesCount()?c})
        </option>
        </#if>
      </#list>
      </optgroup>
    </select>
  </div>

  <div id="summary-per-file">
  <#list report.getResourceReports() as resourceReport>
    <#if complete || (resourceReport.getTotal().getNewIssuesCount() > 0)>
      <#assign issueId=0>
      <#if resourceReport.getTotal().getNewIssuesCount() = 0>
      <#assign tableCss = 'all'>
      <#else>
      <#assign tableCss = 'new all'>
      </#if>
  <table width="100%" class="data ${tableCss}">
    <thead>
    <tr class="total">
      <th align="left" colspan="2">
        <div class="file_title">
          <img src="issuesreport_files/${resourceReport.getType()}.png" title="Resource icon"/>
          <a href="#" onclick="$('.resource-details-${resourceReport_index?c}').toggle(); return false;" style="color: black">${resourceReport.getName()}</a>
        </div>
      </th>
      <#if complete>
      <th align="right" width="1%" nowrap class="resource-details-${resourceReport_index?c}">
        <span id="current-total">${resourceReport.getTotal().getCountInCurrentAnalysis()?c}</span><br/>Issues
      </th>
      </#if>
      <#if complete>
      <th align="right" width="1%" nowrap class="resource-details-${resourceReport_index?c}">
        <#if resourceReport.getTotal().getResolvedIssuesCount() gt 0>
          <span class="better" id="resolved-total">-${resourceReport.getTotal().getResolvedIssuesCount()?c}</span>
        <#else>
          <span id="resolved-total">0</span>
        </#if>
        <br/>Resolved issues
      </th>
      </#if>
      <th align="right" width="1%" nowrap class="resource-details-${resourceReport_index?c}">
        <#if resourceReport.getTotal().getNewIssuesCount() gt 0>
          <span class="worst" id="new-total">+${resourceReport.getTotal().getNewIssuesCount()?c}</span>
        <#else>
          <span id="new-total">0</span>
        </#if>
        <br/>New issues
      </th>
    </tr>
    </thead>
    <tbody class="resource-details-${resourceReport_index?c}">
    <#list resourceReport.getRuleReports() as ruleReport>
      <#if complete || (ruleReport.getTotal().getNewIssuesCount() > 0)>
        <#assign trCss = (ruleReport_index % 2 == 0)?string("even","odd")>
        <#if ruleReport.getTotal().getNewIssuesCount() = 0>
        <#assign trCss = trCss + ' all'>
        <#else>
        <#assign trCss = trCss + ' new all'>
        </#if>
      <tr class="${trCss}">
        <td width="20">
          <img alt="${ruleReport.getSeverity()}" title="${ruleReport.getSeverity()}" src="issuesreport_files/${ruleReport.getSeverity()}.png">
        </td>
        <td align="left">
          ${ruleNameProvider.name(ruleReport.getRule())}
        </td>
        <#if complete>
        <td align="right">
          ${ruleReport.getTotal().getCountInCurrentAnalysis()?c}
        </td>
        </#if>
        <#if complete>
        <td align="right">
          <#if ruleReport.getTotal().getResolvedIssuesCount() gt 0>
            <span class="better">-${ruleReport.getTotal().getResolvedIssuesCount()?c}</span>
          <#else>
            <span>0</span>
          </#if>
        </td>
        </#if>
        <td align="right">
          <#if ruleReport.getTotal().getNewIssuesCount() gt 0>
            <span class="worst">+${ruleReport.getTotal().getNewIssuesCount()?c}</span>
          <#else>
            <span>0</span>
          </#if>
        </td>
      </tr>
      </#if>
    </#list>
    <#if complete>
      <#assign colspan = '5'>
    <#else>
      <#assign colspan = '3'>
    </#if>
    <#assign issues=resourceReport.getIssuesAtLine(0, complete)>
      <#if issues?has_content>
      <tr class="globalIssues">
        <td colspan="${colspan}">
          <#list issues as issue>
            <div class="issue" id="${resourceReport_index?c}V${issueId?c}">
              <div class="vtitle">

                <img alt="${issue.severity()}" title="${issue.severity()}" src="issuesreport_files/${issue.severity()}.png">&nbsp;
                <img src="issuesreport_files/sep12.png"> <span class="rulename">${ruleNameProvider.name(issue.ruleKey())}</span>
                &nbsp;
                <img src="issuesreport_files/sep12.png">&nbsp;

                <span class="issue_date">
                  <#if issue.isNew()>
                    NEW
                    <#else>
                    ${issue.creationDate()?date}
                  </#if>
                </span>
              </div>
              <#if issue.message()??>
                <div class="discussionComment">
                ${issue.message()}
                </div>
              </#if>
            </div>
            <#assign issueId = issueId + 1>
          </#list>
        </td>
      </tr>
      </#if>
      <tr>
        <td colspan="${colspan}">
          <table class="sources" border="0" cellpadding="0" cellspacing="0">
            <#list sourceProvider.getEscapedSource(resourceReport.getResourceNode()) as line>
              <#assign lineIndex=line_index+1>
              <#if resourceReport.isDisplayableLine(lineIndex, complete)>
                <tr id="${resourceReport_index?c}L${lineIndex?c}" class="row">
                  <td class="lid ">${lineIndex?c}</td>
                  <td class="line ">
                    <pre>${line}</pre>
                  </td>
                </tr>
                <tr id="${resourceReport_index}S${lineIndex?c}" class="blockSep">
                  <td colspan="2"></td>
                </tr>
                <#assign issues=resourceReport.getIssuesAtLine(lineIndex, complete)>
                <#if issues?has_content>
                  <tr id="${resourceReport_index?c}LV${lineIndex?c}" class="row">
                    <td class="lid"></td>
                    <td class="issues">
                      <#list issues as issue>
                        <div class="issue" id="${resourceReport_index?c}V${issueId?c}">
                          <div class="vtitle">
                            <img alt="${issue.severity()}" title="${issue.severity()}" src="issuesreport_files/${issue.severity()}.png">&nbsp;
                            <img src="issuesreport_files/sep12.png">&nbsp;<span
                              class="rulename">${ruleNameProvider.name(issue.ruleKey())}</span>
                            &nbsp;
                            <img src="issuesreport_files/sep12.png">&nbsp;

                            <span class="issue_date">
                              <#if issue.isNew()>
                                NEW
                                <#else>
                                ${issue.creationDate()?date}
                              </#if>
                            </span>
                            &nbsp;

                          </div>
                          <#if issue.message()??>
                            <div class="discussionComment">
                            ${issue.message()}
                            </div>
                          </#if>
                        </div>
                        <#assign issueId = issueId + 1>
                      </#list>
                    </td>
                  </tr>
                </#if>
              </#if>
            </#list>
          </table>
        </td>
      </tr>
    </tbody>
  </table>
    </#if>
  </#list>
  </div>
</div>
<script type="text/javascript">
  $(function() {
    refreshFilters();
  });
</script>
</body>
</html>
