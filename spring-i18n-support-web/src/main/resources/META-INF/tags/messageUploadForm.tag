<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ tag import="java.util.*, java.lang.*" %>

<%@ attribute name="action" required="false" type="java.lang.String" %>
<%@ attribute name="containerCss" required="false" type="java.lang.String" %>
<%@ attribute name="titleCss" required="false" type="java.lang.String" %>
<%@ attribute name="descriptionCss" required="false" type="java.lang.String" %>
<%@ attribute name="inputCss" required="false" type="java.lang.String" %>
<%@ attribute name="labelCss" required="false" type="java.lang.String" %>
<%@ attribute name="buttonCss" required="false" type="java.lang.String" %>
<%@ attribute name="cancelCss" required="false" type="java.lang.String" %>

<c:if test="${action == null}">
	<c:set var="action" value="uploadFile.html"/>
</c:if>

<%-- UPLOAD INPUT FILE --%>
<div class="${containerCss}">
	<form method="post" action="${action}" enctype="multipart/form-data">
		<c:if test="${_csrf != null}">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		</c:if>
		<div><label class="${labelCss}" for="msgFile">${fileLabel}</label><input id="msgFile" type="file" name="file" class="${inputCss}" /></div>
		<div>
			<button type="submit" class="${buttonCss}"><fmt:message key="messages.admin.upload.submit" /></button>
			<%--obsolete? <button class="${cancelCss}" onclick="window.location='index.htm';return false;" name="cancel" id="cancel"><fmt:message key="messages.admin.form.cancel" /></button>--%>
		</div>
	</form>
</div>