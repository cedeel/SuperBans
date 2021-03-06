/*
* Copyright (c) 2013 cedeel.
* All rights reserved.
*
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* * Redistributions of source code must retain the above copyright
* notice, this list of conditions and the following disclaimer.
* * Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* * The name of the author may not be used to endorse or promote products
* derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS ``AS IS''
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
* ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package be.darnell.superbans.util;

import java.util.List;

public class Formatting {

    public static long parseTimeSpec(String time, String unit) {
        long sec;
        try {
            sec = Integer.parseInt(time)*60;
        } catch (NumberFormatException ex) {
            return 0;
        }

        if (unit.toLowerCase().startsWith("h")){
            sec *= 60;
        }else if (unit.toLowerCase().startsWith("d")){
            sec *= (60*24);
        }else if (unit.toLowerCase().startsWith("w")){
            sec *= (7*60*24);
        }else if (unit.toLowerCase().startsWith("mo")){
            sec *= (30*60*24);
        }else if (unit.toLowerCase().startsWith("m")){
            sec *= 1;
        }else if (unit.toLowerCase().startsWith("s")){
            sec /= 60;
        }
        return sec;
    }

    public static String combineStrings(List<String> list, int first, int last) {
        StringBuilder sb = new StringBuilder(19);
        for (int i = first; i<last; i++)
            sb.append(list.get(i)).append(" ");
        return sb.toString().trim();
    }
}
