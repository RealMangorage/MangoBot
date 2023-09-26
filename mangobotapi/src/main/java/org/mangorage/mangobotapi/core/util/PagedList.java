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

package org.mangorage.mangobotapi.core.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public class PagedList<T> {
    private Page<T>[] pages;
    private int page_id = 0;
    private int entries = 0;

    public PagedList() {
    }

    public int totalPages() {
        return pages.length;
    }

    public Page<T> next() {
        if (page_id + 1 >= pages.length)
            page_id = 0;
        else
            page_id++;

        return pages[page_id];
    }

    public Page<T> current() {
        return pages[page_id];
    }

    public Page<T> previous() {
        if (page_id <= 0)
            page_id = pages.length - 1;
        else
            page_id--;

        return pages[page_id];
    }

    public int getPage() {
        return page_id + 1; // Add one so 0 -> 1, 1 -> 2, etc
    }

    /**
     * @param data    -> raw Data before proeccesing it
     * @param entries -> Entries per page
     */
    @SuppressWarnings("unchecked")
    public void rebuild(T[] data, int entries) {
        if (data.length <= 0)
            return;

        this.entries = entries;

        Class<T> type = (Class<T>) data[0].getClass();
        int index = 0;
        int page_index = 0;
        int remaining = data.length;
        int totalPages = (int) Math.abs(Math.ceil((double) data.length / (double) entries));

        // Create pages
        this.pages = (Page<T>[]) Array.newInstance(Page.class, totalPages);

        while (remaining > 0) {
            // create Array for next Entry list
            T[] entries_array = (T[]) Array.newInstance(type, entries);

            int remaining_entrys = entries;
            int entry_index = 0;

            while (remaining_entrys > 0 && index < data.length) {
                entries_array[entry_index] = data[index];

                entry_index++;
                index++;
                remaining_entrys--;
                remaining--;
            }

            this.pages[page_index] = new Page<>(type, entries_array);
            page_index++;
        }
    }

    public int getEntriesPerPage() {
        return entries;
    }

    public static final class Page<T> {
        private final T[] entries;

        @SuppressWarnings("unchecked")
        public Page(Class<T> type, T[] entries) {
            this.entries = (T[]) Array.newInstance(type, (int) Arrays.stream(entries).filter(Objects::nonNull).count());
            int index = 0;
            for (T entry : entries) {
                if (entry != null) {
                    this.entries[index] = entry;
                    index++;
                }
            }
        }

        public T[] getEntries() {
            return entries;
        }
    }
}
