<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Issues report of ${report.getTitle()}</title>
  <link href="issuesreport_files/sonar.css" media="all" rel="stylesheet" type="text/css">
  <link rel="shortcut icon" type="image/x-icon" href="issuesreport_files/favicon.ico">
  <script type="text/javascript" src="issuesreport_files/prototypejs.js"></script>
  <script type="text/javascript">
    var violationsPerFile = [
    <#list report.getFileStatuses() as resourceStatus>
      [
        <#assign violations=resourceStatus.getViolations()>
        <#list violations as violation>
          {'i': ${violation_index?c}, 'r': 'R${violation.rule.id?c}', 'l': ${(violation.getLineId()!0)?c}, 'new': ${violation.isNew()?string}, 's': '${violation.getSeverity()}'}<#if violation_has_next>,</#if>
        </#list>
      ]
      <#if resourceStatus_has_next>,</#if>
    </#list>
    ];
    var nbFiles = ${report.getFileStatuses()?size};
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
           $$('div.violation').invoke('hide');
           for (var separatorIndex = 0; separatorIndex < separators.length; separatorIndex++) {
             separators[separatorIndex].removeClassName('visible');
           }
           separators.clear();
           $$('.sources td.ko').invoke('removeClassName', 'ko');
         }

         function showViolations(fileIndex, violations) {
           violations.each(function(violation) {
             $(fileIndex + 'V' + violation['i']).show();
             $$('#' + fileIndex + 'L' + violation['l'] + ' td.line').invoke('addClassName', 'ko');
           });
         }


    function refreshFilters() {
      var onlyNewViolations = $('new_filter').checked;
      var ruleFilter = Form.Element.getValue($('rule_filter'));

      hideAll();
      for (var fileIndex = 0; fileIndex < nbFiles; fileIndex++) {
        var filteredViolations = violationsPerFile[fileIndex].findAll(function(v) {
              return (!onlyNewViolations || v['new']) && (ruleFilter == '' || v['r'] == ruleFilter || v['s'] == ruleFilter);
            }
        );

        var linesToDisplay = filteredViolations.collect(function(v) {
          return v['l'];
        });

        linesToDisplay.sort();// the showLines() requires sorted ids
        showLines(fileIndex, linesToDisplay);
        showViolations(fileIndex, filteredViolations);
      }
    }
  </script>
</head>
<body>
<div id="reportHeader">
  <div id="logo"><img src="issuesreport_files/sonar.png" alt="Sonar"/></div>
  <div class="title">Issues Report</div>
  <div class="subtitle">${report.getTitle()} - ${report.getDate()?datetime}</div>
</div>

