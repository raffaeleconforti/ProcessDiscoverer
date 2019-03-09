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

package com.raffaeleconforti.processdiscoverer;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.raffaeleconforti.processdiscoverer.impl.util.Container;
import com.raffaeleconforti.processdiscoverer.impl.util.Convertor;
import com.raffaeleconforti.service.ProcessDiscovererService;
import com.raffaeleconforti.context.FakePluginContext;
import com.raffaeleconforti.conversion.bpmn.BPMNToPetriNetConverter;
import com.raffaeleconforti.processdiscoverer.impl.SearchStrategy;
import com.raffaeleconforti.processdiscoverer.impl.VisualizationAggregation;
import com.raffaeleconforti.processdiscoverer.impl.VisualizationType;
import com.raffaeleconforti.processdiscoverer.impl.filter.LogFilterCriterionFactory;
import nl.tue.astar.AStarException;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.helper.Version;
import org.apromore.model.LogSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.loganimation.LogAnimationPluginInterface;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.portal.dialogController.SelectDynamicListController;
import org.apromore.service.CanoniserService;
import org.apromore.service.DomainService;
import org.apromore.service.EventLogService;
import org.apromore.service.ProcessService;
import org.apromore.service.bpmndiagramimporter.BPMNDiagramImporter;
import org.apromore.service.helper.UserInterfaceHelper;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.*;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.json.JSONArray;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Flow;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.bpmn.BpmnDefinitions;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import javax.swing.*;
import javax.xml.datatype.DatatypeFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;

