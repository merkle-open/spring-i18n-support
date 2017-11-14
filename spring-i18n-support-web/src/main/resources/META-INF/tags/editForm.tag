<%@ tag  pageEncoding="UTF-8"  language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ tag import="java.util.*, java.lang.*,com.namics.oss.spring.support.i18n.model.MessageResourceEntry" %>

<%@ attribute name="resource" required="false" type="com.namics.oss.spring.support.i18n.model.MessageResourceEntry" %>
<%@ attribute name="langs" required="true" type="java.util.List" %>
<%@ attribute name="action" required="false" type="java.lang.String" %>
<%@ attribute name="addLangSupported" required="false" type="java.lang.Boolean" %>
<%@ attribute name="editLangSupported" required="false" type="java.lang.Boolean"  %>
<%@ attribute name="editTypeSupported" required="false" type="java.lang.Boolean" %>
<%@ attribute name="multilineSupported" required="false" type="java.lang.Boolean" %>
<%@ attribute name="defaultType" required="false" type="java.lang.String" %>

<%@ attribute name="jqueryEmbedded" required="false" type="java.lang.String" %>
<%@ attribute name="cssEmbedded" required="false" type="java.lang.String" %>

<%@ attribute name="errorCss" required="false" type="java.lang.String" %>
<%@ attribute name="containerCss" required="false" type="java.lang.String" %>
<%@ attribute name="codeInputCss" required="false" type="java.lang.String" %>
<%@ attribute name="codeLabelCss" required="false" type="java.lang.String" %>
<%@ attribute name="codeContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="typeInputCss" required="false" type="java.lang.String" %>
<%@ attribute name="typeLabelCss" required="false" type="java.lang.String" %>
<%@ attribute name="typeContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="langInputCss" required="false" type="java.lang.String" %>
<%@ attribute name="langLabelCss" required="false" type="java.lang.String" %>
<%@ attribute name="langContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="messageInputCss" required="false" type="java.lang.String" %>
<%@ attribute name="messageLabelCss" required="false" type="java.lang.String" %>
<%@ attribute name="submitCss" required="false" type="java.lang.String" %>
<%@ attribute name="cancelCss" required="false" type="java.lang.String" %>
<%@ attribute name="messagesContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="buttonsContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="actionsContainerCss" required="false" type="java.lang.String" %>
<%@ attribute name="newLangCss" required="false" type="java.lang.String" %>
<%@ attribute name="acceptCharset" required="false" type="java.lang.String" %>

<c:if test="${jqueryEmbedded == null}">
	<c:set var="jqueryEmbedded" value="false"/>
</c:if>
<c:if test="${cssEmbedded == null}">
	<c:set var="cssEmbedded" value="false"/>
</c:if>
<c:if test="${action == null}">
	<c:set var="action" value="put.html"/>
</c:if>

<c:if test="${addLangSupported == null}">
	<c:set var="addLangSupported" value="false"/>
</c:if>
<c:if test="${editLangSupported == null}">
	<c:set var="editLangSupported" value="false"/>
</c:if>
<c:if test="${editTypeSupported == null}">
	<c:set var="editTypeSupported" value="false"/>
</c:if>
<c:if test="${multilineSupported == null}">
	<c:set var="multilineSupported" value="false"/>
</c:if>

<c:if test="${defaultType == null}">
	<c:set var="defaultType" value=""/>
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
<c:if test="${typeContainerCss == null}">
	<c:set var="typeContainerCss" value="msg_ct_type"/>
</c:if>
<c:if test="${typeInputCss == null}">
	<c:set var="typeInputCss" value="msg_i_type"/>
</c:if>
<c:if test="${typeLabelCss == null}">
	<c:set var="typeLabelCss" value="msg_l_type"/>
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

<c:if test="${actionsContainerCss == null}">
	<c:set var="actionsContainerCss" value="msg_ct_new"/>
</c:if>
<c:if test="${newLangCss == null}">
	<c:set var="newLangCss" value="msg_btn_newlang"/>
</c:if>
<c:if test="${acceptCharset == null}">
	<c:set var="acceptCharset" value="UTF-8"/>
