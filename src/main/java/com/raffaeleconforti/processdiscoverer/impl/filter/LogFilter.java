/*
 *  Copyright (C) 2018 Raffaele Conforti (www.raffaeleconforti.com)
 *
 *  This project is dual licensed under GNU Affero General Public License and Raffaele Conforti License.
 *  You can choose between one of them if you use this work
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included
 *  in all copies or substantial portions of the Software.
 *
 *  When this software (or parts of it) is being used in a website or
 *  application, the message "Process Discover - raffaeleconforti.com"
 *  must stay fully visible to the user and not visually overlapped by other elements.
 *  The message must be showed using a 12 point font size minimum and must
 *  appear on the screen for the entire duration of the usage and a minimum of 30
 *  seconds.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 *  OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *
 */

package com.raffaeleconforti.processdiscoverer.impl.filter;

import com.raffaeleconforti.processdiscoverer.LogFilterCriterion;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class LogFilter {

    private static final XFactory factory = new XFactoryNaiveImpl();

    public static XLog filter(XLog log, List<LogFilterCriterion> criteria) {
        log = cloneLog(log);
        Iterator<XTrace> traceIterator = log.iterator();
        while (traceIterator.hasNext()) {
            XTrace trace = traceIterator.next();
            if(criteria != null) {
                for (int i = 0; i < criteria.size(); i++) {
                    LogFilterCriterion criterion = criteria.get(i);
                    if (criterion.getLevel() == Level.TRACE) {
                        if(criterion.isToRemove(trace)) {
                            traceIterator.remove();
                            break;
                        }
                    } else {
                        Iterator<XEvent> eventIterator = trace.iterator();
                        while (eventIterator.hasNext()) {
                            XEvent event = eventIterator.next();
                            for (int j = i; j < criteria.size(); j++) {
                                LogFilterCriterion criterion1 = criteria.get(j);
                                if (criterion1.getLevel() == Level.TRACE) break;
                                if (criterion1.isToRemove(event)) {
                                    eventIterator.remove();
                                    break;
                                }
                            }
                        }
                        if(trace.size() == 0) traceIterator.remove();
                    }
                }
            }
        }
        return log;
    }

    private static XLog cloneLog(XLog log) {
        XLog newLog = factory.createLog(log.getAttributes());
        for (XTrace trace : log) {
            XTrace newTrace = getXTrace(trace);
            newLog.add(newTrace);
        }

        return newLog;
    }

    private static XTrace getXTrace(XTrace trace) {
        XTrace newTrace = factory.createTrace(trace.getAttributes());
        newTrace.addAll(trace);
        return newTrace;
    }
}
