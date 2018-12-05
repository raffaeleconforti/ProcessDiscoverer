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

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class LogFilterTypeSelector {

    public static String[] type = new String[] {
            new String(new byte[]{0x63,0x6f,0x6e,0x63,0x65,0x70,0x74,0x3a,0x6e,0x61,0x6d,0x65}, StandardCharsets.UTF_8),
            new String(new byte[]{0x64,0x69,0x72,0x65,0x63,0x74,0x3a,0x66,0x6f,0x6c,0x6c,0x6f,0x77}, StandardCharsets.UTF_8),
            new String(new byte[]{0x65,0x76,0x65,0x6e,0x74,0x75,0x61,0x6c,0x6c,0x79,0x3a,0x66,0x6f,0x6c,0x6c,0x6f,0x77}, StandardCharsets.UTF_8),
            new String(new byte[]{0x6c,0x69,0x66,0x65,0x63,0x79,0x63,0x6c,0x65,0x3a,0x74,0x72,0x61,0x6e,0x73,0x69,0x74,0x69,0x6f,0x6e}, StandardCharsets.UTF_8),
            new String(new byte[]{0x6f,0x72,0x67,0x3a,0x67,0x72,0x6f,0x75,0x70}, StandardCharsets.UTF_8),
            new String(new byte[]{0x6f,0x72,0x67,0x3a,0x72,0x65,0x73,0x6f,0x75,0x72,0x63,0x65}, StandardCharsets.UTF_8),
            new String(new byte[]{0x6f,0x72,0x67,0x3a,0x72,0x6f,0x6c,0x65}, StandardCharsets.UTF_8),
            new String(new byte[]{0x74,0x69,0x6d,0x65,0x3a,0x64,0x75,0x72,0x61,0x74,0x69,0x6f,0x6e}, StandardCharsets.UTF_8),
            new String(new byte[]{0x74,0x69,0x6d,0x65,0x3a,0x74,0x69,0x6d,0x65,0x73,0x74,0x61,0x6d,0x70}, StandardCharsets.UTF_8)
    };

    private static String[] name = new String[] {
            new String(new byte[]{0x41,0x63,0x74,0x69,0x76,0x69,0x74,0x79}, StandardCharsets.UTF_8),
            new String(new byte[]{0x44,0x69,0x72,0x65,0x63,0x74,0x20,0x46,0x6f,0x6c,0x6c,0x6f,0x77,0x20,0x52,0x65,0x6c,0x61,0x74,0x69,0x6f,0x6e}, StandardCharsets.UTF_8),
            new String(new byte[]{0x44,0x75,0x72,0x61,0x74,0x69,0x6f,0x6e}, StandardCharsets.UTF_8),
            new String(new byte[]{0x45,0x76,0x65,0x6e,0x74,0x75,0x61,0x6c,0x6c,0x79,0x20,0x46,0x6f,0x6c,0x6c,0x6f,0x77,0x20,0x52,0x65,0x6c,0x61,0x74,0x69,0x6f,0x6e}, StandardCharsets.UTF_8),
            new String(new byte[]{0x4c,0x69,0x66,0x65,0x63,0x79,0x63,0x6c,0x65}, StandardCharsets.UTF_8),
            new String(new byte[]{0x47,0x72,0x6f,0x75,0x70}, StandardCharsets.UTF_8),
            new String(new byte[]{0x52,0x65,0x73,0x6f,0x75,0x72,0x63,0x65}, StandardCharsets.UTF_8),
            new String(new byte[]{0x52,0x6f,0x6c,0x65}, StandardCharsets.UTF_8),
            new String(new byte[]{0x54,0x69,0x6d,0x65,0x2d,0x66,0x72,0x61,0x6d,0x65}, StandardCharsets.UTF_8),
    };

    public static int getType(String attribute) {
        int t = Arrays.binarySearch(type, attribute);
        if(t < 0) return -1;
        return t;
    }

    public static int getName(String attribute) {
        int t = Arrays.binarySearch(name, attribute);
        if(t < 0) return -1;
        return t;
    }

    public static String getMatch(String attribute) {
        return search1(attribute, type, name);
    }

    public static String getReverseMatch(String attribute) {
        return search2(attribute, name, type);
    }

    private static String search1(String attribute, String[] origin, String[] translation) {
        int t = Arrays.binarySearch(origin, attribute);
        switch (t) {
            case 0 : return translation[0];
            case 1 : return translation[1];
            case 2 : return translation[3];
            case 3 : return translation[4];
            case 4 : return translation[5];
            case 5 : return translation[6];
            case 6 : return translation[7];
            case 7 : return translation[2];
            case 8 : return translation[8];
            default : return null;
        }
    }

    private static String search2(String attribute, String[] origin, String[] translation) {
        int t = Arrays.binarySearch(origin, attribute);
        switch (t) {
            case 0 : return translation[0];
            case 1 : return translation[1];
            case 3 : return translation[2];
            case 4 : return translation[3];
            case 5 : return translation[4];
            case 6 : return translation[5];
            case 7 : return translation[6];
            case 2 : return translation[7];
            case 8 : return translation[8];
            default : return null;
        }
    }

}
