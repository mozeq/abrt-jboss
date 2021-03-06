/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * The contents of this file are subject to the terms of the GNU
 * General Public License Version 2 only ("GPL").
 * You may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at http://www.gnu.org/licenses/gpl-2.0.html
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * This particular file is designated as subject to the "Classpath"
 * exception.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright (c) 2009-2010 Oracle and/or its affiliates.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.abrt.log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.jboss.logmanager.formatters.PatternFormatter;

import com.redhat.abrt.ProblemData;
import com.redhat.abrt.ProblemDataAbrt;
import com.redhat.abrt.ProblemDataServer;

public class AbrtLogHandler extends Handler {
    private final Formatter formatter = new PatternFormatter("%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n");
    private ProblemDataServer problemServer = null;

    public AbrtLogHandler() {
        setFormatter(formatter);
        try {
            problemServer = new ProblemDataServer();
        }
        catch (Exception e) {
            System.out.println("Can't connect to ABRT: "+ e.getMessage());
        }
    }

    private void log(LogRecord record) {
        ProblemData pd = new ProblemDataAbrt();
        Throwable throwable = record.getThrown();
        if (throwable != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter pWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(pWriter);
            pd.add("BACKTRACE", stringWriter.toString());
        }
        pd.add("REASON", record.getMessage());
        pd.add("TYPE", "jboss");
        pd.add("ANALYZER", "jboss");
        pd.add("PID", "" + Thread.currentThread().getId());
        /* TODO: get a path to the war file of the crashing application */
        pd.add("EXECUTABLE", "/usr/share/jboss-as/bin/standalone.sh");

        try {
            problemServer.send(pd);
        } catch (IOException e) {
            System.out.println("Sending data to ABRT server failed :'" + e.getMessage() + "'");
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.logging.Handler#publish(java.util.logging.LogRecord)
     */
    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) {
            try {
                log(record);
            } catch (Exception e) {
                throw new IllegalStateException("Can't log the message: ", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.logging.Handler#close()
     */
    @Override
    public void close() {
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.logging.Handler#flush()
     */
    @Override
    public void flush() {
    }

}
