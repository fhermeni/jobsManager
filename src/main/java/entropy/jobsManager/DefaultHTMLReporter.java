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

import gnu.trove.TIntArrayList;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Default status reporter for JobDispatcher. Print an overall status and some details about handlers or jobs.
 *
 * @author Fabien Hermenier
 */
public class DefaultHTMLReporter implements HTMLReporter {

    private static DateFormat df = new SimpleDateFormat("yyyy/MM/dd 'at' HH:mm:ss z");

    private JobDispatcher dispatcher;

    /**
     * Make a new DefaultHTMLReporter.
     *
     * @param d the dispatcher to analyze
     */
    public DefaultHTMLReporter(JobDispatcher d) {
        dispatcher = d;
    }

    /**
     * The maximum number of columns per job table
     *
     * @return an int
     */
    public int getJobColumnsWidth() {
        return 30;
    }

    /**
     * Print the CSS of the page.
     *
     * @param r the response
     * @throws java.io.IOException if an error occurred while writing the response
     */
    public void printCSS(HttpServletResponse r) throws IOException {
        r.getWriter().println("<style type=\"text/css\">");
        r.getWriter().println("table {\nborder-collapse: collapse; padding: 5px;\n border: solid black 1px;}");
        r.getWriter().println("td{border: solid black 1px; width: 20px; height: 20px; text-align: center; padding: 5px;}");
        r.getWriter().println("td.completed{background-color: green;}\ntd.waiting{background-color: yellow;}\ntd.running{background-color: orange;}");
        r.getWriter().println("textarea { width: 80%; height: 100px}");
        r.getWriter().println("h1 {text-align: center;}");
        r.getWriter().println("</style>");
    }


    /**
     * Print a javascript to automatically reload the page every 5 seconds.
     *
     * @param r the response
     * @throws java.io.IOException if an error occurred while writing the response
     */
    public void printJavascript(HttpServletResponse r) throws IOException {
        r.getWriter().println("<script language=\"javaScript\" type=\"text/javascript\">");
        r.getWriter().println("refreshFreq = 1000 * 5;");
        r.getWriter().println("WhereNext = location.href;");
        r.getWriter().println("setTimeout(\"window.open(WhereNext,'_top');\",refreshFreq);");
        r.getWriter().println("</script>");

    }

    /**
     * Print a table of jobs.
     * Number of columns is provided by <code>getJobColumnsWidth()</code>.
     *
     * @param r    the response
     * @param jobs the list of jobs to print
     * @throws java.io.IOException if an error occurred while writing the response
     */
    public void jobDetails(HttpServletResponse r, TIntArrayList jobs) throws IOException {
        int i = 0;
        r.getWriter().println("<table>\n<tr>");
        while (i < jobs.size()) {
            int id = jobs.get(i);
            Job j = dispatcher.getJob(id);
            r.getWriter().println("<td class=\"" + getStatus(j) + "\"><a href=\"/?j=" + j.getId() + "&output=html\">" + j.getId() + "</a></td>");
            if (((i + 1) % getJobColumnsWidth() == 0)) {
                r.getWriter().println("</tr><tr>");
            }
            i++;
        }
        r.getWriter().println("</tr>\n</table>");
    }

    private String getStatus(Job j) {
        if (j.getCommitedTime() > 0) {
            return "completed";
        } else if (j.getDequeuedTime() > 0) {
            return "running";
        }
        return "waiting";
    }

    @Override
    public void printJobReport(JobDispatcher d, Job j, HttpServletResponse r) throws IOException {
        r.setStatus(HttpServletResponse.SC_OK);
        r.getWriter().println("<html><head>");
        printCSS(r);
        r.getWriter().println("</head>\n<body>");

        r.getWriter().println("<h1>Details of job " + j.getId() + "</h1>\n");
        r.getWriter().println("<ul>");
        r.getWriter().println("<li>status: ");
        r.getWriter().println(getStatus(j));
        r.getWriter().println("</li>");
        r.getWriter().println("<li>Enqueued time: ");
        r.getWriter().println(df.format(j.getEnqueuedTime()));
        r.getWriter().println("</li>");
        r.getWriter().println("<li>Dequeued time: ");
        long dq = j.getDequeuedTime();
        r.getWriter().println(dq > 0 ? df.format(dq) : "-");
        r.getWriter().println("</li>");
        r.getWriter().println("<li>Commited time: ");
        long c = j.getCommitedTime();
        r.getWriter().println(c > 0 ? df.format(c) : "-");
        r.getWriter().println("</li>");


        r.getWriter().println("</ul>");
        r.getWriter().println("<br/><ul>");
        for (String k : j.getKeys()) {
            r.getWriter().println("<li>");
            r.getWriter().println(k);
            r.getWriter().println(":");
            r.getWriter().println(j.get(k));
            r.getWriter().println("</li>");
        }
        r.getWriter().println("</ul>");
        r.getWriter().println("<a href=\"/\">Back to jobs</a>");
        r.getWriter().println("</body></html>");

    }

    @Override
    public void printJobsReport(JobDispatcher d, HttpServletResponse r) throws IOException {
        r.setStatus(HttpServletResponse.SC_OK);
        r.getWriter().println("<html><head>");
        printCSS(r);
        printJavascript(r);
        r.getWriter().println("</head>\n<body>");

        r.getWriter().println("<h1>Jobs status</h1>");
        TIntArrayList ws = dispatcher.getWaitings();
        TIntArrayList rs = dispatcher.getRunnings();
        TIntArrayList cs = dispatcher.getComitted();
        int nbWaitings = ws.size();
        int nbRunnings = rs.size();
        int nbCommited = cs.size();
        r.getWriter().println((nbWaitings + nbCommited + nbRunnings) + " jobs: " + nbWaitings + " waitings; " + nbRunnings + " runnings; " + nbCommited + " commited<br/>");
        TIntArrayList allJobs = new TIntArrayList(nbWaitings + nbCommited + nbRunnings);
        allJobs.add(cs.toNativeArray());
        allJobs.add(rs.toNativeArray());
        allJobs.add(ws.toNativeArray());
        jobDetails(r, allJobs);

        r.getWriter().println("</body></html>");
    }
}
