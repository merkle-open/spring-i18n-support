<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ tag import="java.util.*, java.lang.*,com.namics.oss.spring.support.i18n.model.MessageResourceEntry" %>

<%@ attribute name="resource" required="false" type="com.namics.oss.spring.support.i18n.model.MessageResourceEntry" %>
<%@ attribute name="action" required="false" type="java.lang.String" %>

<%@ attribute name="jqueryEmbedded" required="false" type="java.lang.String" %>
<%@ attribute name="cssEmbedded" required="false" type="java.lang.String" %>

<%@ attribute name="errorCss" required="false" type="java.lang.String" %>
<%@ attribute name="containerCss" required="false" type="java.lang.String" %>
<%@ attribute name="codeInputCss" required="false" type="java.lang.String" %>
<%@ attribute name="codeLabelCss" required="false" type="java.lang.String" %>
<%@ attribute name="codeContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="langInputCss" required="false" type="java.lang.String" %>
<%@ attribute name="langLabelCss" required="false" type="java.lang.String" %>
<%@ attribute name="langContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="messageInputCss" required="false" type="java.lang.String" %>
<%@ attribute name="messageLabelCss" required="false" type="java.lang.String" %>
<%@ attribute name="submitCss" required="false" type="java.lang.String" %>
<%@ attribute name="cancelCss" required="false" type="java.lang.String" %>
<%@ attribute name="messagesContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="buttonsContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="newLangCss" required="false" type="java.lang.String" %>

<c:if test="${jqueryEmbedded == null}">
	<c:set var="jqueryEmbedded" value="false"/>
</c:if>
<c:if test="${cssEmbedded == null}">
	<c:set var="cssEmbedded" value="false"/>
</c:if>
<c:if test="${action == null}">
	<c:set var="action" value="delete.html"/>
</c:if>
<c:if test="${containerCss == null}">
	<c:set var="containerCss" value="msg_ct"/>
</c:if>
<c:if test="${codeInputCss == null}">
	<c:set var="codeInputCss" value="msg_i_code"/>
</c:if>
<c:if test="${codeLabelCss == null}">
	<c:set var="codeLabelCss" value="msg_l_code"/>
</c:if>
<c:if test="${codeContainerCss == null}">
	<c:set var="codeContainerCss" value="msg_ct_code"/>
</c:if>
<c:if test="${langInputCss == null}">
	<c:set var="langInputCss" value="msg_i_lang"/>
</c:if>
<c:if test="${langLabelCss == null}">
	<c:set var="langLabelCss" value="msg_l_lang"/>
</c:if>
<c:if test="${langContainerCss == null}">
	<c:set var="langContainerCss" value="msg_ct_lang"/>
</c:if>
<c:if test="${messagesContainerCss == null}">
	<c:set var="messagesContainerCss" value="msg_ct_msgs"/>
</c:if>
<c:if test="${messageInputCss == null}">
	<c:set var="messageInputCss" value="msg_i_msg"/>
</c:if>
<c:if test="${messageLabelCss == null}">
	<c:set var="messageLabelCss" value="msg_l_msg"/>
</c:if>
<c:if test="${buttonsContainerCss == null}">
	<c:set var="buttonsContainerCss" value="msg_ct_btn"/>
</c:if>
<c:if test="${submitCss == null}">
	<c:set var="submitCss" value="msg_btn_submit"/>
</c:if>
<c:if test="${cancelCss == null}">
	<c:set var="cancelCss" value="msg_btn_cancel"/>
</c:if>
<c:if test="${errorCss == null}">
	<c:set var="errorCss" value="msg_err"/>
</c:if>

<c:if test="${newLangCss == null}">
	<c:set var="newLangCss" value="msg_btn_newlang"/>
</c:if>

<%-- DELETE MASK --%>
<div class="${containerCss}">
	<form method="post" action="${action}" id="msgConfirmForm" name="msgConfirmForm">
		<c:if test="${_csrf != null}">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		</c:if>
		<c:set var="code" value=""/>
		<c:set var="isEdit" value="false"/>
		<c:set var="disabled" value=""/>
		<c:if test="${resource != null && resource.codeId != null}">
			<c:set var="code" value="${resource.codeId}"/>
		</c:if>

		<%-- message code currently being deleted --%>
		<div class="${codeContainerCss}">
			<label class="${codeLabelCss}" for="codeId"><fmt:message key="messages.admin.form.code" /></label><input id="codeId" type="text" name="codeId" class="${codeInputCss}" value="<c:out value="${code}" escapeXml="true" />" disabled="disabled" />
			<input type="hidden" name="code" value="${code}" />
			<input type="hidden" name="confirm" id="confirm" value="true" />
		</div>

		<%-- list of existing translations in all available languages --%>
		<div class="${messagesContainerCss}" id="messagesContainer">
			<c:if test="${resource != null && resource.nameMappings != null}">
				<c:forEach items="${resource.nameMappings}" var="mapping">
					<c:set var="lang" value="${mapping.key}" />
					<c:set var="message" value="${mapping.value}" />
					<div class="${langContainerCss}">
						<label class="${langLabelCss}" for="langId${lang}"><fmt:message key="messages.admin.form.lang" /></label><input id="langId${lang}" type="text" name="langId" class="${langInputCss}" value="<c:out value="${lang}" escapeXml="true" />" disabled="disabled" />
						<label class="${messageLabelCss}" for="message${lang}"><fmt:message key="messages.admin.form.message" /></label><input id="message${lang}" type="text" name="message" class="${messageInputCss}" value="<c:out value="${message}" escapeXml="true" />" disabled="disabled" />
					</div>
				</c:forEach>
			</c:if>
		</div>
		<%-- submit, cancel --%>
		<div class="${buttonsContainerCss}">
			<button type="submit" class="${submitCss}"><fmt:message key="messages.admin.form.delete" /></button>
			&nbsp;&nbsp;&nbsp;<button class="${cancelCss}" onclick="window.location='delete.html?confirm=false&code=${code}';return false;" name="cancel" id="cancel"><fmt:message key="messages.admin.form.cancel" /></button>
		</div>
	</form>
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