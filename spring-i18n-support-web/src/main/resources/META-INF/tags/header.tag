<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ tag import="java.util.*,java.lang.*" %>

<%@ attribute name="jquery" required="false" type="java.lang.Boolean" %>
<%@ attribute name="styles" required="false" type="java.lang.Boolean" %>
<%@ attribute name="jqueryTablesorter" required="false" type="java.lang.Boolean" %>

<c:if test="${jquery == null}">
	<c:set var="jquery" value="true"/>
</c:if>
<c:if test="${styles == null}">
	<c:set var="styles" value="true"/>
</c:if>
<c:if test="${jqueryTablesorter == null}">
	<c:set var="jqueryTablesorter" value="false"/>
</c:if>

<%-- if configured, embed css/js --%>
<c:if test="${styles}">
	<style type="text/css">
		<%@ include file="ci18n.css" %>
	</style>
</c:if>
<c:if test="${jquery}">
	<script type="text/javascript">
	//<![CDATA[
		<%@ include file="jquery.js" %>
	//]]>
	</script>
</c:if>
<c:if test="${jqueryTablesorter}">
	<script type="text/javascript">
	//<![CDATA[
		<%@ include file="jquery-ts.js" %>
	//]]>
	</script>
</c:if>