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

package com.raffaeleconforti.processdiscoverer.impl.collectors;

import com.raffaeleconforti.processdiscoverer.impl.VisualizationAggregation;
import com.raffaeleconforti.processdiscoverer.impl.VisualizationType;
import com.raffaeleconforti.processdiscoverer.impl.Arc;
import com.raffaeleconforti.processdiscoverer.impl.util.FrequencySetPopulator;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectDoubleHashMap;

import java.util.Map;

import static com.raffaeleconforti.processdiscoverer.impl.VisualizationType.DURATION;
import static com.raffaeleconforti.processdiscoverer.impl.VisualizationType.FREQUENCY;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 15/07/2018.
 */
public class ArcInfoCollector {

    private final int number_of_traces;

    private final Map<Arc, LongArrayList> arcs_frequency_set;
    private final Map<Arc, LongArrayList> arcs_duration_set;
    private final Map<Arc, DoubleArrayList> arcs_impact_set;
    private final Map<Arc, LongArrayList> tmp_arcs_impact_set;

    private final Calculator calculator;

    private int trace = 0;

    public ArcInfoCollector(int number_of_traces) {
        this.number_of_traces = number_of_traces;

        this.arcs_frequency_set = new UnifiedMap<>();
        this.arcs_duration_set = new UnifiedMap<>();
        this.arcs_impact_set = new UnifiedMap<>();
        this.tmp_arcs_impact_set = new UnifiedMap<>();

        this.calculator = new Calculator();
        calculator.method9(Long.toString(System.currentTimeMillis()));
    }

    public ObjectDoubleHashMap<Arc> getArcsFrequencyMap(VisualizationType type, VisualizationAggregation aggregation) {
        ObjectDoubleHashMap map = new ObjectDoubleHashMap();
        for(Arc arc : arcs_frequency_set.keySet()) {
            map.put(arc, getArcInfo(arc, type, aggregation));
        }
        return map;
    }

    public void updateArcFrequency(Arc arc, int frequency) {
        LongArrayList list = FrequencySetPopulator.retreiveEntryLong(arcs_frequency_set, arc, number_of_traces);
        calculator.method10(calculator.method5(), list.get(trace), frequency);
        list.set(trace, calculator.method6());
    }

    public void updateArcDuration(Arc arc, long duration) {
        LongArrayList durations = arcs_duration_set.get(arc);
        if(durations == null) {
            durations = new LongArrayList();
            arcs_duration_set.put(arc, durations);
        }
        durations.add(duration);
    }

    public void updateArcImpact(Arc arc, long duration) {
        LongArrayList impacts = tmp_arcs_impact_set.get(arc);
        if(impacts == null) {
            impacts = new LongArrayList();
            tmp_arcs_impact_set.put(arc, impacts);
        }
        impacts.add(duration);
    }

    public void consolidateArcImpact(Arc arc, long total_duration) {
        DoubleArrayList list = FrequencySetPopulator.retreiveEntryDouble(arcs_impact_set, arc, number_of_traces);
        LongArrayList impacts = tmp_arcs_impact_set.get(arc);
        double impact = (double) impacts.sum() / ((double) total_duration);
        list.set(trace, list.get(trace) + impact);
    }

    public boolean exists(Arc arc) {
        return arcs_frequency_set.get(arc) != null;
    }

    public double getArcInfo(Arc arc, VisualizationType type, VisualizationAggregation aggregation) {
        if(type == FREQUENCY) {
            return FrequencySetPopulator.getAggregateInformation(arcs_frequency_set.get(arc), aggregation);
        }else if(type == DURATION){
            return FrequencySetPopulator.getAggregateInformation(arcs_duration_set.get(arc), aggregation);
        }
        return 0;
    }

    public void nextTrace() {
        tmp_arcs_impact_set.clear();
        calculator.method10(calculator.method5(), trace, 1);
        trace = (int) calculator.method6();
    }
}
