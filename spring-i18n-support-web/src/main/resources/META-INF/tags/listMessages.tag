<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ tag import="java.util.*, java.lang.*" %> 

<%@ attribute name="messages" required="true" type="java.util.Map" %>
<%@ attribute name="langs" required="true" type="java.util.List" %>

<%@ attribute name="showDelete" required="false" type="java.lang.String" %>
<%@ attribute name="showEdit" required="false" type="java.lang.String" %>

<%@ attribute name="deleteCss" required="false" type="java.lang.String" %>
<%@ attribute name="editCss" required="false" type="java.lang.String" %>

<%@ attribute name="colCssPrefix" required="false" type="java.lang.String" %>
<%@ attribute name="titleCss" required="false" type="java.lang.String" %>
<%@ attribute name="tableCss" required="false" type="java.lang.String" %>
<%@ attribute name="evenRowCss" required="false" type="java.lang.String" %>
<%@ attribute name="oddRowCss" required="false" type="java.lang.String" %>

<%@ attribute name="jqueryEmbedded" required="false" type="java.lang.Boolean" %>
<%@ attribute name="jqueryTablesorterEmbedded" required="false" type="java.lang.Boolean" %>
<%@ attribute name="cssEmbedded" required="false" type="java.lang.Boolean" %>

<c:if test="${jqueryEmbedded == null}">
	<c:set var="jqueryEmbedded" value="false"/>
</c:if>
<c:if test="${jqueryTablesorterEmbedded == null}">
	<c:set var="jqueryTablesorterEmbedded" value="false"/>
</c:if>
<c:if test="${cssEmbedded == null}">
	<c:set var="cssEmbedded" value="false"/>
</c:if>

<c:if test="${showDelete == null}">
	<c:set var="showDelete" value="icon"/>
</c:if>
<c:if test="${showEdit == null}">
	<c:set var="showEdit" value="icon"/>
</c:if>

<c:if test="${deleteCss == null}">
	<c:set var="deleteCss" value="msg_ico_del"/>
</c:if>
<c:if test="${editCss == null}">
	<c:set var="editCss" value="msg_ico_edit"/>
</c:if>

<c:if test="${colCssPrefix == null}">
	<c:set var="colCssPrefix" value="msg_col_"/>
</c:if>

<c:if test="${tableCss == null}">
	<c:set var="tableCss" value="msg_tbl"/>
</c:if>
<c:if test="${titleCss == null}">
	<c:set var="titleCss" value="msg_tbl_h"/>
</c:if>
<c:if test="${evenRowCss == null}">
	<c:set var="evenRowCss" value="msg_even"/>
</c:if>
<c:if test="${oddRowCss == null}">
	<c:set var="oddRowCss" value="msg_odd"/>
</c:if>

<%-- LIST w/ heading, table --%>
<c:forEach items="${messages}" var="list">
	<h3 class="${titleCss}"><fmt:message key="messages.admin.table.title" /> ${list.key}</h3>
	<div class="${tableCss}">
		<table class="${tableCss}">
			<colgroup>
				<c:set var="lastCol" value="1"/>
				<col class="${colCssPrefix}0" />
				<c:forEach items="${langs}" var="lang" varStatus="hs">
					<col class="${colCssPrefix}${hs.index+1}" />
					<c:set var="lastCol" value="${hs.index+2}"/>
				</c:forEach>
				<col class="${colCssPrefix}${lastCol}" />
			</colgroup>
			<thead>
				<tr>
					<th scope="col" class="${colCssPrefix}0"><fmt:message key="messages.admin.table.th.key" /></th>
					<c:forEach items="${langs}" var="lang" varStatus="ls">
						<th scope="col" class="${colCssPrefix}${ls.index+1}">
							<div class="${colCssPrefix}${ls.index+1}">${lang}</div>
						</th>
					</c:forEach>
					<th scope="col" class="${colCssPrefix}${lastCol}"><fmt:message key="messages.admin.table.th.actions" /></th>
				</tr>
			</thead>
			<tbody>
				<%-- configure each column's width in css, a div with overflow:hidden and the same width class ensures overlapping content is cut --%>
				<c:forEach items="${list.value}" var="message" varStatus="ms">
					<tr class="${ms.index%2 == 0 ? evenRowCss : oddRowCss}">
						<td class="${colCssPrefix}0">
							<div class="${colCssPrefix}0" title="<c:out value="${message.codeId}" escapeXml="true" />"><c:out value="${message.codeId}" escapeXml="true" /></div>
						</td>
						<c:forEach items="${langs}" var="lang" varStatus="vs">
							<td class="${colCssPrefix}${vs.index+1}">
								<div class="${colCssPrefix}${vs.index+1}"><c:out value="${message.nameMappings[lang]}" escapeXml="true" /></div>
							</td>
						</c:forEach>
						<td class="${colCssPrefix}${lastCol}">
							<div class="${colCssPrefix}${lastCol}">
								<c:choose>
									<c:when test="${showEdit eq 'text'}">
										<a href="<c:url value="edit.html?code=${message.codeId}"/>"  title="<fmt:message key="messages.admin.table.tr.edit" />"><fmt:message key="messages.admin.table.tr.edit" /></a>
									</c:when>
									<c:when test="${showEdit eq 'icon' || showEdit eq 'true'}">
										<a class="${editCss}" href="<c:url value="edit.htm?code=${message.codeId}"/>" title="<fmt:message key="messages.admin.table.tr.edit" />"><img src="<c:url value="image/x.gif"/>" width="18" height="18" alt="<fmt:message key="messages.admin.table.tr.edit" />" /></a>
									</c:when>
									<c:otherwise>
										<%-- edit not allowed --%>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${showDelete eq 'text'}">
										<a href="<c:url value="confirmDelete.html?code=${message.codeId}"/>"  title="<fmt:message key="messages.admin.table.tr.delete" />"><fmt:message key="messages.admin.table.tr.delete" /></a>
									</c:when>
									<c:when test="${showDelete eq 'icon' || showDelete eq 'true'}">
										<a class="${deleteCss}" href="<c:url value="confirmDelete.html?code=${message.codeId}"/>" title="<fmt:message key="messages.admin.table.tr.delete" />"><img src="<c:url value="image/x.gif"/>" width="18" height="18" alt="<fmt:message key="messages.admin.table.tr.delete" />" /></a>
									</c:when>
									<c:otherwise>
										<%-- delete not allowed --%>
									</c:otherwise>
								</c:choose>
							</div>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</c:forEach>

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
<c:if test="${jqueryTablesorterEmbedded}">
	<script type="text/javascript">
	//<![CDATA[
		<%@ include file="jquery-ts.js" %>
	//]]>
	</script>
</c:if>

<%-- page specific javascript inits --%>
<script type="text/javascript">
//<![CDATA[
	<%-- global var: integer for last column --%>
	var colCnt = ${lastCol};
	<%-- tablesorter init, even/odd class assignment, only first column (msg codes) sortable, last column (expected to contain action links only) w/o sorting --%>
	jQuery(document).ready(function() {
		jQuery(".${tableCss}").tablesorter({
			widgets: ["zebra"],
			widgetZebra: {css: ["${evenRowCss}","${oddRowCss}"]},
			headers: {
			<c:forEach items="${langs}" var="lang" varStatus="ls">
				${ls.index+1}: {sorter:false},
			</c:forEach>
				${lastCol}: {sorter:false}
			}
		});
	});
//]]>
</script>