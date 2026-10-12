<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>TODO List</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>TODO List</h1>

<c:if test="${not empty idError}">
    <c:out value="${idError}" />
</c:if>

<form method="post" action="${pageContext.request.contextPath}">
    <input type="hidden" name="action" value="create">

    <label>Enter text:</label>
    <input type="text" name="todoItemText">
    <button type="submit">Create</button>

    <c:if test="${not empty createError}">
        <c:out value="${createError}" />
    </c:if>
</form>

<ul>
    <c:forEach var="item" items="${items}">
        <li>
            <form method="post" action="${pageContext.request.contextPath}">
                <input type="hidden" name="todoItemId" value="<c:out value='${item.id}' />">

                <input type="text" name="todoItemText" value="<c:out value='${item.text}' />">
                <button type="submit" name="action" value="save">Save</button>
                <button type="submit" name="action" value="delete">Delete</button>

                <c:if test="${not empty saveError && saveErrorId == item.id}">
                    <c:out value="${saveError}" />
                </c:if>
            </form>
        </li>
    </c:forEach>
</ul>
</body>
</html>