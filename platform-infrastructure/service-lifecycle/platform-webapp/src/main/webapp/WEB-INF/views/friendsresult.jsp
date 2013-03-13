<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
	import="java.util.*" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Friends Result</title>
</head>
<body>
	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
<!-- .................PLACE YOUR CONTENT BELOW HERE ................ -->

<h4>${result}</h4>
<br/>
<br/>
<script language="javascript">
	function updateForm(friendId, method) {    
		document.forms["friendResForm"]["friendId"].value = friendId;
		document.forms["friendResForm"]["method"].value = method;
		document.forms["friendResForm"].submit();
	} 
</script>

<form id="friendResForm" name="friendResForm" method="post" action="suggestedfriendspilot.html">
		<input type="hidden" name="friendId" id="friendId">
		<input type="hidden" name="method" id="method">
<Table border="1">
<tr><td><B>Friend Name</B></td><td><B>CSS Identity</B></td></tr>

	<xc:forEach var="cssfriend" items="${cssfriends}">
        <tr>
        	<td>${cssfriend.getName()}</td>
        	<td>${cssfriend.getId()}</td>
        	<td><input type="button" value="Remove Friend" onclick="updateForm('${cssfriend.getId()}','delete')" ></td>	         	       
        </tr>
    </xc:forEach>
    	
	</table>
</form>	
<!-- .................END PLACE YOUR CONTENT ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>