<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!--
  ~ Copyright (c) 2010-2011. EMC Corporation.  All Rights Reserved.
  -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page session="false" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%!
        private static final String COMPONENT_LOAD_MODE="mode";
        private static final String LOGGING_MODE = "logging";
        private static final String NOMINIFY="nominify";

        //ExtJs seems to have some language pack specific to country, below is the list of it.
        private static final TreeMap<String, String> extCountryLangPacks = new TreeMap<String, String>();
        private static final Set ckEditorCountryLangPacks = new TreeSet();
        static
        {
            extCountryLangPacks.put("zh_TW","zh_TW");
            extCountryLangPacks.put("zh_CN","zh_CN");
            extCountryLangPacks.put("sv_SE","sv_SE");
            extCountryLangPacks.put("sr_RS","sr_RS");
            extCountryLangPacks.put("pt_PT","pt_PT");
            extCountryLangPacks.put("pt_BR","pt_BR");
            extCountryLangPacks.put("no_NN","no_NN");
            extCountryLangPacks.put("no_NB","no_NB");
            extCountryLangPacks.put("fr_CA","fr_CA");
            extCountryLangPacks.put("el_GR","el_GR");
            extCountryLangPacks.put("en_GB","en_GB");
            extCountryLangPacks.put("zh","zh_CN");
            extCountryLangPacks.put("no","no_NB");
            extCountryLangPacks.put("el","el_GR");
            extCountryLangPacks.put("sv","sv_SE");

            ckEditorCountryLangPacks.add("en-au");
            ckEditorCountryLangPacks.add("en-ca");
            ckEditorCountryLangPacks.add("en-gb");
            ckEditorCountryLangPacks.add("fr-ca");
            ckEditorCountryLangPacks.add("pt-br");
            ckEditorCountryLangPacks.add("sr-latn");
            ckEditorCountryLangPacks.add("zh-cn");
        };

        /**
         * Get the date pattern for given date style and locale.
         * @param type Date style
         * @param loc Client Locale
         */
        private String getDatePattern(int type, Locale loc) {
            SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(type, loc);
            return changeToExtPattern(format.toPattern());
        }
        /**
         * Get the time pattern for given time style and locale.
         * @param type time style
         * @param loc Client Locale
         */
        private String getTimePattern(int type, Locale loc) {
            SimpleDateFormat format = (SimpleDateFormat) DateFormat.getTimeInstance(type, loc);
            return changeToExtPattern(format.toPattern());
        }

        /**
         * Need to change the Java date formats into Extjs Date formats.
         *
         * @param pattern Pattern from Java
         * @return Pattern in ExtJs format
         */
        private String changeToExtPattern(String pattern) {
            pattern = pattern.replaceAll("de", "\\\\\\\\de");   // KWC-5484 (Spanish "de" needs escaping)
            pattern = pattern.replaceAll("dd", "@@");
            //XCPUIC-446 - Commented the following line, not sure whether this is required for other locales
            //if so, need to skip this for spanish locale.
            //pattern = pattern.replaceAll("d", "j");
            pattern = pattern.replaceAll("EE", "D");
            pattern = pattern.replaceAll("MMMM", "F");
            pattern = pattern.replaceAll("MMM", "###");
            pattern = pattern.replaceAll("MM", "m");
            pattern = pattern.replaceAll("M", "n");
            pattern = pattern.replaceAll("yyyy", "Y");
            pattern = pattern.replaceAll("yy", "y");    // Always 4-digit year

            pattern = pattern.replaceAll("hh", "g");
            pattern = pattern.replaceAll("HH", "H");
            pattern = pattern.replaceAll("mm", "i");
            pattern = pattern.replaceAll("ss", "s");
            pattern = pattern.replaceAll(" ss'.'", ""); // No seconds (Korean-format)
            pattern = pattern.replaceAll("z", "T");

            pattern = pattern.replaceAll("'", "");      // remove text quoting - not needed by Extjs
            pattern = pattern.replaceAll("@@", "d");
            pattern = pattern.replaceAll("###", "M");

            return pattern;
        }
        
        /**
         * given a list of timezone names, add timezones to a map keyed by tz offsets at 4 specific dates.
         * this will enable javascript code to look up timezone name for the user timezone by testing for
         * the same offsets.
         *
         * @param timeZoneMap map to populate
         * @param ids the list of dates to add to the map.
         */
        private void populateTimezoneMap(Map<String, TimeZone> timeZoneMap, String[] ids) {
            Date spring = new Date(2010,3,21,12,0);
            Date summer = new Date(2010,6,21,12,0);
            Date fall = new Date(2010,9,21,12,0);
            Date winter = new Date(2010,12,21,12,0);
            final String KEY_FORMAT = "%d:%d:%d:%d";
            for(String tzId: ids) {
                TimeZone tz = TimeZone.getTimeZone(tzId);
                String key =  String.format(KEY_FORMAT, tz.getOffset(spring.getTime())/60000,tz.getOffset(summer.getTime())/60000,tz.getOffset(fall.getTime())/60000,tz.getOffset(winter.getTime())/60000);
                if (!timeZoneMap.containsKey(key))
                    timeZoneMap.put(key, tz);
            }
        }
        
        /**
         * given a list of timezone names, add timezones to a map keyed by tz offsets at 4 specific dates.
         * this will enable javascript code to look up timezone name for the user timezone by testing for
         * the same offsets.
         *
         * @param clientLocale locale from http request
         * @return a JSON string containing timezone information keyed by a string with GMT offsets in
         * seconds on 4 different dates
         */
        private String getTimezoneTableJSON(Locale clientLocale) {
            final String[] COMMON_TIMEZONES = {
                    "US/Hawaii",
                    "US/Alaska",
                    "US/Pacific",
                    "US/Mountain",
                    "US/Central",
                    "US/Eastern",
                    "Canada/Atlantic",
                    "Canada/Newfoundland",
                    "America/Argentina/San_Juan",
                    "Europe/London",
                    "UTC",
                    "Europe/Paris",
                    "Africa/Cairo",
                    "Europe/Moscow",
                    "IST",
                    "Asia/Bangkok",
                    "Asia/Shanghai",
                    "Asia/Tokyo",
                    "Pacific/Auckland"};
            Map<String, TimeZone> timeZoneMap = new HashMap<String, TimeZone>();
            
            // first populate the map with the most common time zones, then fill it out with the entire
            // list.  This ensures that we don't pick strange time zone names that happen to match common ones.
            populateTimezoneMap(timeZoneMap, COMMON_TIMEZONES);
            populateTimezoneMap(timeZoneMap, TimeZone.getAvailableIDs());
            
            StringBuilder tzJSON = new StringBuilder();
            tzJSON.append("{");
            boolean first = true;
    		for(String key: timeZoneMap.keySet()) {
    			TimeZone tz = timeZoneMap.get(key);
    			if (!first) { 
    				tzJSON.append(",\n"); 
    			}
    			tzJSON.append(String.format("\"%s\": {\"id\":\"%s\",\"abbr\":\"%s\",\"dstAbbr\":\"%s\"}",
    					key, tz.getID(), tz.getDisplayName(false, TimeZone.SHORT, clientLocale), tz.getDisplayName(true, TimeZone.SHORT, clientLocale)));
    			first = false;
    		}
    		tzJSON.append("}");
			return tzJSON.toString();
        }