import static com.raffaeleconforti.processdiscoverer.impl.filter.Action.REMOVE;
import static com.raffaeleconforti.processdiscoverer.impl.filter.Action.RETAIN;
import static com.raffaeleconforti.processdiscoverer.impl.filter.Containment.CONTAIN_ANY;
import static com.raffaeleconforti.processdiscoverer.impl.filter.Level.EVENT;
import static com.raffaeleconforti.processdiscoverer.impl.filter.Level.TRACE;
import static com.raffaeleconforti.processdiscoverer.impl.VisualizationAggregation.*;
import static com.raffaeleconforti.processdiscoverer.impl.VisualizationType.DURATION;
import static com.raffaeleconforti.processdiscoverer.impl.VisualizationType.FREQUENCY;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class ProcessDiscovererController {

    private final DecimalFormat decimalFormat = new DecimalFormat(new String(new byte[]{0x23,0x23,0x23,0x23,0x23,0x23,0x23,0x23,0x23,0x23,0x23,0x23,0x23,0x23,0x30,0x2e,0x23,0x23}, StandardCharsets.UTF_8));
    private final String nativeType = "BPMN 2.0";

    PortalContext portalContext;
    private com.raffaeleconforti.service.ProcessDiscovererService processDiscovererService;
    private EventLogService eventLogService;

    private Radio use_fixed;
    private Radio use_dynamic;
    private Checkbox gateways;
    private Checkbox secondary;
    private Checkbox inverted_nodes;
    private Checkbox inverted_arcs;

    private Intbox activitiesText;
    private Intbox arcsText;
    private Intbox parallelismText;
    private Slider activities;
    private Slider arcs;
    private Slider parallelism;

    private Menupopup selector;

    private Combobutton frequency;
    private Menuitem case_frequency;
    private Menuitem absolute_frequency;
    private Menuitem median_frequency;
    private Menuitem mean_frequency;
    private Menuitem mode_frequency;
    private Menuitem max_frequency;
    private Menuitem min_frequency;

    private Combobutton duration;
    private Menuitem total_duration;
    private Menuitem median_duration;
    private Menuitem mean_duration;
    private Menuitem max_duration;
    private Menuitem min_duration;

    private Button filter;
    private Button details;
    private Button cases;
    private Button fitness;
    private Button animate;
    private Menuitem exportUnfitted;

    private Label caseNumber;
    private Label uniquecaseNumber;
    private Label activityNumber;
    private Label eventNumber;
    private Label meanDuration;
    private Label medianDuration;
    private Label maxDuration;
    private Label minDuration;

    private int arcs_value = 10;
    private int parallelism_value = 40;
    private int activities_value = 100;

    private VisualizationType fixedType = FREQUENCY;
    private VisualizationAggregation fixedAggregation = TOTAL;

    private VisualizationType primaryType = FREQUENCY;
    private VisualizationAggregation primaryAggregation = TOTAL;
    private VisualizationType secondaryType = DURATION;
    private VisualizationAggregation secondaryAggregation = MEDIAN;

    public boolean visualized = false;
    private String log_name = "";
    private XLog log;
    private BPMNDiagram diagram;
    private LogSummaryType logSummary;

    private List<LogFilterCriterion> criteria;
    private Map<String, Map<String, Integer>> options_frequency = new HashMap<>();
    private long min = Long.MAX_VALUE;
    private long max = 0;

    private String label = new String(Container.var1[161], StandardCharsets.UTF_8);

    private DomainService domainService;
    private ProcessService processService;
    private CanoniserService canoniserService;
    private LogAnimationPluginInterface logAnimationPlugin;
    private UserInterfaceHelper userInterfaceHelper;

    public ProcessDiscovererController(PortalContext context, EventLogService eventLogService, ProcessDiscovererService processDiscovererService,
                                       CanoniserService canoniserService, DomainService domainService, ProcessService processService, BPMNDiagramImporter importerService,
                                       UserInterfaceHelper userInterfaceHelper, LogAnimationPluginInterface logAnimationPlugin, VisualizationType fixedType) throws Exception {

        this.domainService = domainService;
        this.processService = processService;
        this.canoniserService = canoniserService;
        this.logAnimationPlugin = logAnimationPlugin;
        this.userInterfaceHelper = userInterfaceHelper;
        this.portalContext = context;
        this.processDiscovererService = processDiscovererService;
        this.eventLogService = eventLogService;
        this.primaryType = fixedType;
        if (primaryType != FREQUENCY) primaryAggregation = MEAN;

        Map<SummaryType, List<VersionSummaryType>> elements = context.getSelection().getSelectedProcessModelVersions();
        if (elements.size() != 1) {
            Messagebox.show(new String(Container.var1[9], StandardCharsets.UTF_8), new String(Container.var1[10], StandardCharsets.UTF_8), Messagebox.OK, Messagebox.INFORMATION);
            return;
        }
        SummaryType summary = elements.keySet().iterator().next();
        if (!(summary instanceof LogSummaryType)) {
            ProcessSummaryType process = (ProcessSummaryType) summary;
            VersionSummaryType vst = (VersionSummaryType) elements.get(summary).get(elements.get(summary).size() - 1);
            int procID = process.getId();
            String procName = process.getName();
            String branch = vst.getName();
            Version version = new Version(vst.getVersionNumber());

            String model = processService.getBPMNRepresentation(procName, procID, branch, version);
            String startevent = model.substring(model.indexOf(new String(Container.var1[11], StandardCharsets.UTF_8)));
            startevent = startevent.substring(0, startevent.indexOf(new String(Container.var1[13], StandardCharsets.UTF_8)) + 2);
            if (startevent.contains(new String(Container.var1[14], StandardCharsets.UTF_8))) {
                String newStartEvent = startevent.replace(new String(Container.var1[14], StandardCharsets.UTF_8), "");
                model = model.replace(startevent, newStartEvent);
            }
            String endevent = model.substring(model.indexOf(new String(Container.var1[12], StandardCharsets.UTF_8)));
            endevent = endevent.substring(0, endevent.indexOf(new String(Container.var1[13], StandardCharsets.UTF_8)) + 2);
            if (endevent.contains(new String(Container.var1[14], StandardCharsets.UTF_8))) {
                String newEndEvent = endevent.replace(new String(Container.var1[14], StandardCharsets.UTF_8), "");
                model = model.replace(endevent, newEndEvent);
            }
            diagram = importerService.importBPMNDiagram(model);
        } else {
            logSummary = (LogSummaryType) summary;
            log_name = logSummary.getName();
            log = eventLogService.getXLog(logSummary.getId());
            generateOptions(log);
            criteria = new ArrayList<>();
        }
        start();
    }

    private void start() {
        try {
            Window slidersWindow;
            if(log == null) {
                slidersWindow = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), new String(Container.var1[15], StandardCharsets.UTF_8), null, null);
                slidersWindow.setTitle(new String(new byte[]{0x42,0x50,0x4d,0x4e,0x20,0x56,0x69,0x73,0x75,0x61,0x6c,0x69,0x7a,0x65,0x72,0x20,0x2d,0x20,0x72,0x61,0x66,0x66,0x61,0x65,0x6c,0x65,0x63,0x6f,0x6e,0x66,0x6f,0x72,0x74,0x69,0x2e,0x63,0x6f,0x6d}, StandardCharsets.UTF_8));
            }else {
                slidersWindow = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), new String(Container.var1[16], StandardCharsets.UTF_8), null, null);
                slidersWindow.setTitle(new String(new byte[]{0x50,0x72,0x6f,0x63,0x65,0x73,0x73,0x20,0x44,0x69,0x73,0x63,0x6f,0x76,0x65,0x72,0x65,0x72,0x20,0x2d,0x20,0x72,0x61,0x66,0x66,0x61,0x65,0x6c,0x65,0x63,0x6f,0x6e,0x66,0x6f,0x72,0x74,0x69,0x2e,0x63,0x6f,0x6d}, StandardCharsets.UTF_8));

                this.use_fixed = (Radio) slidersWindow.getFellow(new String(Container.var1[20], StandardCharsets.UTF_8));
                this.use_dynamic = (Radio) slidersWindow.getFellow(new String(Container.var1[21], StandardCharsets.UTF_8));
                this.gateways = (Checkbox) slidersWindow.getFellow(new String(Container.var1[23], StandardCharsets.UTF_8));
                this.secondary = (Checkbox) slidersWindow.getFellow(new String(Container.var1[25], StandardCharsets.UTF_8));
                this.inverted_nodes = (Checkbox) slidersWindow.getFellow(new String(Container.var1[26], StandardCharsets.UTF_8));
                this.inverted_arcs = (Checkbox) slidersWindow.getFellow(new String(Container.var1[27], StandardCharsets.UTF_8));

                this.activities = (Slider) slidersWindow.getFellow(new String(Container.var1[28], StandardCharsets.UTF_8));
                this.arcs = (Slider) slidersWindow.getFellow(new String(Container.var1[29], StandardCharsets.UTF_8));
                this.parallelism = (Slider) slidersWindow.getFellow(new String(Container.var1[30], StandardCharsets.UTF_8));
                this.activitiesText = (Intbox) slidersWindow.getFellow(new String(Container.var1[31], StandardCharsets.UTF_8));
                this.arcsText = (Intbox) slidersWindow.getFellow(new String(Container.var1[32], StandardCharsets.UTF_8));
                this.parallelismText = (Intbox) slidersWindow.getFellow(new String(Container.var1[33], StandardCharsets.UTF_8));

                this.caseNumber = (Label) slidersWindow.getFellow(new String(Container.var1[34], StandardCharsets.UTF_8));
                this.uniquecaseNumber = (Label) slidersWindow.getFellow(new String(Container.var1[35], StandardCharsets.UTF_8));
                this.activityNumber = (Label) slidersWindow.getFellow(new String(Container.var1[36], StandardCharsets.UTF_8));
                this.eventNumber = (Label) slidersWindow.getFellow(new String(Container.var1[37], StandardCharsets.UTF_8));
                this.meanDuration = (Label) slidersWindow.getFellow(new String(Container.var1[38], StandardCharsets.UTF_8));
                this.medianDuration = (Label) slidersWindow.getFellow(new String(Container.var1[39], StandardCharsets.UTF_8));
                this.maxDuration = (Label) slidersWindow.getFellow(new String(Container.var1[40], StandardCharsets.UTF_8));
                this.minDuration = (Label) slidersWindow.getFellow(new String(Container.var1[41], StandardCharsets.UTF_8));

                this.selector = (Menupopup) slidersWindow.getFellow(new String(Container.var1[42], StandardCharsets.UTF_8));

                this.frequency = (Combobutton) slidersWindow.getFellow(new String(Container.var1[43], StandardCharsets.UTF_8));
                this.absolute_frequency = (Menuitem) slidersWindow.getFellow(new String(Container.var1[44], StandardCharsets.UTF_8));
                this.case_frequency = (Menuitem) slidersWindow.getFellow(new String(Container.var1[45], StandardCharsets.UTF_8));
                this.median_frequency = (Menuitem) slidersWindow.getFellow(new String(Container.var1[46], StandardCharsets.UTF_8));
                this.mean_frequency = (Menuitem) slidersWindow.getFellow(new String(Container.var1[47], StandardCharsets.UTF_8));
                this.mode_frequency = (Menuitem) slidersWindow.getFellow(new String(Container.var1[48], StandardCharsets.UTF_8));
                this.max_frequency = (Menuitem) slidersWindow.getFellow(new String(Container.var1[49], StandardCharsets.UTF_8));
                this.min_frequency = (Menuitem) slidersWindow.getFellow(new String(Container.var1[50], StandardCharsets.UTF_8));

                this.duration = (Combobutton) slidersWindow.getFellow(new String(Container.var1[51], StandardCharsets.UTF_8));
                this.total_duration = (Menuitem) slidersWindow.getFellow(new String(Container.var1[52], StandardCharsets.UTF_8));
                this.median_duration = (Menuitem) slidersWindow.getFellow(new String(Container.var1[53], StandardCharsets.UTF_8));
                this.mean_duration = (Menuitem) slidersWindow.getFellow(new String(Container.var1[54], StandardCharsets.UTF_8));
                this.max_duration = (Menuitem) slidersWindow.getFellow(new String(Container.var1[55], StandardCharsets.UTF_8));
                this.min_duration = (Menuitem) slidersWindow.getFellow(new String(Container.var1[56], StandardCharsets.UTF_8));

                this.details = (Button) slidersWindow.getFellow(new String(Container.var1[63], StandardCharsets.UTF_8));
                this.cases = (Button) slidersWindow.getFellow(new String(Container.var1[64], StandardCharsets.UTF_8));
                this.fitness = (Button) slidersWindow.getFellow(new String(Container.var1[65], StandardCharsets.UTF_8));
                this.filter = (Button) slidersWindow.getFellow(new String(Container.var1[66], StandardCharsets.UTF_8));
                this.animate = (Button) slidersWindow.getFellow(new String(Container.var1[67], StandardCharsets.UTF_8));

                this.exportUnfitted = (Menuitem) slidersWindow.getFellow(new String(Container.var1[69], StandardCharsets.UTF_8));
            }

            Combobutton export = (Combobutton) slidersWindow.getFellow(new String(Container.var1[70], StandardCharsets.UTF_8));
            Menuitem downloadPDF = (Menuitem) slidersWindow.getFellow(new String(Container.var1[71], StandardCharsets.UTF_8));
            Menuitem downloadPNG = (Menuitem) slidersWindow.getFellow(new String(Container.var1[72], StandardCharsets.UTF_8));
            Menuitem exportBPMN = (Menuitem) slidersWindow.getFellow(new String(Container.var1[73], StandardCharsets.UTF_8));

            if(log != null) {
                populateMetrics(log);

                for (String option : generateLabels(log)) {
                    Menuitem item = new Menuitem(option);
                    item.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), new EventListener<Event>() {
                        public void onEvent(Event event) throws Exception {
                            setLabel(item.getLabel());
                            visualized = false;
                            options_frequency.clear();
                            generateOptions(log);
                            populateMetrics(log);
                            setArcAndActivityRatios();
                        }
                    });
                    selector.appendChild(item);
                }

                EventListener<Event> radioListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        visualized = false;
                        setArcAndActivityRatios();
                    }
                };
                this.use_fixed.addEventListener(new String(Container.var1[75], StandardCharsets.UTF_8), radioListener);
                this.use_dynamic.addEventListener(new String(Container.var1[75], StandardCharsets.UTF_8), radioListener);
                this.gateways.addEventListener(new String(Container.var1[75], StandardCharsets.UTF_8), radioListener);
                this.secondary.addEventListener(new String(Container.var1[75], StandardCharsets.UTF_8), radioListener);
                this.inverted_nodes.addEventListener(new String(Container.var1[75], StandardCharsets.UTF_8), radioListener);
                this.inverted_arcs.addEventListener(new String(Container.var1[75], StandardCharsets.UTF_8), radioListener);

                this.activities.addEventListener(new String(Container.var1[76], StandardCharsets.UTF_8), new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        activitiesText.setValue(activities.getCurpos());
                        setArcAndActivityRatios();
                    }
                });
                this.arcs.addEventListener(new String(Container.var1[76], StandardCharsets.UTF_8), new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        arcsText.setValue(arcs.getCurpos());
                        setArcAndActivityRatios();
                    }
                });
                this.parallelism.addEventListener(new String(Container.var1[76], StandardCharsets.UTF_8), new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        parallelismText.setValue(parallelism.getCurpos());
                        setArcAndActivityRatios();
                    }
                });

                EventListener<Event> actChangeListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        int i = activitiesText.getValue();
                        if (i < 0) i = 0;
                        else if (i > 100) i = 100;
                        activitiesText.setValue(i);
                        activities.setCurpos(i);
                        setArcAndActivityRatios();
                    }
                };
                EventListener<Event> actChangingListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        String s = ((InputEvent) event).getValue();
                        if (!s.isEmpty()) {
                            int i = Integer.parseInt(s);
                            if (i < 0) i = 0;
                            else if (i > 100) i = 100;
                            activitiesText.setValue(i);
                            activities.setCurpos(i);
                            setArcAndActivityRatios();
                        }
                    }
                };
                this.activitiesText.addEventListener(new String(Container.var1[77], StandardCharsets.UTF_8), actChangeListener);
                this.activitiesText.addEventListener(new String(Container.var1[78], StandardCharsets.UTF_8), actChangingListener);

                EventListener<Event> arcChangeListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        int i = arcsText.getValue();
                        if (i < 0) i = 0;
                        else if (i > 100) i = 100;
                        arcsText.setValue(i);
                        arcs.setCurpos(i);
                        setArcAndActivityRatios();
                    }
                };
                EventListener<Event> arcChangingListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        String s = ((InputEvent) event).getValue();
                        if (!s.isEmpty()) {
                            int i = Integer.parseInt(s);
                            if (i < 0) i = 0;
                            else if (i > 100) i = 100;
                            arcsText.setValue(i);
                            arcs.setCurpos(i);
                            setArcAndActivityRatios();
                        }
                    }
                };
                this.arcsText.addEventListener(new String(Container.var1[77], StandardCharsets.UTF_8), arcChangeListener);
                this.arcsText.addEventListener(new String(Container.var1[78], StandardCharsets.UTF_8), arcChangingListener);

                EventListener<Event> parallelismChangeListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        int i = parallelismText.getValue();
                        if (i < 0) i = 0;
                        else if (i > 100) i = 100;
                        parallelismText.setValue(i);
                        parallelism.setCurpos(i);
                        setArcAndActivityRatios();
                    }
                };
                EventListener<Event> parallelismChangingListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        String s = ((InputEvent) event).getValue();
                        if (!s.isEmpty()) {
                            int i = Integer.parseInt(s);
                            if (i < 0) i = 0;
                            else if (i > 100) i = 100;
                            parallelismText.setValue(i);
                            parallelism.setCurpos(i);
                            setArcAndActivityRatios();
                        }
                    }
                };
                this.parallelismText.addEventListener(new String(Container.var1[77], StandardCharsets.UTF_8), parallelismChangeListener);
                this.parallelismText.addEventListener(new String(Container.var1[78], StandardCharsets.UTF_8), parallelismChangingListener);

                EventListener<Event> frequencyListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        visualizeFrequency();
                    }
                };
                this.frequency.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), frequencyListener);
                this.absolute_frequency.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), frequencyListener);
                this.case_frequency.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), frequencyListener);
                this.median_frequency.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), frequencyListener);
                this.mean_frequency.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), frequencyListener);
                this.mode_frequency.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), frequencyListener);
                this.max_frequency.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), frequencyListener);
                this.min_frequency.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), frequencyListener);

                EventListener<Event> durationListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        visualizeDuration();
                    }
                };
                this.duration.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), durationListener);
                this.total_duration.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), durationListener);
                this.median_duration.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), durationListener);
                this.mean_duration.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), durationListener);
                this.max_duration.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), durationListener);
                this.min_duration.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), durationListener);

                this.exportUnfitted.addEventListener(new String(Container.var1[79], StandardCharsets.UTF_8), new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        activities_value = activities.getCurpos();
                        arcs_value = arcs.getCurpos();

                        XLog filtered_log = processDiscovererService.generateFilteredLog(log, getLabel(), 1 - activities.getCurposInDouble() / 100, inverted_nodes.isChecked(), inverted_arcs.isChecked(), fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
                        saveLog(filtered_log);
                    }
                });

                this.details.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        Window details_window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), new String(Container.var1[17], StandardCharsets.UTF_8), null, null);
                        details_window.setTitle(new String(new byte[]{0x44,0x65,0x74,0x61,0x69,0x6c,0x73,0x20,0x2d,0x20,0x72,0x61,0x66,0x66,0x61,0x65,0x6c,0x65,0x63,0x6f,0x6e,0x66,0x6f,0x72,0x74,0x69,0x2e,0x63,0x6f,0x6d}, StandardCharsets.UTF_8));
                        Listbox listbox = (Listbox) details_window.getFellow(new String(Container.var1[81], StandardCharsets.UTF_8));
                        Listheader pos = (Listheader) details_window.getFellow(new String(Container.var1[82], StandardCharsets.UTF_8));
                        pos.setSortAscending(new NumberComparator(true, 0));
                        pos.setSortDescending(new NumberComparator(false, 0));
                        Listheader detail_frequency = (Listheader) details_window.getFellow(new String(Container.var1[83], StandardCharsets.UTF_8));
                        detail_frequency.setSortAscending(new NumberComparator(true, 2));
                        detail_frequency.setSortDescending(new NumberComparator(false, 2));
                        Listheader detail_ratio = (Listheader) details_window.getFellow(new String(Container.var1[84], StandardCharsets.UTF_8));
                        detail_ratio.setSortAscending(new NumberComparator(true, 2));
                        detail_ratio.setSortDescending(new NumberComparator(false, 2));

                        int i = 1;
                        for (String key : options_frequency.get(getLabel()).keySet()) {
                            Listcell listcell0 = new Listcell(Integer.toString(i));
                            Listcell listcell1 = new Listcell(key);
                            Listcell listcell2 = new Listcell(options_frequency.get(getLabel()).get(key).toString());
                            Listcell listcell3 = new Listcell(decimalFormat.format(100 * ((double) options_frequency.get(getLabel()).get(key) / Double.parseDouble(eventNumber.getValue()))) + "%");
                            Listitem listitem = new Listitem();
                            listitem.appendChild(listcell0);
                            listitem.appendChild(listcell1);
                            listitem.appendChild(listcell2);
                            listitem.appendChild(listcell3);
                            listbox.appendChild(listitem);
                            i++;
                        }

                        listbox.setRows(5);

                        Button save = (Button) details_window.getFellow("save");
                        save.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                Writer writer = new BufferedWriter(new OutputStreamWriter(baos));
                                CSVWriter csvWriter = new CSVWriter(writer);
                                csvWriter.writeNext(new String[] {"Activity", "Frequency", "Frequency %"});
                                for (String key : options_frequency.get(getLabel()).keySet()) {
                                    csvWriter.writeNext(new String[] {key, options_frequency.get(getLabel()).get(key).toString(), decimalFormat.format(100 * ((double) options_frequency.get(getLabel()).get(key) / Double.parseDouble(eventNumber.getValue()))) + "%"});
                                }
                                csvWriter.flush();
                                csvWriter.close();
                                byte[] buffer = baos.toByteArray();
                                ByteArrayInputStream is = new ByteArrayInputStream(buffer);
                                AMedia amedia = new AMedia("Details.csv", "csv", "application/file", is);
                                Filedownload.save(amedia);
                            }
                        });

                        details_window.doModal();
                    }
                });

                this.cases.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        Window cases_window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), new String(Container.var1[18], StandardCharsets.UTF_8), null, null);
                        cases_window.setTitle(new String(new byte[]{0x50,0x72,0x6f,0x63,0x65,0x73,0x73,0x20,0x49,0x6e,0x73,0x74,0x61,0x6e,0x63,0x65,0x73,0x20,0x2d,0x20,0x72,0x61,0x66,0x66,0x61,0x65,0x6c,0x65,0x63,0x6f,0x6e,0x66,0x6f,0x72,0x74,0x69,0x2e,0x63,0x6f,0x6d}, StandardCharsets.UTF_8));
                        Listbox listbox = (Listbox) cases_window.getFellow(new String(Container.var1[85], StandardCharsets.UTF_8));
                        Listheader pos = (Listheader) cases_window.getFellow(new String(Container.var1[82], StandardCharsets.UTF_8));
                        pos.setSortAscending(new NumberComparator(true, 0));
                        pos.setSortDescending(new NumberComparator(false, 0));
                        Listheader variant_value = (Listheader) cases_window.getFellow(new String(Container.var1[86], StandardCharsets.UTF_8));
                        variant_value.setSortAscending(new NumberComparator(true, 2));
                        variant_value.setSortDescending(new NumberComparator(false, 2));
                        Listheader case_length = (Listheader) cases_window.getFellow(new String(Container.var1[87], StandardCharsets.UTF_8));
                        case_length.setSortAscending(new NumberComparator(true, 2));
                        case_length.setSortDescending(new NumberComparator(false, 2));

                        Map<String, Integer[]> info = getCases();
                        int i = 1;
                        for (String key : info.keySet()) {
                            Listcell listcell0 = new Listcell(Integer.toString(i));
                            Listcell listcell1 = new Listcell(key);
                            Listcell listcell2 = new Listcell(info.get(key)[1].toString());
                            Listcell listcell3 = new Listcell(info.get(key)[0].toString());
                            Listitem listitem = new Listitem();
                            listitem.appendChild(listcell0);
                            listitem.appendChild(listcell1);
                            listitem.appendChild(listcell2);
                            listitem.appendChild(listcell3);

                            listbox.appendChild(listitem);
                            i++;
                        }

                        listbox.setRows(5);

                        listbox.addEventListener("onSelect", new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                try {
                                    String traceID = ((Listcell) (listbox.getSelectedItem()).getChildren().get(1)).getLabel();
                                    JSONArray array = processDiscovererService.generateTraceModel(log, traceID, getLabel(), 1 - activities.getCurposInDouble() / 100, 1 - arcs.getCurposInDouble() / 100, true, inverted_nodes.isChecked(), inverted_arcs.isChecked(), secondary.isChecked(), fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);

                                    String jsonString = array.toString();
                                    String javascript = "load('" + jsonString + "');";
                                    Clients.evalJavaScript("reset()");
                                    Clients.evalJavaScript(javascript);
                                    Clients.evalJavaScript("layout_dagre_TB(false)");
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        Button save = (Button) cases_window.getFellow("save");
                        save.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), new EventListener<Event>() {
                            @Override
                            public void onEvent(Event event) throws Exception {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                Writer writer = new BufferedWriter(new OutputStreamWriter(baos));
                                CSVWriter csvWriter = new CSVWriter(writer);
                                Map<String, Integer[]> info = getCases();
                                csvWriter.writeNext(new String[] {"Case ID", "Case Length", "Unique Case ID"});
                                for (String key : info.keySet()) {
                                    csvWriter.writeNext(new String[] {key, info.get(key)[1].toString(), info.get(key)[0].toString()});
                                }
                                csvWriter.flush();
                                csvWriter.close();
                                byte[] buffer = baos.toByteArray();
                                ByteArrayInputStream is = new ByteArrayInputStream(buffer);
                                AMedia amedia = new AMedia("Cases.csv", "csv", "application/file", is);
                                Filedownload.save(amedia);
                            }
                        });
                        cases_window.doOverlapped();
                    }
                });

                this.fitness.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        Window details_window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), new String(Container.var1[19], StandardCharsets.UTF_8), null, null);
                        details_window.setTitle(new String(new byte[]{0x46,0x69,0x74,0x6e,0x65,0x73,0x73,0x20,0x2d,0x20,0x72,0x61,0x66,0x66,0x61,0x65,0x6c,0x65,0x63,0x6f,0x6e,0x66,0x6f,0x72,0x74,0x69,0x2e,0x63,0x6f,0x6d}, StandardCharsets.UTF_8));
                        Listbox listbox = (Listbox) details_window.getFellow(new String(Container.var1[81], StandardCharsets.UTF_8));
                        Listheader detail_frequency = (Listheader) details_window.getFellow(new String(Container.var1[83], StandardCharsets.UTF_8));
                        detail_frequency.setSortAscending(new NumberComparator(true, 1));
                        detail_frequency.setSortDescending(new NumberComparator(false, 1));
                        Listheader detail_ratio = (Listheader) details_window.getFellow(new String(Container.var1[84], StandardCharsets.UTF_8));
                        detail_ratio.setSortAscending(new NumberComparator(true, 1));
                        detail_ratio.setSortDescending(new NumberComparator(false, 1));
                        Listcell listcell1 = new Listcell(new String(Container.var1[88], StandardCharsets.UTF_8));
                        double fitness = measureFitness();
                        Listcell listcell2 = new Listcell(decimalFormat.format(fitness));
                        Listcell listcell3 = new Listcell(decimalFormat.format(fitness * 100) + "%");
                        Listitem listitem = new Listitem();
                        listitem.appendChild(listcell1);
                        listitem.appendChild(listcell2);
                        listitem.appendChild(listcell3);
                        listbox.appendChild(listitem);
                        listbox.setRows(5);
                        details_window.doModal();
                    }
                });

                EventListener<Event> windowListener = new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        int i = activitiesText.getValue();
                        if (i < 0) i = 0;
                        else if (i > 100) i = 100;
                        activitiesText.setValue(i);
                        activities.setCurpos(i);
                        i = arcsText.getValue();
                        if (i < 0) i = 0;
                        else if (i > 100) i = 100;
                        arcsText.setValue(i);
                        arcs.setCurpos(i);
                        i = parallelismText.getValue();
                        if (i < 0) i = 0;
                        else if (i > 100) i = 100;
                        parallelismText.setValue(i);
                        parallelism.setCurpos(i);
                        setArcAndActivityRatios();
                    }
                };

                this.filter.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        new FilterCriterionSelector(getLabel(), ProcessDiscovererController.this, criteria, options_frequency, min, max);
                    }
                });
                this.animate.addEventListener(new String(Container.var1[80], StandardCharsets.UTF_8), new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        String layout = event.getData().toString();
                        if(!gateways.isChecked()) {
                            diagram = processDiscovererService.insertBPMNGateways(diagram);
                        }
                        for(BPMNEdge edge : diagram.getEdges()) {
                            edge.setLabel("");
                        }

                        UIContext context = new UIContext();
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                        UIPluginContext uiPluginContext = context.getMainPluginContext();
                        BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(uiPluginContext, diagram);
                        BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);

                        StringBuilder sb = new StringBuilder();
                        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                                "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                                "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                                "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                                "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                                "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">");

                        sb.append(definitions.exportElements());
                        sb.append("</definitions>");
                        String model = sb.toString();

                        model.replaceAll("\n", "");

                        XLog filtered = processDiscovererService.generateFilteredLog(log, getLabel(), 1 - activities.getCurposInDouble() / 100, inverted_nodes.isChecked(), inverted_arcs.isChecked(), fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
                        logAnimationPlugin.execute(portalContext, model, layout, filtered, gateways.isChecked());
                    }
                });

                this.animate.addEventListener(new String(Container.var1[89], StandardCharsets.UTF_8), new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        activities_value = activities.getCurpos();
                        arcs_value = arcs.getCurpos();
                        parallelism_value = parallelism.getCurpos();

                        Set<String> manually_removed_activities = new HashSet<>();
                        String node = event.getData().toString();
                        for (String name : options_frequency.get(getLabel()).keySet()) {
                            if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
                                manually_removed_activities.add(name);
                                break;
                            }
                        }

                        if (manually_removed_activities.size() > 0) {
                            addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                    REMOVE,
                                    CONTAIN_ANY,
                                    TRACE,
                                    getLabel(),
                                    getLabel(),
                                    manually_removed_activities
                            ));
                        }
                    }
                });

                this.animate.addEventListener(new String(Container.var1[90], StandardCharsets.UTF_8), new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        activities_value = activities.getCurpos();
                        arcs_value = arcs.getCurpos();
                        parallelism_value = parallelism.getCurpos();

                        Set<String> manually_removed_activities = new HashSet<>();
                        String node = event.getData().toString();
                        for (String name : options_frequency.get(getLabel()).keySet()) {
                            if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
                                manually_removed_activities.add(name);
                                break;
                            }
                        }

                        if (manually_removed_activities.size() > 0) {
                            addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                    RETAIN,
                                    CONTAIN_ANY,
                                    TRACE,
                                    getLabel(),
                                    getLabel(),
                                    manually_removed_activities
                            ));
                        }
                    }
                });

                this.animate.addEventListener(new String(Container.var1[91], StandardCharsets.UTF_8), new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        activities_value = activities.getCurpos();
                        arcs_value = arcs.getCurpos();
                        parallelism_value = parallelism.getCurpos();

                        Set<String> manually_removed_activities = new HashSet<>();
                        String node = event.getData().toString();
                        for (String name : options_frequency.get(getLabel()).keySet()) {
                            if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
                                manually_removed_activities.add(name);
                                break;
                            }
                        }

                        if (manually_removed_activities.size() > 0) {
                            addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                    REMOVE,
                                    CONTAIN_ANY,
                                    EVENT,
                                    getLabel(),
                                    getLabel(),
                                    manually_removed_activities
                            ));
                        }
                    }
                });

                this.animate.addEventListener(new String(Container.var1[92], StandardCharsets.UTF_8), new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        activities_value = activities.getCurpos();
                        arcs_value = arcs.getCurpos();
                        parallelism_value = parallelism.getCurpos();

                        Set<String> manually_removed_activities = new HashSet<>();
                        String node = event.getData().toString();
                        for (String name : options_frequency.get(getLabel()).keySet()) {
                            if (name.equals(node) || name.replaceAll("'", "").equals(node)) {
                                manually_removed_activities.add(name);
                                break;
                            }
                        }

                        if (manually_removed_activities.size() > 0) {
                            addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                    RETAIN,
                                    CONTAIN_ANY,
                                    EVENT,
                                    getLabel(),
                                    getLabel(),
                                    manually_removed_activities
                            ));
                        }
                    }
                });

                this.animate.addEventListener(new String(Container.var1[93], StandardCharsets.UTF_8), new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        activities_value = activities.getCurpos();
                        arcs_value = arcs.getCurpos();
                        parallelism_value = parallelism.getCurpos();

                        Set<String> manually_removed_arcs = new HashSet<>();
                        String edge = event.getData().toString();

                        for (String name : options_frequency.get(new String(Container.var1[94], StandardCharsets.UTF_8)).keySet()) {
                            if (name.equals(edge) || name.replaceAll("'", "").equals(edge)) {
                                manually_removed_arcs.add(name);
                                break;
                            }
                        }

                        if (manually_removed_arcs.size() > 0) {
                            addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                    REMOVE,
                                    CONTAIN_ANY,
                                    TRACE,
                                    getLabel(),
                                    new String(Container.var1[94], StandardCharsets.UTF_8),
                                    manually_removed_arcs
                            ));
                        }
                    }
                });

                this.animate.addEventListener(new String(Container.var1[95], StandardCharsets.UTF_8), new EventListener<Event>() {
                    @Override
                    public void onEvent(Event event) throws Exception {
                        activities_value = activities.getCurpos();
                        arcs_value = arcs.getCurpos();
                        parallelism_value = parallelism.getCurpos();

                        Set<String> manually_removed_arcs = new HashSet<>();
                        String edge = event.getData().toString();

                        for (String name : options_frequency.get(new String(Container.var1[94], StandardCharsets.UTF_8)).keySet()) {
                            if (name.equals(edge) || name.replaceAll("'", "").equals(edge)) {
                                manually_removed_arcs.add(name);
                                break;
                            }
                        }

                        if (manually_removed_arcs.size() > 0) {
                            addCriterion(LogFilterCriterionFactory.getLogFilterCriterion(
                                    RETAIN,
                                    CONTAIN_ANY,
                                    TRACE,
                                    getLabel(),
                                    new String(Container.var1[94], StandardCharsets.UTF_8),
                                    manually_removed_arcs
                            ));
                        }
                    }
                });

                slidersWindow.addEventListener(new String(Container.var1[96], StandardCharsets.UTF_8), windowListener);
                slidersWindow.addEventListener(new String(Container.var1[97], StandardCharsets.UTF_8), windowListener);
            }

            exportBPMN.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    activities_value = activities.getCurpos();
                    arcs_value = arcs.getCurpos();
                    parallelism_value = parallelism.getCurpos();

                    if(!gateways.isChecked()) {
                        diagram = processDiscovererService.insertBPMNGateways(diagram);
                    }
                    for(BPMNEdge edge : diagram.getEdges()) {
                        edge.setLabel("");
                    }

                    for (Flow flow : diagram.getFlows()) {
                        flow.setLabel("");
                    }
                    for (org.processmining.models.graphbased.directed.bpmn.elements.Event event1 : diagram.getEvents()) {
                        event1.getAttributeMap().put("ProM_Vis_attr_label", "");
                    }

                    UIContext context = new UIContext();
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    UIPluginContext uiPluginContext = context.getMainPluginContext();
                    BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(uiPluginContext, diagram);
                    BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);

                    String model = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                            "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                            "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                            "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                            "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                            "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">") +
                            definitions.exportElements() +
                            "</definitions>";

                    String defaultProcessName = null;
                    if (log_name != null) {
                        defaultProcessName = log_name.split("\\.")[0];
                    }

                    String user = portalContext.getCurrentUser().getUsername();
                    Version version = new Version(1, 0);
                    Set<RequestParameterType<?>> canoniserProperties = new HashSet<>();
                    String now = DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString();
                    boolean publicModel = true;

                    List<String> domains = domainService.findAllDomains();
                    SelectDynamicListController domainCB = new SelectDynamicListController(domains);
                    domainCB.setReference(domains);
                    domainCB.setAutodrop(true);
                    domainCB.setWidth("85%");
                    domainCB.setHeight("100%");
                    domainCB.setAttribute("hflex", "1");

                    ProcessModelVersion pmv = processService.importProcess(user,
                            portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId(),
                            defaultProcessName,
                            version,
                            nativeType,
                            canoniserService.canonise(nativeType, new ByteArrayInputStream(model.getBytes()), canoniserProperties),
                            domainCB.getValue(),
                            "Model generated by the Apromore BPMN process mining service.",
                            now,  // creation timestamp
                            now,  // last update timestamp
                            publicModel);

                    portalContext.displayNewProcess(userInterfaceHelper.createProcessSummary(pmv.getProcessBranch().getProcess(),
                            pmv.getProcessBranch(),
                            pmv,
                            nativeType,
                            domainCB.getValue(),
                            now,  // creation timestamp
                            now,  // last update timestamp
                            user,
                            publicModel));

                    portalContext.refreshContent();
                }
            });

            EventListener<Event> exportPDF = new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    String name = log_name;
                    int pos = 97;
                    if (primaryType == FREQUENCY) {
                        if (primaryAggregation == TOTAL) {
                            pos += 1;
                        } else if (primaryAggregation == CASES) {
                            pos += 2;
                        } else if (primaryAggregation == MEAN) {
                            pos += 3;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 4;
                        } else if (primaryAggregation == MODE) {
                            pos += 5;
                        } else if (primaryAggregation == MAX) {
                            pos += 6;
                        } else if (primaryAggregation == MIN) {
                            pos += 7;
                        }
                    } else if (primaryType == DURATION) {
                        if (primaryAggregation == TOTAL) {
                            pos += 8;
                        } else if (primaryAggregation == MEAN) {
                            pos += 9;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 10;
                        } else if (primaryAggregation == MAX) {
                            pos += 11;
                        } else if (primaryAggregation == MIN) {
                            pos += 12;
                        }
                    } else if (primaryType == DURATION) {
                        if (primaryAggregation == TOTAL) {
                            pos += 13;
                        } else if (primaryAggregation == MEAN) {
                            pos += 14;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 15;
                        } else if (primaryAggregation == MAX) {
                            pos += 16;
                        } else if (primaryAggregation == MIN) {
                            pos += 17;
                        }
                    }
                    name += new String(Container.var1[pos], StandardCharsets.UTF_8);
                    String command = String.format(new String(Container.var1[115], StandardCharsets.UTF_8), name);
                    Clients.evalJavaScript(command);
                }
            };

            export.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), exportPDF);
            downloadPDF.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), exportPDF);
            downloadPNG.addEventListener(new String(Container.var1[74], StandardCharsets.UTF_8), new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    String name = log_name;
                    int pos = 97;
                    if (primaryType == FREQUENCY) {
                        if (primaryAggregation == TOTAL) {
                            pos += 1;
                        } else if (primaryAggregation == CASES) {
                            pos += 2;
                        } else if (primaryAggregation == MEAN) {
                            pos += 3;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 4;
                        } else if (primaryAggregation == MODE) {
                            pos += 5;
                        } else if (primaryAggregation == MAX) {
                            pos += 6;
                        } else if (primaryAggregation == MIN) {
                            pos += 7;
                        }
                    } else if (primaryType == DURATION) {
                        if (primaryAggregation == TOTAL) {
                            pos += 8;
                        } else if (primaryAggregation == MEAN) {
                            pos += 9;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 10;
                        } else if (primaryAggregation == MAX) {
                            pos += 11;
                        } else if (primaryAggregation == MIN) {
                            pos += 12;
                        }
                    } else if (primaryType == DURATION) {
                        if (primaryAggregation == TOTAL) {
                            pos += 13;
                        } else if (primaryAggregation == MEAN) {
                            pos += 14;
                        } else if (primaryAggregation == MEDIAN) {
                            pos += 15;
                        } else if (primaryAggregation == MAX) {
                            pos += 16;
                        } else if (primaryAggregation == MIN) {
                            pos += 17;
                        }
                    }
                    name += new String(Container.var1[pos], StandardCharsets.UTF_8);
                    String command = String.format(new String(Container.var1[116], StandardCharsets.UTF_8), name);
                    Clients.evalJavaScript(command);
                }
            });

            slidersWindow.doOverlapped();

            if(log == null) {
                try {
                    Object[] o = processDiscovererService.generateJSONFromBPMNDiagram(diagram);
                    JSONArray array = (JSONArray) o[0];
                    diagram = (BPMNDiagram) o[1];

                    String jsonString = array.toString();
                    String javascript = "load('" + jsonString + "');";
                    Clients.evalJavaScript("reset()");
                    Clients.evalJavaScript(javascript);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            portalContext.getMessageHandler().displayError("Could not load component ", e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Integer[]> getCases() {
        XLog filtered_log = processDiscovererService.generateFilteredLog(log, getLabel(), 1 - activities.getCurposInDouble() / 100, inverted_nodes.isChecked(), inverted_arcs.isChecked(), fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
        Map<String, Integer[]> cases = new HashMap(filtered_log.size());
        ObjectIntHashMap<String> variant = new ObjectIntHashMap<>();
        for(XTrace trace : filtered_log) {
            int length = 0;
            StringBuilder traceBuilder = new StringBuilder();
            for (XEvent event : trace) {
                String label = event.getAttributes().get(getLabel()).toString();
                if(event.getAttributes().get(new String(Container.var1[117], StandardCharsets.UTF_8)).toString().endsWith("complete")) length++;
                traceBuilder.append(label + ",");
            }
            String s = traceBuilder.toString();
            Integer i;
            if(variant.containsKey(s)) i = variant.get(s);
            else {
                i = variant.size() + 1;
                variant.put(s, i);
            }
            cases.put(XConceptExtension.instance().extractName(trace), new Integer[] {i, length});
        }
        return cases;
    }

    private double measureFitness() {
        BPMNDiagram bpmnDiagram = diagram;
        if(!gateways.isChecked()) bpmnDiagram = processDiscovererService.insertBPMNGateways(diagram);
        for (BPMNNode node : bpmnDiagram.getNodes()) {
            if(node instanceof org.processmining.models.graphbased.directed.bpmn.elements.Event) {
                org.processmining.models.graphbased.directed.bpmn.elements.Event event1 = (org.processmining.models.graphbased.directed.bpmn.elements.Event) node;
                if (event1.getEventType() == org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.START) {
                    event1.getAttributeMap().put("ProM_Vis_attr_label", "START");
                } else if (event1.getEventType() == org.processmining.models.graphbased.directed.bpmn.elements.Event.EventType.END) {
                    event1.getAttributeMap().put("ProM_Vis_attr_label", "END");
                } else {
                    event1.getAttributeMap().put("ProM_Vis_attr_label", "");
                }
            }
        }

        Object[] petrinet = BPMNToPetriNetConverter.convert(bpmnDiagram);

        PNRepResult result = computeAlignment(
                new FakePluginContext(),
                new XEventNameClassifier(),
                (Petrinet) petrinet[0],
                (Marking) petrinet[1],
                (Marking) petrinet[2],
                log);
        return getAlignmentValue(result);
    }

    private void addCriterion(LogFilterCriterion logFilterCriterion) throws InterruptedException {
        if (!criteria.contains(logFilterCriterion)) {
            criteria.add(logFilterCriterion);
            refreshCriteria();
        }
    }

    public void refreshCriteria() throws InterruptedException {
        XLog reduced_log = processDiscovererService.generateFilteredLog(log, getLabel(), 1 - activities.getCurposInDouble() / 100, inverted_nodes.isChecked(), inverted_arcs.isChecked(), fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
        populateMetrics(reduced_log);
        generateOptions(reduced_log);
        visualized = false;
        setArcAndActivityRatios();
    }

    private void populateMetrics(XLog log) {
        Set<String> uniqueTraces = new HashSet<>();
        Map<String, Integer> labels = new HashMap<>();
        Set<String> resources = new HashSet<>();
        List<Long> durations = new ArrayList<>(log.size());

        int events = 0;

        Date start = null;
        Date end = null;

        XTimeExtension xte = XTimeExtension.instance();
        XOrganizationalExtension xor = XOrganizationalExtension.instance();

        for (XTrace trace : log) {
            StringBuilder traceBuilder = new StringBuilder();
            durations.add(xte.extractTimestamp(trace.get(trace.size() - 1)).getTime() - xte.extractTimestamp(trace.get(0)).getTime());
            for (XEvent event : trace) {
                String label = event.getAttributes().get(getLabel()).toString();
                if (!labels.containsKey(label)) labels.put(label, labels.size());
                traceBuilder.append(labels.get(label) + ",");

                resources.add(xor.extractResource(event));
                Date d = xte.extractTimestamp(event);
                if (start == null || d.before(start)) start = d;
                if (end == null || d.after(end)) end = d;
                events++;
            }
            uniqueTraces.add(traceBuilder.toString());
        }

        Long[] dur = durations.toArray(new Long[durations.size()]);
        Arrays.sort(dur);

        double shortest = Double.MAX_VALUE;
        double longhest = 0;
        double median = dur[dur.length / 2];
        double mean = 0;
        for (Long l : dur) {
            mean += l;
            if (shortest > l) shortest = l;
            if (longhest < l) longhest = l;
        }
        mean = mean / dur.length;

        caseNumber.setValue(Integer.toString(log.size()));
        uniquecaseNumber.setValue(Integer.toString(uniqueTraces.size()));
        activityNumber.setValue(Integer.toString(labels.size()));
        eventNumber.setValue(Integer.toString(events));
        meanDuration.setValue(Convertor.convertFrom(Double.toString(mean)));
        medianDuration.setValue(Convertor.convertFrom(Double.toString(median)));
        maxDuration.setValue(Convertor.convertFrom(Double.toString(longhest)));
        minDuration.setValue(Convertor.convertFrom(Double.toString(shortest)));
    }

    private void generateOptions(XLog log) {
        boolean firstTime = (options_frequency.keySet().size() == 0);
        Multimap<String, String> tmp_options = HashMultimap.create();
        Map<String, Map<String, Integer>> tmp_options_frequency = new HashMap<>();

        for (XTrace trace : log) {
            if (firstTime) {
                for (XEvent event : trace) {
                    for (XAttribute attribute : event.getAttributes().values()) {
                        String key = attribute.getKey();
                        if (!(key.equals("lifecycle:model") || key.equals("time:timestamp"))) {
                            tmp_options.put(key, attribute.toString());
                            if(tmp_options_frequency.get(key) == null) tmp_options_frequency.put(key, new HashMap<>());

                            Integer i = tmp_options_frequency.get(key).get(attribute.toString());
                            if (i == null) tmp_options_frequency.get(key).put(attribute.toString(), 1);
                            else tmp_options_frequency.get(key).put(attribute.toString(), i + 1);
                        }
                        if (key.equals("time:timestamp")) {
                            min = Math.min(min, ((XAttributeTimestamp) attribute).getValueMillis());
                            max = Math.max(max, ((XAttributeTimestamp) attribute).getValueMillis());
                        }
                    }
                }
            }

            for (int i = -1; i < trace.size(); i++) {
                String event1;
                if (i == -1) event1 = "|>";
                else event1 = trace.get(i).getAttributes().get(getLabel()).toString();

                for (int j = i + 1; j < trace.size() + 1; j++) {
                    String event2;
                    if (j == trace.size()) event2 = "[]";
                    else {
                        XAttribute attribute = trace.get(j).getAttributes().get(getLabel());
                        if (attribute != null) event2 = attribute.toString();
                        else event2 = "";
                    }

                    if(j == i + 1) {
                        String df = (event1 + " => " + event2);
                        tmp_options.put("direct:follow", df);
                        if (tmp_options_frequency.get("direct:follow") == null)
                            tmp_options_frequency.put("direct:follow", new HashMap<>());
                        Integer k = tmp_options_frequency.get("direct:follow").get(df);
                        if (k == null) tmp_options_frequency.get("direct:follow").put(df, 1);
                        else tmp_options_frequency.get("direct:follow").put(df, k + 1);
                    }
                    if(i != -1 && j != trace.size()) {
                        String ef = (event1 + " => " + event2);
                        tmp_options.put("eventually:follow", ef);
                        if (tmp_options_frequency.get("eventually:follow") == null)
                            tmp_options_frequency.put("eventually:follow", new HashMap<>());
                        Integer k = tmp_options_frequency.get("eventually:follow").get(ef);
                        if (k == null) tmp_options_frequency.get("eventually:follow").put(ef, 1);
                        else tmp_options_frequency.get("eventually:follow").put(ef, k + 1);
                    }
                }
            }
        }

        options_frequency.putAll(tmp_options_frequency);

        options_frequency.put("time:timestamp", new HashMap<>());
        options_frequency.put("time:duration", new HashMap<>());
    }

    private void visualizeFrequency() throws InterruptedException {
        primaryType = FREQUENCY;
        if (case_frequency.isChecked()) {
            primaryAggregation = CASES;
            case_frequency.setChecked(true);
        } else if (mean_frequency.isChecked()) {
            primaryAggregation = MEAN;
            mean_frequency.setChecked(true);
        } else if (median_frequency.isChecked()) {
            primaryAggregation = MEDIAN;
            median_frequency.setChecked(true);
        } else if (mode_frequency.isChecked()) {
            primaryAggregation = MODE;
            mode_frequency.setChecked(true);
        } else if (max_frequency.isChecked()) {
            primaryAggregation = MAX;
            max_frequency.setChecked(true);
        } else if (min_frequency.isChecked()) {
            primaryAggregation = MIN;
            min_frequency.setChecked(true);
        } else {
            primaryAggregation = TOTAL;
            absolute_frequency.setChecked(true);
        }

        secondaryType = DURATION;
        if (total_duration.isChecked()) {
            secondaryAggregation = TOTAL;
        } else if (mean_duration.isChecked()) {
            secondaryAggregation = MEAN;
        } else if (max_duration.isChecked()) {
            secondaryAggregation = MAX;
        } else if (min_duration.isChecked()) {
            secondaryAggregation = MIN;
        } else {
            secondaryAggregation = MEDIAN;
        }

        visualized = false;
        setArcAndActivityRatios();
    }


    private void visualizeDuration() throws InterruptedException {
        primaryType = DURATION;
        if (total_duration.isChecked()) {
            primaryAggregation = TOTAL;
            total_duration.setChecked(true);
        } else if (mean_duration.isChecked()) {
            primaryAggregation = MEAN;
            mean_duration.setChecked(true);
        } else if (max_duration.isChecked()) {
            primaryAggregation = MAX;
            max_duration.setChecked(true);
        } else if (min_duration.isChecked()) {
            primaryAggregation = MIN;
            min_duration.setChecked(true);
        } else {
            primaryAggregation = MEDIAN;
            median_duration.setChecked(true);
        }

        secondaryType = FREQUENCY;
        if (case_frequency.isChecked()) {
            secondaryAggregation = CASES;
        } else if (mean_frequency.isChecked()) {
            secondaryAggregation = MEAN;
        } else if (median_frequency.isChecked()) {
            secondaryAggregation = MEDIAN;
        } else if (mode_frequency.isChecked()) {
            secondaryAggregation = MODE;
        } else if (max_frequency.isChecked()) {
            secondaryAggregation = MAX;
        } else if (min_frequency.isChecked()) {
            secondaryAggregation = MIN;;
        } else {
            secondaryAggregation = TOTAL;
        }
        visualized = false;
        setArcAndActivityRatios();
    }

    public void setArcAndActivityRatios() {
        if(activities_value != activities.getCurpos() ||
                arcs_value != arcs.getCurpos() ||
                parallelism_value != parallelism.getCurpos() ||
                !visualized
        ) {
            if (use_dynamic.isSelected()) {
                fixedType = primaryType;
                fixedAggregation = primaryAggregation;
            }

            boolean test = false;
            while (!test) {
                activities_value = activities.getCurpos();
                arcs_value = arcs.getCurpos();
                parallelism_value = parallelism.getCurpos();

                BPMNDiagram diagram = processDiscovererService.generateDFGFromLog(log, getLabel(), 1 - activities.getCurposInDouble() / 100, 1 - arcs.getCurposInDouble() / 100, true, inverted_nodes.isChecked(), inverted_arcs.isChecked(), fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
                if (diagram.getNodes().isEmpty()) {
                    arcs.setCurpos(arcs_value + 1);
                    arcsText.setValue(arcs_value + 1);
                } else {
                    test = true;
                }
            }

            try {
                JSONArray array;
                if(gateways.isChecked()) {
                    Object[] o = processDiscovererService.generateJSONWithGatewaysFromLog(log, getLabel(), 1 - activities.getCurposInDouble() / 100, 1 - arcs.getCurposInDouble() / 100, parallelism.getCurposInDouble() / 100, true, true, inverted_nodes.isChecked(), inverted_arcs.isChecked(), secondary.isChecked(), fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
                    array = (JSONArray) o[0];
                    diagram = (BPMNDiagram) o[1];
                }else {
                    Object[] o = processDiscovererService.generateJSONFromLog(log, getLabel(), 1 - activities.getCurposInDouble() / 100, 1 - arcs.getCurposInDouble() / 100, true, inverted_nodes.isChecked(), inverted_arcs.isChecked(), secondary.isChecked(), fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
                    array = (JSONArray) o[0];
                    diagram = (BPMNDiagram) o[1];
                }

                String jsonString = array.toString();
                String javascript = "load('" + jsonString + "');";
                Clients.evalJavaScript("reset()");
                Clients.evalJavaScript(javascript);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveLog(XLog filtered_log) {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            eventLogService.exportToStream(outputStream, filtered_log);

            int folderId = portalContext.getCurrentFolder() == null ? 0 : portalContext.getCurrentFolder().getId();

            eventLogService.importLog(portalContext.getCurrentUser().getUsername(), folderId,
                    logSummary.getName() + "_filtered", new ByteArrayInputStream(outputStream.toByteArray()), "xes.gz",
                    logSummary.getDomain(), DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()).toString(),
                    logSummary.isMakePublic());

            portalContext.refreshContent();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> generateLabels(XLog log) {
        Set<String> set = new HashSet<>();
        for(XTrace trace : log) {
            for(XEvent event : trace) {
                for (XAttribute attribute : event.getAttributes().values()) {
                    String key = attribute.getKey();
                    if(key.equals("time:timestamp")) continue;
                    set.add(key);
                }
            }
        }
        for(XTrace trace : log) {
            for(XEvent event : trace) {
                Set<String> set1 = new HashSet<>();
                for (XAttribute attribute : event.getAttributes().values()) {
                    String key = attribute.getKey();
                    if(key.equals("time:timestamp")) continue;
                    set1.add(key);
                }
                for(String s : (new ArrayList<>(set))) {
                    if(!set1.contains(s)) {
                        set.remove(s);
                    }
                }
            }
        }
        List<String> list = new ArrayList<>(set);
        Collections.sort(list);
        return list;
    }

    private void setLabel(String label) {
        this.label = label;
    }

    private String getLabel() {
        return label;
    }

    private PNRepResult computeAlignment(PluginContext pluginContext, XEventClassifier xEventClassifier, Petrinet petrinet, Marking initialMarking, Marking finalMarking, XLog log) {
        pluginContext.addConnection(new InitialMarkingConnection(petrinet, initialMarking));
        pluginContext.addConnection(new FinalMarkingConnection(petrinet, finalMarking));

        PetrinetReplayerWithILP replayer = new PetrinetReplayerWithILP();

        XEventClass dummyEvClass = new XEventClass("DUMMY",99999);

        Map<Transition, Integer> transitions2costs = constructTTCMap(petrinet);
        Map<XEventClass, Integer> events2costs = constructETCMap(petrinet, xEventClassifier, log, dummyEvClass);

        CostBasedCompleteParam parameters = constructParameters(transitions2costs, events2costs, petrinet, initialMarking, finalMarking);
        TransEvClassMapping mapping = constructMapping(petrinet, xEventClassifier, log, dummyEvClass);

        try {
            return replayer.replayLog(pluginContext, petrinet, log, mapping, parameters);
        } catch (AStarException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return null;
    }


    private Map<Transition, Integer> constructTTCMap(Petrinet petrinet) {
        Map<Transition, Integer> transitions2costs = new UnifiedMap<>();

        for(Transition t : petrinet.getTransitions()) {
            if(t.isInvisible()) {
                transitions2costs.put(t, 0);
            }else {
                transitions2costs.put(t, 1);
            }
        }
        return transitions2costs;
    }

    private static Map<XEventClass, Integer> constructETCMap(Petrinet petrinet, XEventClassifier xEventClassifier, XLog log, XEventClass dummyEvClass) {
        Map<XEventClass,Integer> costMOT = new UnifiedMap<>();
        XLogInfo summary = XLogInfoFactory.createLogInfo(log, xEventClassifier);

        for (XEventClass evClass : summary.getEventClasses().getClasses()) {
            int value = 1;
            for(Transition t : petrinet.getTransitions()) {
                if(t.getLabel().equals(evClass.getId())) {
                    value = 1;
                    break;
                }
            }
            costMOT.put(evClass, value);
        }

        costMOT.put(dummyEvClass, 1);

        return costMOT;
    }

    private CostBasedCompleteParam constructParameters(Map<Transition, Integer> transitions2costs, Map<XEventClass, Integer> events2costs, Petrinet petrinet, Marking initialMarking, Marking finalMarking) {
        CostBasedCompleteParam parameters = new CostBasedCompleteParam(events2costs, transitions2costs);

        parameters.setInitialMarking(initialMarking);
        parameters.setFinalMarkings(finalMarking);
        parameters.setGUIMode(false);
        parameters.setCreateConn(false);
        ((CostBasedCompleteParam) parameters).setMaxNumOfStates(Integer.MAX_VALUE);

        return  parameters;
    }

    private static TransEvClassMapping constructMapping(Petrinet net, XEventClassifier xEventClassifier, XLog log, XEventClass dummyEvClass) {
        TransEvClassMapping mapping = new TransEvClassMapping(xEventClassifier, dummyEvClass);

        XLogInfo summary = XLogInfoFactory.createLogInfo(log, xEventClassifier);

        for (Transition t : net.getTransitions()) {
            boolean mapped = false;

            for (XEventClass evClass : summary.getEventClasses().getClasses()) {
                String id = evClass.getId();

                if (t.getLabel().equals(id)) {
                    mapping.put(t, evClass);
                    mapped = true;
                    break;
                }
            }

            if (!mapped) {
                mapping.put(t, dummyEvClass);
            }

        }

        return mapping;
    }

    private double getAlignmentValue(PNRepResult pnRepResult) {
        int unreliable = 0;
        if(pnRepResult == null) {
//            System.out.println("UNRELIABLE");
            return Double.NaN;
        }
        for(SyncReplayResult srp : pnRepResult) {
            if(!srp.isReliable()) {
                unreliable += srp.getTraceIndex().size();
            }
        }
        if(unreliable > pnRepResult.size() / 2) {
//            System.out.println("UNRELIABLE");
            return Double.NaN;
        }else {
            return (Double) pnRepResult.getInfo().get(PNRepResult.TRACEFITNESS);
        }
    }
}
