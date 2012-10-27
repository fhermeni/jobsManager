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

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The interface to specify a HTML interface
 * to visualize the benchmark status.
 *
 * @author Fabien Hermenier
 */
public interface HTMLReporter {

    /**
     * Print some details about a job.
     *
     * @param d        the job dispatcher to analyze
     * @param j        the job to analyze
     * @param response the response to the client
     * @throws java.io.IOException if an error occurred while writing the response
     */
    public void printJobReport(JobDispatcher d, Job j, HttpServletResponse response) throws IOException;

    /**
     * Print some details about all the jobs.
     *
     * @param d        the job dispatcher to analyze
     * @param response the response to the client
     * @throws java.io.IOException if an error occurred while writing the response
     */
    public void printJobsReport(JobDispatcher d, HttpServletResponse response) throws IOException;
}
