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
import com.raffaeleconforti.processdiscoverer.impl.util.Container;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 15/07/2018.
 */
public abstract class LogFilterCriterionImpl implements LogFilterCriterion {

    protected final String timestamp_code = new String(Container.var1[122], StandardCharsets.UTF_8);

    protected final String label;
    protected final String attribute;
    protected final Set<String> value;
    private final Action action;
    protected final Containment containment;
    protected final Level level;
    private final int hashCode;

    protected LogFilterCriterionImpl(Action action, Containment containment, Level level, String label, String attribute, Set<String> value) {
        this.label = label;
        this.action = action;
        this.containment = containment;
        this.level = level;
        this.attribute = attribute;
        this.value = value;
        this.hashCode = new HashCodeBuilder().append(level).append(containment).append(action).append(attribute).append(value).hashCode();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public Containment getContainment() {
        return containment;
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public String getAttribute() {
        return attribute;
    }

    @Override
    public Set<String> getValue() {
        return value;
    }

    @Override
    public boolean isToRemove(XTrace trace) {
        boolean matches = matchesCriterion(trace);
        if (matches && action == Action.REMOVE) return true;
        else return !matches && action == Action.RETAIN;
    }

    @Override
    public boolean isToRemove(XEvent event) {
        boolean matches = matchesCriterion(event);
        if (matches && action == Action.REMOVE) return true;
        else return !matches && action == Action.RETAIN;
    }

    protected abstract boolean matchesCriterion(XTrace trace);

    protected abstract boolean matchesCriterion(XEvent event);

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof LogFilterCriterionImpl) {
            LogFilterCriterionImpl logFilterCriterion = (LogFilterCriterionImpl) o;
            return this.level == logFilterCriterion.level &&
                    this.containment == logFilterCriterion.containment &&
                    this.action == logFilterCriterion.action &&
                    this.attribute.equals(logFilterCriterion.attribute) &&
                    this.value.equals(logFilterCriterion.value);
        }
        return false;
    }

    @Override
    public String toString() {
        String string = "";
        if(action == Action.RETAIN) {
            string += "Retain ";
        }else {
            string += "Remove ";
        }

        String values = "[";
        int count = value.size() - 1;
        for(String v : value) {
            values += v;
            if(count > 0) {
                values += " OR ";
                count--;
            }
        }
        values += "]";

        if(level == Level.EVENT) {
            string += "all events where attribute " + attribute + " is equal to " + values;
        }else {
            string += "all traces ";
            if(containment == Containment.CONTAIN_ANY) {
                string += "containing an event where attribute " + attribute + " is equal to " + values;
            }else {
                string += "where attribute " + attribute + " is equal to " + values + " for all events";
            }
        }

        return string;
    }
}
