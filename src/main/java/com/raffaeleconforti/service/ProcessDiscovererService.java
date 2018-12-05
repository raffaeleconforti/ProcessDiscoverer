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

package com.raffaeleconforti.service;

import com.raffaeleconforti.processdiscoverer.LogFilterCriterion;
import com.raffaeleconforti.processdiscoverer.impl.SearchStrategy;
import com.raffaeleconforti.processdiscoverer.impl.VisualizationAggregation;
import com.raffaeleconforti.processdiscoverer.impl.VisualizationType;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public interface ProcessDiscovererService {

    Object[] generateJSONFromBPMNDiagram(BPMNDiagram diagram);
    Object[] generateJSONFromLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    Object[] generateJSONWithGatewaysFromLog(XLog log, String attribute, double activities, double arcs, double parallelism, boolean preserve_connectivity, boolean prioritize_parallelism, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    JSONArray generateTraceModel(XLog log, String traceID, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    BPMNDiagram generateDFGFromLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    BPMNDiagram insertBPMNGateways(BPMNDiagram bpmnDiagram);
    XLog generateFilteredLog(XLog log, String attribute, double activities, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    XLog generateFilteredFittedLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria, SearchStrategy searchStrategy);

}
