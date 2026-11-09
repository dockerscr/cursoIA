package com.astrobookings.client.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Context root route. Redirects to the catalog so {@code /} and
 * {@code /launches} always show the same entry page.
 *
 * <p>The empty mapping is intentional: {@code "/"} would shadow the container's
 * default servlet and break static assets.</p>
 */
@WebServlet(name = "HomeServlet", urlPatterns = "")
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/launches");
    }
}
