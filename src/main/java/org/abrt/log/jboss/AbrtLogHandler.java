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
package org.abrt.log.jboss;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.jboss.logmanager.formatters.PatternFormatter;

public class AbrtLogHandler extends Handler {
    private final Formatter formatter = new PatternFormatter("%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n");

    public AbrtLogHandler() {
        setFormatter(formatter);
        System.out.println(">>>>>>>>>>>>>> Init ABRT logger");
        System.out.println(getLevel());
    }

    private void log(LogRecord record) {

        BufferedWriter output = null;
        System.out.println(">>>>>>>>>>>>>> Let's log something for ABRT! " + String.format(record.getMessage(), record.getParameters()));


        Throwable throwable = record.getThrown();
        if (throwable != null) {
            //save the stacktrace to "backtrace" element
        }

        try {
            output = new BufferedWriter(new FileWriter("/tmp/abrt_jboss_log", true));
            output.write(record.getSourceClassName());
            output.write(record.getLevel() + " ");
            output.write(getFormatter().format(record));
            output.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {  // cleanup
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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
        // Not our log level - ignore
        //else {
        //
        //}
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
