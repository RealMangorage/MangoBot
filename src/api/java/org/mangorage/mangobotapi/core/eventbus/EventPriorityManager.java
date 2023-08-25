/*
 * Copyright (c) 2023. MangoRage
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.mangorage.mangobotapi.core.eventbus;

import java.util.ArrayList;
import java.util.List;

public class EventPriorityManager {
    private static final ArrayList<String> S = new ArrayList<>();

    public static void main(String[] args) {
        String HIGHEST = "A";
        String HIGH = "B";
        String NORMAL = "C";
        String LOW = "D";
        String LOWEST = "E";

        S.addAll(List.of(HIGHEST, HIGH, NORMAL, LOW, LOWEST));

        String SUPER_HIGHEST = "SUPER";

        OrderAfter(HIGHEST, SUPER_HIGHEST);
        OrderBefore(HIGHEST, SUPER_HIGHEST);
        boolean flag = true;
    }

    public static void OrderBefore(String A, String B) {
        S.remove(B);
        int index = S.indexOf(A);
        S.add(index, B);
    }

    public static void OrderAfter(String A, String B) {
        S.remove(B);
        int index = S.indexOf(A) + 1;
        S.add(index, B);
    }

    private static final EventPriorityManager INSTANCE = new EventPriorityManager();

    public static EventPriorityManager getInstance() {
        return INSTANCE;
    }

    private final ArrayList<EventPriority> PRIORITIES = new ArrayList<>();

    protected EventPriorityManager() {

    }

    public void addAfter(EventPriority after, EventPriority priority) {

    }

    public void addBefore(EventPriority before, EventPriority priority) {

    }
}