<div id="content">

  <div id="rules">
  <#list report.getStatuses() as resourceStatus>
  <table width="100%" class="data">
    <thead>
    <tr>
      <th colspan="2" align="left"></th>
      <th align="right" width="1%" nowrap>Current Analysis</th>
      <th align="right" width="1%" nowrap>New violations</th>
    </tr>
    <tr class="total">
      <th colspan="2" align="left">
        <#if resourceStatus.getResource()??>
          <a href="#${resourceStatus_index-1}">${resourceStatus.getName()}</a>
          <#else>
          Total Number of Violations
        </#if>
      </th>
      <th align="right">
        <span id="val-${resourceStatus.getKey()}">${resourceStatus.getValue()?c}</span>
      </th>
      <th align="right">
        <#if resourceStatus.getVariation() gt 0>
          <span class="worst" id="var-${resourceStatus.getKey()}">+${resourceStatus.getVariation()?c}</span>
          <#elseif resourceStatus.getVariation() == 0>
            <span id="var-${resourceStatus.getKey()}">0</span>
          <#else>
            <span class="better" id="var-${resourceStatus.getKey()}">${resourceStatus.getVariation()?c}</span>
        </#if>
      </th>
    </tr>
    </thead>
    <tbody>
      <#list resourceStatus.getRuleStatuses() as ruleStatus>
        <#assign trCss = (ruleStatus_index % 2 == 0)?string("even","odd")>
      <tr class="${trCss}">
        <td width="20">
          <img alt="${ruleStatus.getSeverity()}" title="${ruleStatus.getSeverity()}" src="issuesreport_files/${ruleStatus.getSeverity()}.png">
        </td>
        <td align="left">
          ${ruleNames.name(ruleStatus.getRule())}
        </td>
        <td align="right">
          ${ruleStatus.getValue()?c}
        </td>
        <td align="right">
          <#if ruleStatus.getVariation() gt 0>
            <span class="worst">+${ruleStatus.getVariation()?c}</span>
            <#elseif ruleStatus.getVariation() == 0>
              0
            <#else>
              <span class="better">${ruleStatus.getVariation()?c}</span>
          </#if>
        </td>
      </tr>
      </#list>
    </tbody>
  </#list>
  </table>
  </div>

  <hr/>

  <div id="filters">
    <input type="checkbox" id="new_filter" onclick="refreshFilters()" checked="checked" /> <label for="new_filter">Only NEW
    violations</label>
    &nbsp;&nbsp;&nbsp;&nbsp;
    <select id="rule_filter" onchange="refreshFilters()">
      <option value="" selected>Filter by:</option>
      <optgroup label="Severity">
        <option value="BLOCKER">Blocker</option>
        <option value="CRITICAL">Critical</option>
        <option value="MAJOR">Major</option>
        <option value="MINOR">Minor</option>
        <option value="INFO">Info</option>
      </optgroup>
      <optgroup label="Rule">
      <#list report.getTotal().getRuleStatuses() as ruleStatus>
        <option value="R${ruleStatus.getRule().getId()?c}">
          ${ruleNames.name(ruleStatus.getRule())}
          (${ruleStatus.getValue()?c})
        </option>
      </#list>
      </optgroup>
    </select>
  </div>

  <div>
  <#list report.getFileStatuses() as resourceStatus>
    <#assign violationId=0>
    <a name="${resourceStatus_index?c}"></a>
    <div id="file${resourceStatus_index?c}">
      <div class="file_title">
        <img src="issuesreport_files/CLA.png" title="Class"> ${resourceStatus.getName()}
      </div>
      <#assign violations=resourceStatus.getViolations(0)>
      <#if violations?has_content>
        <table cellpadding="0" cellspacing="0" class="globalViolations">
          <tbody>
          <tr>
            <td>
              <#list violations as violation>
                <div class="violation" id="${resourceStatus_index?c}V${violationId?c}">
                  <div class="vtitle">

                    <img alt="${violation.getSeverity()}" title="${violation.getSeverity()}" src="issuesreport_files/${violation.getSeverity()}.png">&nbsp;
                    <img src="issuesreport_files/sep12.png"> <span class="rulename">${ruleNames.name(violation.getRule())}</span>
                    &nbsp;
                    <img src="issuesreport_files/sep12.png">&nbsp;

                    <span class="violation_date">
                      <#if violation.isNew()>
                        NEW
                        <#else>
                        ${violation.createdAt?date}
                      </#if>
                    </span>
                  </div>
                  <#if violation.message??>
                    <div class="discussionComment">
                    ${violation.message}
                    </div>
                  </#if>
                </div>
                <#assign violationId = violationId + 1>
              </#list>
            </td>
          </tr>
          </tbody>
        </table>
      </#if>
      <table class="sources" border="0" cellpadding="0" cellspacing="0">
        <#list resourceStatus.getLines() as line>
          <#assign lineIndex=line_index+1>
          <#if resourceStatus.isDisplayableLine(lineIndex)>
            <tr id="${resourceStatus_index?c}L${lineIndex?c}" class="row">
              <td class="lid ">${lineIndex?c}</td>
              <td class="line ">
                <pre>${line}</pre>
              </td>
            </tr>
            <tr id="${resourceStatus_index}S${lineIndex?c}" class="blockSep">
              <td colspan="2"></td>
            </tr>
            <#assign violations=resourceStatus.getViolations(lineIndex)>
            <#if violations?has_content>
              <tr id="${resourceStatus_index?c}LV${lineIndex?c}" class="row">
                <td class="lid"></td>
                <td class="violations">
                  <#list violations as violation>
                    <div class="violation" id="${resourceStatus_index?c}V${violationId?c}">
                      <div class="vtitle">
                        <img alt="${violation.getSeverity()}" title="${violation.getSeverity()}" src="issuesreport_files/${violation.getSeverity()}.png">&nbsp;
                        <img src="issuesreport_files/sep12.png">&nbsp;<span
                          class="rulename">${ruleNames.name(violation.getRule())}</span>
                        &nbsp;
                        <img src="issuesreport_files/sep12.png">&nbsp;

                        <span class="violation_date">
                          <#if violation.isNew()>
                            NEW
                            <#else>
                            ${violation.createdAt?date}
                          </#if>
                        </span>
                        &nbsp;

                      </div>
                      <#if violation.message??>
                        <div class="discussionComment">
                        ${violation.message}
                        </div>
                      </#if>
                    </div>
                    <#assign violationId = violationId + 1>
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
