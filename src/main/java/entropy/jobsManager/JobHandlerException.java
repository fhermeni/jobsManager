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

/**
 * An exception to signal an error for a JobHandler.
 *
 * @author Fabien Hermenier
 */
public class JobHandlerException extends Exception {

    /**
     * Make a new exception.
     *
     * @param message the error message
     */
    public JobHandlerException(String message) {
        super(message);
    }

    /**
     * Rethrow another exception/
     *
     * @param message the error message
     * @param t       the exception to rethrow
     */
    public JobHandlerException(String message, Throwable t) {
        super(message, t);
    }

}
