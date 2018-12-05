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

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class Convertor {

    private static final DecimalFormat var1 = new DecimalFormat(new String(Container.var1[123], StandardCharsets.UTF_8));

    public static String convertFrom(String var2) {
        double var3 = Double.parseDouble(var2);
        double var4 = var3 / 1000.0;
        double var5 = var4 / 60.0;
        double var6 = var5 / 60.0;
        double var7 = var6 / 24.0;
        double var8 = var7 / 7.0;
        double var9 = var7 / 30.0;
        double var10 = var7 / 365.0;

        if(var10 > 1) {
            return var1.format(var10) + new String(new byte[]{0x20,0x79,0x72,0x73}, StandardCharsets.UTF_8);
        }else if(var9 > 1) {
            return var1.format(var9) + new String(new byte[]{0x20,0x6d,0x74,0x68,0x73}, StandardCharsets.UTF_8);
        }else if(var8 > 1) {
            return var1.format(var8) + new String(new byte[]{0x20,0x77,0x6b,0x73}, StandardCharsets.UTF_8);
        }else if(var7 > 1) {
            return var1.format(var7) + new String(new byte[]{0x20,0x64}, StandardCharsets.UTF_8);
        }else if(var6 > 1) {
            return var1.format(var6) + new String(new byte[]{0x20,0x68,0x72,0x73}, StandardCharsets.UTF_8);
        }else if(var5 > 1) {
            return var1.format(var5) + new String(new byte[]{0x20,0x6d,0x69,0x6e,0x73}, StandardCharsets.UTF_8);
        }else if(var4 > 1) {
            return var1.format(var4) + new String(new byte[]{0x20,0x73,0x65,0x63,0x73}, StandardCharsets.UTF_8);
        }else if(var3 > 1){
            return var1.format(var3) + new String(new byte[]{0x20,0x6d,0x69,0x6c,0x6c,0x69,0x73}, StandardCharsets.UTF_8);
        }else {
            return new String(new byte[]{0x69,0x6e,0x73,0x74,0x61,0x6e,0x74}, StandardCharsets.UTF_8); 
        }
    }

    public static String[] convertFrom(Double var11) {
        double var12 = var11 / 1000.0;
        double var13 = var12 / 60.0;
        double var14 = var13 / 60.0;
        double var15 = var14 / 24.0;
        double var16 = var15 / 7.0;
        double var17 = var15 / 30.0;
        double var18 = var15 / 365.0;

        if(var18 > 1) return new String[] {var1.format(var18), new String(new byte[]{0x30}, StandardCharsets.UTF_8)};
        else if(var17 > 1) return new String[] {var1.format(var17), new String(new byte[]{0x31}, StandardCharsets.UTF_8)};
        else if(var16 > 1) return new String[] {var1.format(var16), new String(new byte[]{0x32}, StandardCharsets.UTF_8)};
        else if(var15 > 1) return new String[] {var1.format(var15), new String(new byte[]{0x33}, StandardCharsets.UTF_8)};
        else if(var14 > 1) return new String[] {var1.format(var14), new String(new byte[]{0x34}, StandardCharsets.UTF_8)};
        else if(var13 > 1) return new String[] {var1.format(var13), new String(new byte[]{0x35}, StandardCharsets.UTF_8)};
        else return new String[] {var1.format(var12), new String(new byte[]{0x36}, StandardCharsets.UTF_8)};
    }

}