</c:if>
<%-- EDIT MASK --%>
<div class="${containerCss}">
	<form method="post" action="${action}" id="msgEditForm" name="msgEditForm" accept-charset="${acceptCharset}">
		<c:if test="${_csrf != null}">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
		</c:if>
		<c:set var="code" value=""/>
		<c:set var="type" value="${defaultType}"/>
		<c:set var="isEdit" value="false"/>
		<c:set var="disabled" value=""/>
		<c:set var="typeDisabled" value=""/>
		<c:if test="${resource != null && resource.codeId != null}">
			<c:set var="code" value="${resource.codeId}"/>
			<c:set var="type" value="${resource.type}" />
			<c:set var="disabled">disabled="disabled"</c:set>
			<c:set var="typeDisabled">disabled="disabled"</c:set>
			<c:set var="isEdit" value="true"/>
		</c:if>
		<c:if test="${!editTypeSupported}">
			<c:set var="typeDisabled">disabled="disabled"</c:set>
		</c:if>

		<%-- message code currently being edited --%>
		<div class="${codeContainerCss}">
			<label class="${codeLabelCss}" for="codeId"><fmt:message key="messages.admin.form.code" /></label><input id="codeId" type="text" name="codeId" class="${codeInputCss}" value="${code}" ${disabled} />
			<c:if test="${isEdit}">
				<input type="hidden" name="codeId" value="${code}" />
			</c:if>
		</div>
		<%-- message type (structual) --%>
		<div class="${typeContainerCss}">
			<c:if test="${type == null && !isEdit}">
				<c:set var="type" value="${defaultType}"/>
			</c:if>
			<label class="${typeLabelCss}" for="type"><fmt:message key="messages.admin.form.type" /></label><input id="type" type="text" name="type" class="${typeInputCss}" value="${type}" ${typeDisabled} />
			<c:if test="${isEdit || (typeDisabled != null && typeDisabled != '') }">
				<input type="hidden" name="type" value="${type}"/>
			</c:if>
		</div>

		<%-- list of existing translations in all available languages --%>
		<div class="${messagesContainerCss}" id="messagesContainer">
			<c:set var="langsString" value=""/>
			<c:forEach items="${langs}" var="lang">
				<c:set var="langsString" value="${langsString}_${lang}"/>
				<c:set var="message" value=""/>
				<c:if test="${resource != null && resource.nameMappings != null && resource.nameMappings[lang] != null}">
					<c:set var="message" value="${resource.nameMappings[lang]}"/>
				</c:if>
				<div class="${langContainerCss}">
					<c:choose>
						<c:when test="${editLangSupported}" >
							<label for="langId${lang}" class="${langLabelCss}"><fmt:message key="messages.admin.form.lang" /></label><input id="langId${lang}" type="text" name="langId" class="${langInputCss}" value="${lang}" />
						</c:when>
						<c:otherwise>
							<label for="langId${lang}" class="${langLabelCss}"><fmt:message key="messages.admin.form.lang" /></label><input id="langId${lang}" type="text" name="langId" class="${langInputCss}" value="${lang}" disabled="disabled"/>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${multilineSupported}" >
							<label for="message${lang}" class="${messageLabelCss}"><fmt:message key="messages.admin.form.message" /></label><textarea id="message${lang}"  name="message" class="${messageInputCss}" ><c:out value="${message}" escapeXml="true" /></textarea>
						</c:when>
						<c:otherwise>
							<label for="message${lang}" class="${messageLabelCss}"><fmt:message key="messages.admin.form.message" /></label><input id="message${lang}" type="text" name="message" class="${messageInputCss}" value="<c:out value="${message}" escapeXml="true" />" />
						</c:otherwise>
					</c:choose>
				</div>
			</c:forEach>
			<c:if test="${resource != null && resource.nameMappings != null}">
				<c:forEach items="${resource.nameMappings}" var="mapping">
					<c:if test="${!fn:contains(langsString,mapping.key)}">
						<c:set var="lang" value="${mapping.key}" />
						<c:set var="message" value="${mapping.value}" />
						<div class="${langContainerCss}">
							<c:choose>
								<c:when test="${editLangSupported}">
									<label for="langId${lang}" class="${langLabelCss}"><fmt:message key="messages.admin.form.lang" /></label><input id="langId${lang}" type="text" name="langId" class="${langInputCss}" value="${lang}" />
								</c:when>
								<c:otherwise>
									<label for="langId${lang}" class="${langLabelCss}"><fmt:message key="messages.admin.form.lang" /></label><input id="langId${lang}" type="text" name="langId" class="${langInputCss}" value="${lang}" disabled="disabled" />
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${multilineSupported}" >
									<label for="message${lang}" class="${messageLabelCss}"><fmt:message key="messages.admin.form.message" /></label><textarea  id="message${lang}" name="message" class="${messageInputCss}" ><c:out value="${message}" escapeXml="true" /></textarea>
								</c:when>
								<c:otherwise>
									<label for="message${lang}" class="${messageLabelCss}"><fmt:message key="messages.admin.form.message" /></label><input  id="message${lang}" type="text" name="message" class="${messageInputCss}" value="<c:out value="${message}" escapeXml="true" />" />
								</c:otherwise>
						</c:choose>
						</div>
					</c:if>
				</c:forEach>
			</c:if>
		</div>
		<%-- if allowed, display 'new language' button --%>
		<c:if test="${addLangSupported}">
			<div class="${actionsContainerCss}">
				<button class="${newLangCss}" name="newLangSwitch" id="newLangSwitch"><fmt:message key="messages.admin.form.new" /></button>
			</div>
		</c:if>
		<%-- submit, cancel --%>
		<div class="${buttonsContainerCss}">
			<button type="submit" class="${submitCss}"><fmt:message key="messages.admin.form.submit" /></button>
			&nbsp;&nbsp;&nbsp;<button class="${cancelCss}" name="cancel" onclick="window.location='index.html'; return false;"><fmt:message key="messages.admin.form.cancel" /></button>
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

