<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="hairdressings">
    <h2>hairdressings</h2>

    <table id="hairdressingsTable" class="table table-striped">
        <thead>
        <tr>
            <th style="width: 200px;">Description</th>
            <th>Date</th>
            <th>Capacity</th>
            <td>
            	<spring:url value="/hairdressings/delete/{hairdressingId}" var="hairdressingUrl">
               		<spring:param name="hairdressingId" value="${hairdressing.id}"/>
                </spring:url>
            	<a href="${fn:escapeXml(hairdressingUrl)}"></a>

        </tr>
        </thead>
        <tbody>
        <c:forEach items="${hairdressings}" var="hairdressing">
            <tr>
                <td>
                    <c:out value="${hairdressing.description}"/>
                </td>
                <td>
                    <c:out value="${Date.date}"/>
                </td>
                <td>
                    <c:out value="25"/>
                </td>
                <td>
                    <c:forEach var="pet" items="${owner.pets}">
                        <c:out value="${pet.name} "/>
                    </c:forEach>
                </td>               
            </tr>
        </c:forEach>
        </tbody>
    </table>
</petclinic:layout>
