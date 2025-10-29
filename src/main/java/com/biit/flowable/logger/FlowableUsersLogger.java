package com.biit.flowable.logger;

/*-
 * #%L
 * Liferay users in Flowable
 * %%
 * Copyright (C) 2021 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.logger.BiitLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowableUsersLogger extends BiitLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowableUsersLogger.class);

    /**
     * Events that have business meaning (i.e. creating category, deleting form, ...). To follow user actions.
     *
     * @param className name of the class
     * @param message   to be displayed
     */
    public static void info(String className, String message) {
        info(LOGGER, className, message);
    }

    /**
     * Shows not critical errors. I.e. Email address not found, permissions not allowed for this user, ...
     *
     * @param className
     * @param message
     */
    public static void warning(String className, String message) {
        warning(LOGGER, className + ": " + message);
    }

    /**
     * For following the trace of the execution. I.e. Knowing if the application access to a method, opening database
     * connection, etc.
     *
     * @param className
     * @param message
     */
    public static void debug(String className, String message) {
        debug(LOGGER, className, message);
    }

    /**
     * To log any not expected error that can cause application malfunction.
     *
     * @param className
     * @param message
     */
    public static void severe(String className, String message) {
        severe(LOGGER, className, message);
    }

    /**
     * To log java exceptions and log also the stack trace. If enabled, also can send an email to the administrator to
     * alert of the error.
     *
     * @param className
     * @param throwable
     */
    public static void errorMessage(String className, Throwable throwable) {
        errorMessageNotification(LOGGER, className, BiitLogger.getStackTrace(throwable));
    }

}
