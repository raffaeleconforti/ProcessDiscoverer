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

package com.raffaeleconforti.processdiscoverer.impl.selectors;

import com.raffaeleconforti.processdiscoverer.impl.VisualizationAggregation;
import com.raffaeleconforti.processdiscoverer.impl.VisualizationType;
import com.raffaeleconforti.processdiscoverer.impl.collectors.NodeInfoCollector;
import com.raffaeleconforti.processdiscoverer.impl.collectors.Calculator;
import com.raffaeleconforti.processdiscoverer.impl.logprocessors.EventNameAnalyser;
import org.eclipse.collections.api.iterator.MutableIntIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.primitive.IntDoublePair;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntDoubleHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.Comparator;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class NodeSelector {

    private final EventNameAnalyser eventNameAnalyser = new EventNameAnalyser();
    private final int start_int = 1;
    private final int end_int = 2;
    private final boolean contain_start_events;
    private final HashBiMap<String, Integer> simplified_names;

    private final MutableList<IntDoublePair> sorted_activity_frequency;
    private final IntHashSet retained_activities;
    private final Calculator calculator;
    private double threshold = 0.0;
    private double max;
    private double min;
    private boolean inverted;

    public NodeSelector(NodeInfoCollector nodeInfoCollector, double activities, boolean contain_start_events, VisualizationType type, VisualizationAggregation aggregation, boolean inverted) {
        this.inverted = inverted;
        this.contain_start_events = contain_start_events;
        this.simplified_names = nodeInfoCollector.getSimplified_names();

        this.calculator = new Calculator();
        calculator.method9(Long.toString(System.currentTimeMillis()));

        retained_activities = new IntHashSet();
        retained_activities.add(start_int);
        retained_activities.add(end_int);

        IntDoubleHashMap activity_frequency = nodeInfoCollector.getActivityFrequencyMap(type, aggregation);

        if(activity_frequency.size() > 0) {
            calculator.method10(calculator.method5(), (long) activity_frequency.min(), 1);
            min = calculator.method6();
            calculator.method10(calculator.method5(), (long) activity_frequency.max(), 1);
            max = calculator.method6();
            threshold = getLog((1 + max) - min) * activities;
        }

        sorted_activity_frequency = activity_frequency.keyValuesView().toList();
        sorted_activity_frequency.sort(new Comparator<IntDoublePair>() {
            @Override
            public int compare(IntDoublePair o1, IntDoublePair o2) {
                return Double.compare(o2.getTwo(), o1.getTwo());
            }
        });
    }

    public IntHashSet selectActivities() {
        for(int i = 0; i < sorted_activity_frequency.size(); i++) {
            calculator.method10(calculator.method5(), (long) sorted_activity_frequency.get(i).getTwo(), 1);
            double current = scale(calculator.method6());
            if(current >= threshold) {
                retained_activities.add(sorted_activity_frequency.get(i).getOne());
            }
        }

        if(contain_start_events) {
            MutableIntIterator iterator = retained_activities.intIterator();
            while (iterator.hasNext()) {
                int i = iterator.next();
                String name = getEventFullName(i);
                String name_to_check = "";

                if (eventNameAnalyser.isStartEvent(name)) name_to_check = eventNameAnalyser.getCompleteEvent(name);
                else if (eventNameAnalyser.isCompleteEvent(name)) name_to_check = eventNameAnalyser.getStartEvent(name);

                if (!isSingleTypeEvent(getEventNumber(name)) && !retained_activities.contains(getEventNumber(name_to_check))) {
                    iterator.remove();
                }
            }
        }
        return retained_activities;
    }

    private double scale(long value) {
        double v = (value - min) + 1;
        return (inverted) ? getLog((((1 + max) - min) - v) + 1) : getLog(v);
    }

    private double getLog(double value) {
        return (Math.log10(value) / Math.log10(2));
    }

    private String getEventFullName(int event) {
        return simplified_names.inverse().get(event);
    }

    private Integer getEventNumber(String event) {
        return simplified_names.get(event);
    }

    private boolean isSingleTypeEvent(int event) {
        String name = getEventFullName(event);
        if(eventNameAnalyser.isStartEvent(name) && getEventNumber(eventNameAnalyser.getCompleteEvent(name)) != null) return false;
        return !eventNameAnalyser.isCompleteEvent(name) || getEventNumber(eventNameAnalyser.getStartEvent(name)) == null;
    }
}
