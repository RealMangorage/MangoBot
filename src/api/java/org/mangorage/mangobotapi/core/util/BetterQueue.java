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

public class BetterQueue<T> {
    private QueueData<T>[] QUEUE;

    public BetterQueue() {
        this(0);
    }

    @SuppressWarnings("unchecked")
    public BetterQueue(int initialSize) {
        QUEUE = (QueueData<T>[]) Array.newInstance(QueueData.class, initialSize);
    }

    public QueueData<T> add(QueueData<T> data) {
        if (contains(data)) {
            return null; // Element already exists in the queue
        }

        int newSize = QUEUE.length + 1;
        QueueData<T>[] newArray = Arrays.copyOf(QUEUE, newSize);

        int newIndex = newSize - 1;
        newArray[newIndex] = data;
        QUEUE = newArray;

        data.updatePosition(newIndex); // Update position

        return data;
    }

    @SuppressWarnings("unchecked")
    public QueueData<T> add(T object) {
        return add(new QueueData<>(object));
    }

    @SuppressWarnings("unchecked")
    public boolean remove(QueueData<T> data) {
        int indexToRemove = -1;

        for (int i = 0; i < QUEUE.length; i++) {
            if (QUEUE[i].equals(data)) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) {
            return false; // Element not found
        }

        int newSize = QUEUE.length - 1;
        QueueData<T>[] newArray = (QueueData<T>[]) Array.newInstance(QueueData.class, newSize);

        int newArrayIndex = 0;

        // Copy the elements before the removed index and update positions
        for (int i = 0; i < indexToRemove; i++) {
            newArray[newArrayIndex] = QUEUE[i];
            if (newArray[newArrayIndex].getPosition() != newArrayIndex) {
                newArray[newArrayIndex].updatePosition(newArrayIndex);
            }
            newArrayIndex++;
        }

        // Copy the elements after the removed index and update positions
        for (int i = indexToRemove + 1; i < QUEUE.length; i++) {
            newArray[newArrayIndex] = QUEUE[i];
            if (newArray[newArrayIndex].getPosition() != newArrayIndex) {
                newArray[newArrayIndex].updatePosition(newArrayIndex);
            }
            newArrayIndex++;
        }

        QUEUE = newArray;

        return true;
    }

    public boolean relocate(QueueData<T> data, int newIndex) {
        // move data to newIndex, has to be within the scope
        // if data is not in the Queue return false

        // A B C D E F G
        // relocate C to the front
        // C A B D E F G

        if (newIndex < 0 || QUEUE.length <= newIndex)
            throw new IllegalStateException("Cannot relocate to %s as it is out of bounds".formatted(newIndex));
        if (data.getPosition() == newIndex)
            return false;
        if (!contains(data))
            return false;

        int fromIndex = data.getPosition();
        QueueData<T> elementToMove = QUEUE[fromIndex];

        if (fromIndex < newIndex) {
            for (int i = fromIndex; i < newIndex; i++) {
                QUEUE[i] = QUEUE[i + 1];
                QUEUE[i + 1].updatePosition(i);
            }
        } else {
            for (int i = fromIndex; i > newIndex; i--) {
                QUEUE[i] = QUEUE[i - 1];

                QUEUE[i - 1].updatePosition(i);
            }
        }

        QUEUE[newIndex] = elementToMove;
        elementToMove.updatePosition(newIndex);

        return true;
    }

    public QueueData<T> poll() {
        if (isEmpty()) return null;

        QueueData<T> data = QUEUE[0];
        remove(data);

        return data;
    }

    public T pollData() {
        QueueData<T> queueData = poll();
        return queueData == null ? null : queueData.getData();
    }

    public QueueData<T> peek() {
        if (QUEUE.length == 0)
            return null;
        return QUEUE[0];
    }

    public T peekData() {
        if (isEmpty()) return null;
        return QUEUE[0].getData();
    }

    public boolean isEmpty() {
        return QUEUE.length == 0;
    }

    public boolean contains(QueueData<T> data) {
        for (QueueData<T> queuedata : QUEUE)
            if (queuedata == data)
                return true;

        return false;
    }

}
