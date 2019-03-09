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

package com.raffaeleconforti.processdiscoverer.impl.filter.impl;

import com.raffaeleconforti.processdiscoverer.impl.filter.Action;
import com.raffaeleconforti.processdiscoverer.impl.filter.Containment;
import com.raffaeleconforti.processdiscoverer.impl.filter.Level;
import com.raffaeleconforti.processdiscoverer.impl.filter.LogFilterCriterionImpl;
import com.raffaeleconforti.processdiscoverer.impl.util.Container;
import com.raffaeleconforti.processdiscoverer.impl.util.Convertor;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Set;

public class LogFilterCriterionDuration extends LogFilterCriterionImpl {

    public LogFilterCriterionDuration(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        super(action, containment, level, label, attribute, value);
    }

    @Override
    public boolean matchesCriterion(XTrace trace) {
        if(level == Level.TRACE) {
            long s = Long.MAX_VALUE;
            long e = 0;
            for (XEvent event : trace) {
                s = Math.min(s, ((XAttributeTimestamp) event.getAttributes().get(timestamp_code)).getValueMillis());
                e = Math.max(e, ((XAttributeTimestamp) event.getAttributes().get(timestamp_code)).getValueMillis());
            }
            long d = e - s;

            for(String v : value) {

                String[] h = Convertor.convertFrom(Double.parseDouble(v.substring(1)));

                double seconds = 1000.0;
                double minutes = seconds * 60.0;
                double hours = minutes * 60.0;
                double days = hours * 24.0;
                double weeks = days * 7.0;
                double months = days * 30.0;
                double years = days * 365.0;

                double x = 0;
                if(h[1].equals("0")) x = Double.parseDouble(h[0]) * years;
                else if(h[1].equals("1")) x = Double.parseDouble(h[0]) * months;
                else if(h[1].equals("2")) x = Double.parseDouble(h[0]) * weeks;
                else if(h[1].equals("3")) x = Double.parseDouble(h[0]) * days;
                else if(h[1].equals("4")) x = Double.parseDouble(h[0]) * hours;
                else if(h[1].equals("5")) x = Double.parseDouble(h[0]) * minutes;
                else if(h[1].equals("6")) x = Double.parseDouble(h[0]) * seconds;

                if(v.startsWith(">")) {
                    return d >= x;
                }
                if(v.startsWith("<")) {
                    return d <= x;
                }
            }
        }
        return false;
    }

    @Override
    public boolean matchesCriterion(XEvent event) {
        return false;
    }

}