%>
<%
    String componentLoadMode = request.getParameter(COMPONENT_LOAD_MODE);
    if (componentLoadMode == null || componentLoadMode.length() == 0) {
        Cookie cookies[] = request.getCookies();
        if (cookies != null && cookies.length > 0 ) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(COMPONENT_LOAD_MODE)) {
                    componentLoadMode = cookie.getValue();
                    break;
                }
            }
        }
    }
    if (componentLoadMode == null) {
        componentLoadMode = "production";
        //componentLoadMode = "development";
        //componentLoadMode = "ondemand";

    }

    boolean nominify = false;
    if (request.getParameterMap().containsKey(NOMINIFY)) {
        nominify = true;
    }

    String extJSMode = "";
    if (!componentLoadMode.equals("production")) {
        extJSMode="-debug";
    }

    String loggingMode = request.getParameter(LOGGING_MODE);
    if(loggingMode == null || loggingMode.length() == 0)
    {
        loggingMode = "false";
    }

    Locale clientLocale = request.getLocale();
    String lang = clientLocale.getLanguage();
    String country = clientLocale.getCountry();

    String extLangFileSuffix = lang;
    String ckEditorLanguage = lang;
    if (country != null && country.length() > 0) {
        lang = lang+"_"+country;
        String str = lang + "-" + country.toLowerCase();
        if (ckEditorCountryLangPacks.contains(str)) {
            ckEditorLanguage = str;
        }
    }

    if (ckEditorLanguage.equals("zh"))
        ckEditorLanguage = "zh-cn";  // XCPECM-222

    if (extCountryLangPacks.containsKey(lang)) {
        extLangFileSuffix = extCountryLangPacks.get(lang);
    }
	String tzTableJson = getTimezoneTableJSON(clientLocale);
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
		<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8" >

        <% if(loggingMode.equals("true")) { %>
            <script type="text/javascript" src="${resourceUrl}/js/firebug-lite/dist/xcp-firebug-lite.js"></script>
        <% } %>

        <% if (nominify) { %>
            <script type="text/javascript" src="${resourceUrl}/js/ext-4.1/ext-all-dev.js"></script>
        <% } else {  %>
            <script type="text/javascript" src="${resourceUrl}/js/ext-4.1/ext-all.js"></script>
        <% } %>

        <% if (extJSMode.equals("-debug")) { %>
            <script type="text/javascript" src="${resourceUrl}/js/ext-4.1/src/diag/layout/Context.js"></script>
            <script type="text/javascript" src="${resourceUrl}/js/ext-4.1/src/diag/layout/ContextItem.js"></script>
        <% } %>

        <script type="text/javascript" src="${resourceUrl}/js/ext-4.1/locale/ext-lang-<%=extLangFileSuffix%>.js"></script>
        <script type="text/javascript" src="component/xcp-core/openajax/lib/openajax/OpenAjax.js"></script>
        <script type="text/javascript" src="${resourceUrl}/js/AppConfiguration.js"></script>
        <%-- temporary change for window title --%>
        <script type="text/javascript">
            document.title = xcp.appContext.name;
        </script>
        <script type="text/javascript">
            //Check if the application version properties is missing
            if (!xcp.appContext.version) {
                //Set application version from the server
                xcp.appContext.version='${applicationVersion}';
            }
            if (!xcp.componentVersion) {
                xcp.componentVersion = '${applicationVersion}';
            }
            xcp.ckeditorlanguage = "<%=ckEditorLanguage%>";
            xcp.extLangFileSuffix = "<%=extLangFileSuffix%>";
            Ext.namespace("xcp.Formats");
            Ext.apply(xcp.Formats, {
                dateFormats : {
                    short:       "<%=getDatePattern(DateFormat.SHORT, clientLocale)%>",
                    medium:      "<%=getDatePattern(DateFormat.MEDIUM, clientLocale)%>",
                    long:        "<%=getDatePattern(DateFormat.LONG, clientLocale)%>"
                },
                timeFormats : {
                    short:       "<%=getTimePattern(DateFormat.SHORT, clientLocale)%>",
                    medium:      "<%=getTimePattern(DateFormat.MEDIUM, clientLocale)%>",
                    long:        "<%=getTimePattern(DateFormat.LONG, clientLocale)%>"
                },
				timezoneAbbreviation : "UTC",
				timezoneDstAbbreviation : "UTC",
				timezoneId : "UTC"
            });

            // calculate timezone constants for the current user.  Use function closure to
            // avoid cluttering the global namespace
            (function() {
            	var tzTab = <%=tzTableJson%>;
                var springDate = new Date(2012,3,21,12,0,0);
                var summerDate = new Date(2012,6,21,12,0,0);
                var fallDate = new Date(2012,9,21,12,0,0);
                var winterDate = new Date(2012,12,21,12,0,0);
                var key = "" +
                        springDate.getTimezoneOffset() * -1 + ":" +
                        summerDate.getTimezoneOffset() * -1 + ":" +
                        fallDate.getTimezoneOffset() * -1 + ":" +
                        winterDate.getTimezoneOffset() * -1;
                if (key in tzTab) {
                	xcp.Formats.timezoneId = tzTab[key].id;
                	xcp.Formats.timezoneAbbreviation = tzTab[key].abbr;
                	xcp.Formats.timezoneDstAbbreviation = tzTab[key].dstAbbr;
                }
            })();
        </script>

        <% if (nominify) { %>
            <script id="xcp_startup" type="text/javascript" src="component/xcp-core/xcp_startup/contents-xcp_startup-${applicationVersion}.js?nominify=true"></script>
        <% } else {  %>
            <script id="xcp_startup" type="text/javascript" src="component/xcp-core/xcp_startup/contents-xcp_startup-${applicationVersion}.js"></script>
        <% } %>

         <script type="text/javascript">

            Ext.onReady(function() {
				xcp.Startup.start({
                    componentLoadMode : '<%=componentLoadMode%>',
                    nominify: <%=nominify%>
                });
                if(console && console.firebuglite)
                {
                    xcp.Logger.fireEvent("enablelogging");
                }
			});

        </script>
    </head>
    <body>


    </body>
</html>
