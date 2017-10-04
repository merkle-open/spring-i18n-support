<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ tag import="java.util.*, java.lang.*" %>

<%@ attribute name="error" required="false" type="java.lang.String" %>
<%@ attribute name="success" required="false" type="java.lang.String" %>
<%@ attribute name="errorCss" required="false" type="java.lang.String" %>
<%@ attribute name="successCss" required="false" type="java.lang.String" %>

<%-- ERROR OR SUCCESS MESSAGE IN CONTAINER --%>
<c:if test="${error != null && error!=''}">
	<div class="${errorCss}">
		<fmt:message key="${error}" />
	</div>
</c:if>
<c:if test="${success != null && success!=''}">
	<div class="${successCss}">
		<fmt:message key="${success}" />
	</div>
</c:if>