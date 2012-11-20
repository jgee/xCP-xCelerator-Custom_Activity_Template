<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!--
  ~ Copyright (c) 2010-2011. EMC Corporation.  All Rights Reserved.
  -->

<%@ page session="false" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.TreeSet" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%!
        //ExtJs seems to have some language pack specific to country, below is the list of it.
        private static final Set extCountryLangPacks = new TreeSet();
        static
        {
            extCountryLangPacks.add("zh_TW");
            extCountryLangPacks.add("zh_CN");
            extCountryLangPacks.add("sv_SE");
            extCountryLangPacks.add("sr_RS");
            extCountryLangPacks.add("pt_PT");
            extCountryLangPacks.add("pt_BR");
            extCountryLangPacks.add("no_NN");
            extCountryLangPacks.add("no_NB");
            extCountryLangPacks.add("fr_CA");
            extCountryLangPacks.add("en_GB");
            extCountryLangPacks.add("el_GR");
        };
%>
<%
    Locale clientLocale = request.getLocale();
    String lang = clientLocale.getLanguage();
    String country = clientLocale.getCountry();
    String extLangFileSuffix = lang;
    if (country != null && country.length() > 0) {
        String str = lang+"_"+country;
        if (extCountryLangPacks.contains(str)) {
            extLangFileSuffix = str;
        }
    }
    //TODO we might need to handle ext lang packs which have country suffix as well.
%>

<spring:eval expression="@application['version']" var="applicationVersion"/>
<spring:url value="/resources-{applicationVersion}" var="resourceUrl">
	<spring:param name="applicationVersion" value="${applicationVersion}"/>
</spring:url>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta http-equiv="Cache-Control" content="no-cache" />

        <link rel="stylesheet" type="text/css" href="${resourceUrl}/js/resources/css/xcp-default.css" />
        <link rel="stylesheet" type="text/css" href="component/xcp-core/xcp_signin/contents-${applicationVersion}.css"/>

        <script type="text/javascript" src="${resourceUrl}/js/ext-4.1/ext-all.js"></script>
        <script type="text/javascript" src="${resourceUrl}/js/ext-4.1/locale/ext-lang-<%=extLangFileSuffix%>.js"></script>
        <script type="text/javascript" src="component/xcp-core/xcp_signin/contents-${applicationVersion}.js"></script>
        <script type="text/javascript" src="${resourceUrl}/js/AppConfiguration.js"></script>
        <%-- temporary change for window title --%>
        <script type="text/javascript">
            document.title = xcp.appContext.name;
            xcp.appContext.version='${applicationVersion}';
            xcp.componentVersion = '${applicationVersion}';
        </script>
        <script type="text/javascript">
            try {
                history.go(+1);
            } catch (Error) {
                //Ignore
            }
            Ext.onReady(function() {
                Ext.Ajax.request({
                    url: "ui/pages/application/signin",
                    async: false,
                    disableCaching : false,
                    success: function(response, options) {
                        var signInPanelConfig = Ext.JSON.decode(response.responseText);
                        var signInPanel = Ext.ComponentMgr.create(signInPanelConfig);
                        signInPanel.render("signin-area");
                        xcp.util.SignInUtil.initUI(signInPanel);
                    },
                    failure: function(response, options) {
                        throw "Error while retrieving Sign In dialog.";
                    }
                });
            });
        </script>
    </head>
	<body id="signin-page">
        <div id="msg-area" class="msg-area-div"></div>
        <form method="post">
            <div id="signin-area" class="signin-area-div"></div>
        </form>
    </body>
</html>

