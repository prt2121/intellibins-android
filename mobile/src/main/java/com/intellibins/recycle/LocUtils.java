/*
 * Copyright (c) 2015 Intellibins authors
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *   * Neither the name of The Intern nor the names of its contributors may
 * be used to endorse or promote products derived from this software
 * without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE LISTED COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intellibins.recycle;

import com.intellibins.recycle.model.Loc;

import android.location.Location;

import rx.functions.Func2;

/**
 * Created by prt2121 on 1/1/15.
 */
public class LocUtils {

    private static double myLatitude;

    private static double myLongitude;

    public static Func2<Loc, Loc, Integer> compare(double lat, double lng) {
        myLatitude = lat;
        myLongitude = lng;
        return f;
    }

    public static Func2<Loc, Loc, Integer> f = new Func2<Loc, Loc, Integer>() {
        @Override
        public Integer call(Loc loc1, Loc loc2) {
            float[] result1 = new float[3];
            Location.distanceBetween(myLatitude, myLongitude,
                    loc1.latitude, loc1.longitude,
                    result1);
            Float distance1 = result1[0];
            float[] result2 = new float[3];
            Location.distanceBetween(myLatitude, myLongitude,
                    loc2.latitude, loc2.longitude,
                    result2);
            Float distance2 = result2[0];
            return distance1.compareTo(distance2);
        }
    };

}
