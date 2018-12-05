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

package com.raffaeleconforti.processdiscoverer.impl.util;

import com.raffaeleconforti.processdiscoverer.impl.Arc;
import com.raffaeleconforti.processdiscoverer.impl.VisualizationAggregation;
import com.raffaeleconforti.statistics.StatisticsSelector;
import org.eclipse.collections.impl.block.factory.primitive.DoublePredicates;
import org.eclipse.collections.impl.block.factory.primitive.LongPredicates;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

import java.util.Map;

import static com.raffaeleconforti.processdiscoverer.impl.VisualizationAggregation.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class FrequencySetPopulator {

    public static double getAggregateInformation(LongArrayList list, VisualizationAggregation aggregation) {
        Double result = -Double.MAX_VALUE;
        if(aggregation == TOTAL) result = (double) list.sum();
        else if(aggregation == CASES) result = (double) list.count(LongPredicates.greaterThan(0));
        else if(aggregation == MAX) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MAX, null, list.toArray());
        else if(aggregation == MIN) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MAX, null, list.toArray());
        else if(aggregation == MEAN) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MEAN, null, list.toArray());
        else if(aggregation == MEDIAN) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MEDIAN, null, list.toArray());
        else if(aggregation == MODE) result = (new StatisticsSelector()).evaluate(StatisticsSelector.StatisticsMeasures.MODE, null, list.toArray());
        return result;
    }

    public static LongArrayList retreiveEntryLong(Map<Arc, LongArrayList> set, Arc key, int length) {
        LongArrayList list;
        if((list = set.get(key)) == null) {
            list = createEntryLong(length);
            set.put(key, list);
        }
        return list;
    }

    public static DoubleArrayList retreiveEntryDouble(Map<Arc, DoubleArrayList> set, Arc key, int length) {
        DoubleArrayList list;
        if((list = set.get(key)) == null) {
            list = createEntryDouble(length);
            set.put(key, list);
        }
        return list;
    }

    public static LongArrayList retreiveEntry(IntObjectHashMap<LongArrayList> set, int key, int length) {
        LongArrayList list;
        if((list = set.get(key)) == null) {
            list = createEntryLong(length);
            set.put(key, list);
        }
        return list;
    }

    private static LongArrayList createEntryLong(int length) {
        LongArrayList list = new LongArrayList(length);
        for(int i = 0; i < length; i++) list.add(0);
        return list;
    }

    private static DoubleArrayList createEntryDouble(int length) {
        DoubleArrayList list = new DoubleArrayList(length);
        for(int i = 0; i < length; i++) list.add(0);
        return list;
    }

}
