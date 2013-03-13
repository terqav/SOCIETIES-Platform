<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Devices - Device Management - Device Details</title>
<style>
.error {
	color: #ff0000;
}

.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}
</style>
</head>
<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
	<!-- .................PLACE YOUR CONTENT BELOW HERE ................ -->

	<br />
	<br />

	<xc:forEach var="driverService" items="${driverServices}" varStatus="status1">
		<%-- <h2>Service ${status1.index + 1} </h2> --%>
		
		<h2>${driverService.name}</h2>
		
		<xc:forEach var="action" items="${iActions}" varStatus="status2">
		
			<h3> ${action.actionName}: </h3>
		</xc:forEach>
<%-- 		<form method="post" action="devicemgmt.html">
						<input type="hidden" name="deviceId" value="${device.deviceId}" />
						<input type="hidden" name="deviceNodeId" value="${device.deviceNodeId}" />
						<xc:choose>
							<xc:when test="${device.status == false }">
								<input type="submit" name="enable" id="enable" value="Enable" />
							</xc:when>
							<xc:when test="${device.status == true }">
								<input type="submit" name="enable" id="enable" value="Disable" />
							</xc:when>
    						<xc:otherwise>    						
    						</xc:otherwise>
						</xc:choose>
					</form> --%>
		
		
		<br />
		<br />
	</xc:forEach>

	<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>