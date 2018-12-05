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
import com.raffaeleconforti.processdiscoverer.impl.ProcessDiscovererImpl;
import com.raffaeleconforti.processdiscoverer.impl.SearchStrategy;
import com.raffaeleconforti.processdiscoverer.impl.VisualizationAggregation;
import com.raffaeleconforti.processdiscoverer.impl.VisualizationType;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
@Service
public class ProcessDiscovererServiceImpl extends DefaultParameterAwarePlugin implements ProcessDiscovererService {
    
    @Override
    public Object[] generateJSONFromBPMNDiagram(BPMNDiagram bpmnDiagram) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(null);
        return processDiscoverer.generateJSONFromBPMNDiagram(bpmnDiagram);
    }

    @Override
    public Object[] generateJSONFromLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateJSONFromLog(attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, secondary, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public Object[] generateJSONWithGatewaysFromLog(XLog log, String attribute, double activities, double arcs, double parallelism, boolean preserve_connectivity, boolean prioritize_parallelism, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateJSONWithGatewaysFromLog(attribute, activities, arcs, parallelism, preserve_connectivity, prioritize_parallelism, inverted_nodes, inverted_arcs, secondary, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public JSONArray generateTraceModel(XLog log, String traceID, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateTraceModel(traceID, attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, secondary, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public BPMNDiagram generateDFGFromLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateDFGFromLog(attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public BPMNDiagram insertBPMNGateways(BPMNDiagram bpmnDiagram) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(null);
        return processDiscoverer.insertBPMNGateways(bpmnDiagram);
    }

    @Override
    public XLog generateFilteredLog(XLog log, String attribute, double activities, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateFilteredLog(attribute, activities, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public XLog generateFilteredFittedLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria, SearchStrategy searchStrategy) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateFilteredFittedLog(attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria, searchStrategy);
    }

}
