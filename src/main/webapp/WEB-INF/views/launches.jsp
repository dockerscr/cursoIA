<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="fragments/header.jspf" %>

<c:choose>
    <c:when test="${not empty errorMessage}">
        <section class="banner banner--error" role="alert" aria-labelledby="catalog-error-title">
            <h1 id="catalog-error-title">Launch catalog unavailable</h1>
            <p><c:out value="${errorMessage}"/></p>
            <a class="button" href="<c:url value='/launches'/>">Try again</a>
        </section>
    </c:when>

    <c:when test="${empty launches}">
        <section class="banner banner--empty" aria-labelledby="catalog-empty-title">
            <h1 id="catalog-empty-title">No launches available</h1>
            <p>There are no launches open for booking right now. Please check back soon.</p>
            <a class="button" href="<c:url value='/launches'/>">Refresh</a>
        </section>
    </c:when>

    <c:otherwise>
        <section class="catalog" aria-labelledby="catalog-title">
            <h1 id="catalog-title">Upcoming rocket launches</h1>
            <p class="catalog__intro">Browse the available launches and choose one to book your seat.</p>

            <table class="catalog-table">
                <caption class="visually-hidden">Available rocket launches ordered by launch date, earliest first</caption>
                <thead>
                <tr>
                    <th scope="col">Launch</th>
                    <th scope="col">Date</th>
                    <th scope="col">Location</th>
                    <th scope="col">Price</th>
                    <th scope="col">Duration</th>
                    <th scope="col">Max seats</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="launch" items="${launches}">
                    <tr>
                        <td data-label="Launch">
                            <a class="launch-link" href="<c:url value='/launches/${launch.id}'/>">
                                <c:out value="${launch.name}"/>
                            </a>
                            <c:if test="${launch.bookable}">
                                <span class="badge badge--open">Open</span>
                            </c:if>
                        </td>
                        <td data-label="Date"><c:out value="${launch.dateLabel}"/></td>
                        <td data-label="Location"><c:out value="${launch.location}"/></td>
                        <td data-label="Price">
                            <fmt:formatNumber value="${launch.price}" type="currency" currencyCode="USD"/>
                        </td>
                        <td data-label="Duration"><c:out value="${launch.durationMinutes}"/> min</td>
                        <td data-label="Max seats"><c:out value="${launch.maxParticipants}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </section>
    </c:otherwise>
</c:choose>

<%@ include file="fragments/footer.jspf" %>
