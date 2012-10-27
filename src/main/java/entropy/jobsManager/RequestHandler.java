package entropy.jobsManager;/*
 * Copyright (c) 2010 Ecole des Mines de Nantes.
 *
 *      This file is part of Entropy.
 *
 *      Entropy is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      Entropy is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with Entropy.  If not, see <http://www.gnu.org/licenses/>.
 */

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The handler to communicate with all the Job Handler.
 * All the communication are made through HTTP requests.
 * <p/>
 * The following request are handled:
 * <table>
 * <tr>
 * <td><b>GET /</b></td>
 * <td>Print an overall status of the benchmark (HTML output)</td>
 * </tr>
 * <tr>
 * <td><b>GET /?j=id</b></td>
 * <td>Print the status of the job ID. The param <b>output=html</b> provides a HTML output for a visualization
 * using a web browser</td>
 * </tr>
 * <tr>
 * <td><b>GET /?dequeue</b></td>
 * <td>Dequeue the first waiting job</td>
 * </tr>
 * <tr>
 * <td><b>POST /?commit=id/b></td>
 * <td>Commit the running job ID</td>
 * </tr>
 * </table>
 *
 * @author Fabien Hermenier
 */
public class RequestHandler extends AbstractHandler {

    /**
     * The job dispatcher.
     */
    private JobDispatcher master;

    /**
     * The status reporter to print status report.
     */
    private HTMLReporter reporter;

    /**
     * Make a new RequestHandler that will manage the job for a specific dispatcher.
     *
     * @param m the dispatcher to use
     */
    public RequestHandler(JobDispatcher m) {
        this.master = m;
    }

    /**
     * Get the status reporter used to print the status of the job dispatcher.
     *
     * @return a status reporter.
     */
    public HTMLReporter getStatusReporter() {
        return reporter;
    }

    /**
     * Set the status reporter used to print the status of the job dispatcher.
     *
     * @param r the status reporter.
     */
    public void setStatusReporter(HTMLReporter r) {
        reporter = r;
    }

    /**
     * Handle the request of the client and make the appropriate response.
     * The response is using a UTF-8 charset. If the URI of the request is unknown, then
     * the server replies with a {@value javax.servlet.http.HttpServletResponse#SC_NOT_FOUND} status code.
     *
     * @param s                  the URI
     * @param request            the client request
     * @param httpServletRequest the complete client request
     * @param response           the response to send to the client
     * @throws java.io.IOException            if an error occurred while writing the response or reading the request
     * @throws javax.servlet.ServletException if another error occurred
     */
    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");

        boolean handled = false;
        String method = request.getMethod();
        String jobId = request.getParameter("j");
        String action = request.getParameter("a");
        if (s.equals("/")) {
            if (method.equals("GET")) {
                if (request.getParameters() == null || request.getParameters().isEmpty()) {
                    reporter.printJobsReport(master, response);
                    handled = true;
                } else if (action != null) {
                    if (action.equals("status")) {
                        response.getWriter().println(master.getWaitings().size() + "/" + master.getRunnings().size() + "/" + master.getComitted().size());
                        response.setStatus(HttpServletResponse.SC_OK);
                        handled = true;
                    } else if (action.equals("stop")) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        handled = true;
                        master.stopServer();
                    } else if (action.equals("dequeue")) {
                        this.handleDequeueRequest(request, response);
                        handled = true;
                    } else {
                        response.getWriter().println("Unsupported action '" + action + "'");
                        response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);

                    }
                } else if (jobId != null) {
                    handled = true;
                    int i = Integer.parseInt(jobId);
                    Job j = master.getJob(i);
                    if (j == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                        reporter.printJobReport(master, j, response);
                    }
                } else {
                    JobDispatcher.getLogger().debug("Unhandled request: " + method + " " + request.getRequestURI());
                }
            } else if (method.equals("POST") && action.equals("commit")) {
                this.handleCommitRequest(request, response);
                handled = true;
            }


            if (!handled) {
                JobDispatcher.getLogger().debug("Unhandled request: " + method + " " + request.getRequestURI() + " " + request.getParameters());
            }

            request.setHandled(handled);
        }
    }

    /**
     * Handle a dequeue request.
     * If there is a job in the waiting queue, then it is sended to the handler and the response
     * status code is equals to {@value javax.servlet.http.HttpServletResponse#SC_OK}. Otherwise
     * the status code of the response is {@value javax.servlet.http.HttpServletResponse#SC_GONE}.
     *
     * @param r        the complete request of the client
     * @param response the response to send to the client.
     * @throws java.io.IOException if an error occurred while writing the response to the client.
     */
    public void handleDequeueRequest(HttpServletRequest r, HttpServletResponse response) throws IOException {
        Job j = master.dequeue();
        if (j != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().print(j.toJSON());
            response.setContentType("text/json");
        } else {
            JobDispatcher.getLogger().debug("No more waiting jobs");
            response.setStatus(HttpServletResponse.SC_GONE);
        }
    }

    /**
     * Handle a commit request.
     * The request must be using the POST method.
     *
     * @param request  the complete request of the client
     * @param response the response to send to the client.
     * @throws java.io.IOException if an error occurred while writing the response to the client.
     */
    public void handleCommitRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        StringBuilder b = new StringBuilder();
        String line = request.getReader().readLine();
        while (line != null) {
            b.append(line);
            line = request.getReader().readLine();
        }
        //System.err.println(b.toString());
        Job j = Job.fromJSON(b.toString());
        int id = Integer.parseInt(request.getParameter("j"));
        Map<String, String> values = new HashMap<String, String>();
        for (Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
            values.put(e.getKey(), e.getValue()[0]);
        }
        //System.err.println(j);
        master.commit(j);
    }
}
