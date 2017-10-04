<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ tag import="java.util.*, java.lang.*,java.text.*" %>

<%@ attribute name="mainCss" required="false" type="java.lang.String" %>
<%@ attribute name="itemCss" required="false" type="java.lang.String" %>

<%@ attribute name="jqueryEmbedded" required="false" type="java.lang.String" %>
<%@ attribute name="cssEmbedded" required="false" type="java.lang.String" %>

<c:if test="${jqueryEmbedded == null}">
	<c:set var="jqueryEmbedded" value="false"/>
</c:if>
<c:if test="${cssEmbedded == null}">
	<c:set var="cssEmbedded" value="false"/>
</c:if>
<c:if test="${mainCss == null}">
	<c:set var="mainCss" value="msg_exp_ct"/>
</c:if>
<c:if test="${itemCss == null}">
	<c:set var="itemCss" value="msg_exp_item"/>
</c:if>

<%-- EXCEL FILE EXPORT LIST (links) --%>
<c:set var="timestamp"><%=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())%></c:set>
<div class="${mainCss}">
	<a class="${itemCss}" href="messages-${timestamp}.xls"><fmt:message key="messages.admin.export.excel97" /></a>
	<a class="${itemCss}" href="messages-${timestamp}.xlsx"><fmt:message key="messages.admin.export.excel2007" /></a>
	<a class="${itemCss}" href="messages-${timestamp}.sql"><fmt:message key="messages.admin.export.sql" /></a>
</div>

<%-- if configured, embed css/js --%>
<c:if test="${cssEmbedded}">
	<style type="text/css">
		<%@ include file="ci18n.css" %>
	</style>
</c:if>
<c:if test="${jqueryEmbedded}">
	<script type="text/javascript">
	//<![CDATA[
		<%@ include file="jquery.js" %>
	//]]>
	</script>
</c:if>