<%-- page specific javascript inits --%>
<script type="text/javascript">
//<![CDATA[
	<%-- regex for allowed language shorthand (locale) --%>
	var LOCALE_REGEX = /^[a-z]{2,2}(_[A-Z]{2,2}(_[a-z]{3,3})?)?$/;
	jQuery(document).ready(function() {
		<%-- locale validation and error css indication onkeyup --%>
		jQuery("[name=langId]").keyup(function(){
			var value = jQuery(this).val();
			if (LOCALE_REGEX.exec(value) != null) {
				jQuery(this).removeClass("${errorCss}");
			} else if (!jQuery(this).hasClass("${errorCss}")) {
				jQuery(this).addClass("${errorCss}");
			}
			return true;
		});

		<%-- edit submit --%>
		jQuery("#msgEditForm").submit(function(){
			jQuery("[name=langId]").each(function(index,value){
				var hiddenId = "hid" + Math.floor(Math.random()*1000);
				var locale = jQuery(value).val();
				var id = jQuery(value).attr("id").substring(6);
				jQuery("#msgEditForm").append("<input type=\"hidden\" id=\"" + hiddenId + "\"name=\"nameMappings[" + locale + "]\" />");
				jQuery("#" + hiddenId).val(jQuery("#message" + id).val());
			});
			return true;
		});

		<%-- clickhandler: add language --%>
		jQuery("#newLangSwitch").click(function(){
			var rand=Math.floor(Math.random()*201);
			var insert =	"<div class=\"${langContainerCss}\" style=\"display:none;\" id=\"ctMsg" + rand + "\">\n";
			insert +=			"<label for=\"langId" + rand + "\" class=\"${langLabelCss}\">" + '<fmt:message key="messages.admin.form.lang" />' +"</label><input id=\"langId" + rand + "\" type=\"text\" name=\"langId\" class=\"${langInputCss}\" />\n";
<c:choose>
	<c:when test="${multilineSupported}" >
			insert +=			"<label for=\"message" + rand + "\" class=\"${messageLabelCss}\">" + '<fmt:message key="messages.admin.form.message" />' + "</label><textarea id=\"message" + rand + "\"  name=\"message\" class=\"${messageInputCss}\" ></textarea>\n";
	</c:when>
	<c:otherwise>
			insert +=			"<label for=\"message" + rand + "\" class=\"${messageLabelCss}\">" + '<fmt:message key="messages.admin.form.message" />' + "</label><input id=\"message" + rand + "\" type=\"text\" name=\"message\" class=\"${messageInputCss}\" />\n";
	</c:otherwise>
</c:choose>
			insert +=		"</div>";
			jQuery("#messagesContainer").append(insert);
			jQuery("#ctMsg" + rand).fadeIn(250);
			return false;
		});
	});
//]]>
</script>