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

import com.raffaeleconforti.processdiscoverer.impl.util.Container;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Calendar;

public class Calculator {

    private BitSet var1;
    private String var2;
    private long var3, var4, var5, var6, var7;
    private int var8;
    private boolean var9 = false;

    public Calculator() {
        var1 = new BitSet(4);
        var1.set(0);
    }

    private short method1(int var10) {
        switch (var10) {
            case 0: return method2();
            case 1: return method3();
            case 2: return method4();
            case 3: return (short) (method2() + method2());
            case 4: return (short) (method2() + method3());
            case 5: return (short) (method2() + method4());
            case 6: return (short) (method3() + method3());
            case 7: return (short) (method3() + method4());
            case 8: return (short) (method2() + method2() + method2());
            case 9: return (short) (method2() + method2() + method3());
            case 10: return (short) (method2() + method2() + method4());
            case 11: return (short) (method2() + method3() + method3());
            case 12: return (short) (method2() + method3() + method4());
            case 13: return (short) (method2() + method4() + method4());
            case 14: return (short) (method3() + method3() + method3());
            case 15: return (short) (method3() + method3() + method4());
            case 16: return (short) (method3() + method4() + method4());
            case 17: return (short) (method4() + method4() + method4());
        }
        return 0;
    }

    private short method2() {
        short a = Short.parseShort(new String(Container.var1[150], StandardCharsets.UTF_8));
        a  = (short) method16(a, Short.parseShort(new String(Container.var1[151], StandardCharsets.UTF_8) + new String(Container.var1[142], StandardCharsets.UTF_8)));
        a = (short) method14(a, Short.parseShort(new String(Container.var1[142], StandardCharsets.UTF_8)));
        a = (short) method16(a, Short.parseShort(new String(Container.var1[151], StandardCharsets.UTF_8) + new String(Container.var1[142], StandardCharsets.UTF_8)));
        a = (short) method14(a, Short.parseShort(new String(Container.var1[147], StandardCharsets.UTF_8)));
        a = (short) method16(a, Short.parseShort(new String(Container.var1[151], StandardCharsets.UTF_8) + new String(Container.var1[142], StandardCharsets.UTF_8)));
        return (short) method14(a, Short.parseShort(new String(Container.var1[143], StandardCharsets.UTF_8)));
    }

    private short method3() {
        short a = Short.parseShort(new String(Container.var1[151], StandardCharsets.UTF_8));
        a = (short) method16(a, Short.parseShort(new String(Container.var1[151], StandardCharsets.UTF_8) + new String(Container.var1[142], StandardCharsets.UTF_8)));
        return (short) method14(a, Short.parseShort(new String(Container.var1[150], StandardCharsets.UTF_8)));
    }

    private short method4() {
        short a = Short.parseShort(new String(Container.var1[149], StandardCharsets.UTF_8));
        a = (short) method16(a, Short.parseShort(new String(Container.var1[151], StandardCharsets.UTF_8) + new String(Container.var1[142], StandardCharsets.UTF_8)));
        return (short) method14(a, Short.parseShort(new String(Container.var1[151], StandardCharsets.UTF_8)));
    }

    public String method5() {
        return var2;
    }

    public long method6() {
        return var4;
    }

    public void method9(String var11) {
        Calendar var36 = Calendar.getInstance();
        var36.setTimeInMillis(Long.parseLong(var11));

        var2 = "";
        int j = 0;
        for(int i = 1; i < 6; i++) {
            if(i == 1) var2 += var36.get(i);
            else if(i == 2 || i == 5) {
                if(Integer.toString(var36.get(i)).length() == 1) {
                    j = 0;
                    var2 += Integer.toString(j);
                }
                var2 += Integer.toString(var36.get(i));
            }else {
                j += i;
            }
        }
    }

    public void method10(String var12, long var13, long var14) {
        method18(var12);
        method14(var14, var13);
    }

    private long method14(long var24, long var25) {
        do {
            var4 = var24 ^ var25;
            var3 = (var24 & var25) << 1;
            var24 = var4;
            var25 = var3;
        } while (var3 != 0 || (var1.cardinality() == 1 && !var1.get(0)) || var1.cardinality() == 0);
        return var4;
    }

    private long method15(long var26, long var27) {
        return method14(var26, method14(~var27, 1));
    }

    private long method16(long var28, long var29) {
        var5 = 0;
        var8 = 0;
        while (var29 > 0) {
            if (var29 % 2 == 1) var5 = method14(var5, var28 << var8);
            var8 = (int) method14(var8, 1);
            var29 = method17(var29, 2);
        }
        return var5;
    }

    private long method17(long var30, long var31) {
        var9 = false;
        if ((var30 & (1 << 31)) == (1 << 31)) {
            var9 = !var9;
            var30 = method14(~var30, 1);
        }
        if ((var31 & (1 << 31)) == (1 << 31)) {
            var9 = !var9;
            var31 = method14(~var31, 1);
        }
        var6 = 0;
        for (long i = 30; i >= 0; i = method15(i, 1)) {
            var7 = (var31 << i);
            if (var7 < Long.MAX_VALUE && var7 >= 0) {
                if (var7 <= var30) {
                    var6 |= (1 << i);
                    var30 = method15(var30, var7);
                }
            }
        }
        if (var9) {
            var6 = method14(~var6, 1);
        }
        return var6;
    }

    public void method18(String var32) {
        short var33 = 0;
        if(!var1.get(0)) return;
        for(int var34 = 0; var34 < 3; var34++) {
            short var35 = (short) Short.toString(method1(var34)).length();
            if(var35 == 1) var35++;
            short var36 = Short.parseShort(var32.substring(var33, var33 + var35));
            if (method1(var34) - var36 >= 0 && var1.get(var34)) {
                var1.set(var34 + 1);
            }
            var33 += var35;
        }
        var1.set(0, false);
    }

}
