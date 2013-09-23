<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Issues report of ${report.getTitle()}</title>
  <link href="issuesreport_files/sonar.css" media="all" rel="stylesheet" type="text/css">
  <link rel="shortcut icon" type="image/x-icon" href="issuesreport_files/favicon.ico">
  <script type="text/javascript" src="issuesreport_files/prototypejs.js"></script>
  <script type="text/javascript">
    var issuesPerResource = [
    <#list report.getResourceReports() as resourceReport>
      [
        <#assign issues=resourceReport.getIssues()>
        <#list issues as issue>
          {'i': ${issue_index?c}, 'r': 'R${issue.ruleKey()}', 'l': ${(issue.line()!0)?c}, 'new': ${issue.isNew()?string}, 's': '${issue.severity()}'}<#if issue_has_next>,</#if>
        </#list>
      ]
      <#if resourceReport_has_next>,</#if>
    </#list>
    ];
    var nbResources = ${report.getResourcesWithReport()?size};
    var separators = new Array();

    function showLine(fileIndex, lineId) {
      var elt = $(fileIndex + 'L' + lineId);
      if (elt != null) {
        elt.show();
      }
      elt = $(fileIndex + 'LV' + lineId);
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
              separator.addClassName('visible');
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
       $$('tr.row').invoke('hide');
       $$('div.issue').invoke('hide');
       for (var separatorIndex = 0; separatorIndex < separators.length; separatorIndex++) {
         separators[separatorIndex].removeClassName('visible');
       }
       separators.clear();
       $$('.sources td.ko').invoke('removeClassName', 'ko');
     }

     function showIssues(fileIndex, issues) {
       issues.each(function(issue) {
         $(fileIndex + 'V' + issue['i']).show();
         $$('#' + fileIndex + 'L' + issue['l'] + ' td.line').invoke('addClassName', 'ko');
       });
     }


    function refreshFilters() {
      var onlyNewIssues = $('new_filter').checked;
      var ruleFilter = Form.Element.getValue($('rule_filter'));

      hideAll();
      for (var resourceIndex = 0; resourceIndex < nbResources; resourceIndex++) {
        var filteredIssues = issuesPerResource[resourceIndex].findAll(function(v) {
              return (!onlyNewIssues || v['new']) && (ruleFilter == '' || v['r'] == ruleFilter || v['s'] == ruleFilter);
            }
        );

        var linesToDisplay = filteredIssues.collect(function(v) {
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

  <div id="summary">
  <table width="100%" class="data">
    <thead>
    <tr>
      <th colspan="2" align="left"></th>
      <th align="right" width="1%" nowrap>Current Analysis</th>
      <th align="right" width="1%" nowrap>Resolved issues</th>
      <th align="right" width="1%" nowrap>New issues</th>
    </tr>
    <tr class="total">
      <th colspan="2" align="left">
          Total Number of Issues
      </th>
      <th align="right">
        <span id="current-total">${report.getSummary().getTotal().getCountInCurrentAnalysis()?c}</span>
      </th>
      <th align="right">
        <#if report.getSummary().getTotal().getResolvedIssuesCount() gt 0>
          <span class="better" id="resolved-total">-${report.getSummary().getTotal().getResolvedIssuesCount()?c}</span>
        <#else>
          <span id="resolved-total">0</span>
        </#if>
      </th>
      <th align="right">
        <#if report.getSummary().getTotal().getNewIssuesCount() gt 0>
          <span class="worst" id="new-total">+${report.getSummary().getTotal().getNewIssuesCount()?c}</span>
        <#else>
          <span id="new-total">0</span>
        </#if>
      </th>
    </tr>
    </thead>
    <tbody>
      <#list report.getSummary().getRuleReports() as ruleReport>
        <#assign trCss = (ruleReport_index % 2 == 0)?string("even","odd")>
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
      </#list>
    </tbody>
  </table>
  </div>

  <div id="summary-per-file">
  <#list report.getResourceReports() as resourceReport>
  <table width="100%" class="data">
    <thead>
    <tr>
      <th colspan="2" align="left"></th>
      <th align="right" width="1%" nowrap>Current Analysis</th>
      <th align="right" width="1%" nowrap>Resolved issues</th>
      <th align="right" width="1%" nowrap>New issues</th>
    </tr>
    <tr class="total">
      <th colspan="2" align="left">
          <a href="#${resourceReport_index-1}">${resourceReport.getName()}</a>
      </th>
      <th align="right">
        <span id="current-total">${resourceReport.getTotal().getCountInCurrentAnalysis()?c}</span>
      </th>
      <th align="right">
        <#if resourceReport.getTotal().getResolvedIssuesCount() gt 0>
          <span class="better" id="resolved-total">-${resourceReport.getTotal().getResolvedIssuesCount()?c}</span>
        <#else>
          <span id="resolved-total">0</span>
        </#if>
      </th>
      <th align="right">
        <#if resourceReport.getTotal().getNewIssuesCount() gt 0>
          <span class="worst" id="new-total">+${resourceReport.getTotal().getNewIssuesCount()?c}</span>
        <#else>
          <span id="new-total">0</span>
        </#if>
      </th>
    </tr>
    </thead>
  </table>
  </#list>
  </div>
  <hr/>

  <div id="filters">
    <input type="checkbox" id="new_filter" onclick="refreshFilters()" checked="checked" /> <label for="new_filter">Only NEW
    issues</label>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <select id="rule_filter" onchange="refreshFilters()">
      <option value="" selected>Filter by:</option>
      <optgroup label="Severity">
      <#assign severities = report.getSummary().getTotalBySeverity()>
      <#list severities?keys as severity>
        <option value="${severity}">
          ${severity?lower_case?cap_first}
          (${severities[severity].getCountInCurrentAnalysis()?c})
        </option>
      </#list>
      </optgroup>
      <optgroup label="Rule">
      <#assign rules = report.getSummary().getCountByRuleKey()>
      <#list rules?keys as ruleKey>
        <option value="R${ruleKey}">
          ${ruleNameProvider.name(ruleKey)}
          (${rules[ruleKey]?c})
        </option>
      </#list>
      </optgroup>
    </select>
  </div>

  <div>
  <#list report.getResourceReports() as resourceReport>
    <#assign issueId=0>
    <a name="${resourceReport_index?c}"></a>
    <div id="file${resourceReport_index?c}">
      <div class="file_title">
        <img src="issuesreport_files/CLA.png" title="Class"> ${resourceReport.getName()}
      </div>
      <#assign issues=resourceReport.getIssuesAtLine(0)>
      <#if issues?has_content>
        <table cellpadding="0" cellspacing="0" class="globalIssues">
          <tbody>
          <tr>
            <td>
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
          </tbody>
        </table>
      </#if>
      <table class="sources" border="0" cellpadding="0" cellspacing="0">
        <#list sourceProvider.getEscapedSource(resourceReport.getResourceNode()) as line>
          <#assign lineIndex=line_index+1>
          <#if resourceReport.isDisplayableLine(lineIndex)>
            <tr id="${resourceReport_index?c}L${lineIndex?c}" class="row">
              <td class="lid ">${lineIndex?c}</td>
              <td class="line ">
                <pre>${line}</pre>
              </td>
            </tr>
            <tr id="${resourceReport_index}S${lineIndex?c}" class="blockSep">
              <td colspan="2"></td>
            </tr>
            <#assign issues=resourceReport.getIssuesAtLine(lineIndex)>
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
    </div>
  </#list>
  </div>
</div>
<script type="text/javascript">refreshFilters();</script>
</body>
</html>
