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

import com.raffaeleconforti.processdiscoverer.impl.filter.LogFilterTypeSelector;
import com.raffaeleconforti.processdiscoverer.impl.util.Container;
import org.apromore.plugin.portal.PortalContext;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
class FilterCriterionSelector {

    private final DecimalFormat decimalFormat = new DecimalFormat(new String(Container.var1[123], StandardCharsets.UTF_8));

    PortalContext portalContext;
    private ProcessDiscovererController processDiscovererController;
    private List<LogFilterCriterion> criteria;

    private Window filterSelectorW;
    private Listbox criteriaList;

    public FilterCriterionSelector(String label, ProcessDiscovererController processDiscovererController, List<LogFilterCriterion> criteria, Map<String, Map<String, Integer>> options_frequency, long min, long max) throws IOException {
        this.processDiscovererController = processDiscovererController;
        this.criteria = criteria;

        portalContext = processDiscovererController.portalContext;

        filterSelectorW = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/filterCriteria.zul", null, null);
        filterSelectorW.setTitle(new String(new byte[]{0x46,0x69,0x6c,0x74,0x65,0x72,0x20,0x43,0x72,0x69,0x74,0x65,0x72,0x69,0x61,0x20,0x2d,0x20,0x72,0x61,0x66,0x66,0x61,0x65,0x6c,0x65,0x63,0x6f,0x6e,0x66,0x6f,0x72,0x74,0x69,0x2e,0x63,0x6f,0x6d}, StandardCharsets.UTF_8));

        criteriaList = (Listbox) filterSelectorW.getFellow("criteria");
        updateList();

        Button okButton = (Button) filterSelectorW.getFellow("filterOkButton");
        Button createButton = (Button) filterSelectorW.getFellow("filterCreateButton");
        Button editButton = (Button) filterSelectorW.getFellow("filterEditButton");
        Button moveUpButton = (Button) filterSelectorW.getFellow("filterMoveUpButton");
        Button moveDownButton = (Button) filterSelectorW.getFellow("filterMoveDownButton");
        Button removeButton = (Button) filterSelectorW.getFellow("filterRemoveButton");
        Button removeAllButton = (Button) filterSelectorW.getFellow("filterRemoveAllButton");

        okButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws InterruptedException {
                save();
            }
        });
        createButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                new CreateFilterCriterion(label, FilterCriterionSelector.this, criteria, options_frequency, min, max);
            }
        });
        editButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                if (criteriaList.getSelectedIndex() > -1) {
                    new CreateFilterCriterion(label, FilterCriterionSelector.this, criteria, options_frequency, min, max, criteriaList.getSelectedIndex());
                }
            }
        });
        moveUpButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                if(criteriaList.getSelectedIndex() > 0) {
                    int pos = criteriaList.getSelectedIndex();
                    LogFilterCriterion criterion = criteria.get(pos);
                    criteria.set(pos, criteria.get(pos - 1));
                    criteria.set(pos - 1, criterion);
                    updateList();
                }
            }
        });
        moveDownButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                if(criteriaList.getSelectedIndex() > -1 && criteriaList.getSelectedIndex() < criteria.size() - 1) {
                    int pos = criteriaList.getSelectedIndex();
                    LogFilterCriterion criterion = criteria.get(pos);
                    criteria.set(pos, criteria.get(pos + 1));
                    criteria.set(pos + 1, criterion);
                    updateList();
                }
            }
        });
        removeButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) {
                int selected = criteriaList.getSelectedIndex();
                if(selected > -1) {
                    criteria.remove(selected);
                    criteriaList.removeItemAt(selected);
                }
            }
        });
        removeAllButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) {
                criteria.clear();
                updateList();
            }
        });
        filterSelectorW.doModal();
    }

    private void save() throws InterruptedException {
        filterSelectorW.detach();
        processDiscovererController.refreshCriteria();
    }

    public void updateList() {
        ListModelList<String> model = new ListModelList<>();
        for(LogFilterCriterion criterion : criteria) {
            String label = criterion.toString();
            for(int i = 0; i < LogFilterTypeSelector.type.length; i++) {
                label = label.replaceAll(LogFilterTypeSelector.type[i], LogFilterTypeSelector.getMatch(LogFilterTypeSelector.type[i]));
            }

            if(label.contains("Time-frame")) {
                String tmp_label = label.substring(0, label.indexOf("is equal"));
                String e = label.substring(label.indexOf("<") + 1, label.indexOf(" OR >"));
                String s = label.substring(label.indexOf(" OR >") + 5, label.indexOf("]"));
                label = tmp_label
                        + " is between "
                        + (new Date(Long.parseLong(s))).toString()
                        + " and "
                        + (new Date(Long.parseLong(e))).toString();
            }
            if(label.contains("Duration")) {
                String d = label.substring(label.indexOf(">") + 1, label.indexOf("]"));
                if(label.contains("Remove")) {
                    label = "Remove all traces with a Duration greater than " + convertMilliseconds(d);
                }else {
                    label = "Retain all traces with a Duration greater than " + convertMilliseconds(d);
                }
            }
            if(label.contains("Direct Follow Relation")) {
                String d = label.substring(label.indexOf("["));
                if(label.contains("Remove")) {
                    label = "Remove all traces containing the Direct Follow Relation " + d;
                }else {
                    label = "Retain all traces containing the Direct Follow Relation " + d;
                }
            }
            model.add(label);
        }
        criteriaList.setModel(model);
    }

    private String convertMilliseconds(String number) {
        double milliseconds = Double.parseDouble(number);
        double seconds = milliseconds / 1000.0;
        double minutes = seconds / 60.0;
        double hours = minutes / 60.0;
        double days = hours / 24.0;
        double weeks = days / 7.0;
        double months = days / 30.0;
        double years = days / 365.0;

        if(years > 1) return decimalFormat.format(years) + " Years";
        else if(months > 1) return decimalFormat.format(months) + " Months";
        else if(weeks > 1) return decimalFormat.format(weeks) + " Weeks";
        else if(days > 1) return decimalFormat.format(days) + " Days";
        else if(hours > 1) return decimalFormat.format(hours) + " Hours";
        else if(minutes > 1) return decimalFormat.format(minutes) + " Minutes";
        else return decimalFormat.format(seconds) + " Seconds";
    }
}
