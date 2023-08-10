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

package org.mangorage.mangobot.gui;

import net.dv8tion.jda.api.entities.Message;
import org.mangorage.mangobotapi.core.eventbus.EventBus;
import org.mangorage.mangobotapi.core.events.discord.DMessageRecievedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainScreen extends JComponent implements ActionListener, KeyListener {

    public static void createScreen(EventBus eventBus) {
        JFrame frame = new JFrame();
        frame.setTitle("Mango Bot");

        frame.add(new MainScreen(eventBus, frame));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 450);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        createScreen(EventBus.create());
    }

    public static String convert(Message message) {
        String result = """
                                
                Message ID: %s
                Author ID: %s
                Author Name: %s
                MessageContent:
                %s
                """
                .formatted(
                        message.getId(),
                        message.getAuthor().getId(),
                        message.getAuthor().getName(),
                        message.getContentRaw()
                );

        System.out.print(result);

        return result;
    }

    private final List<Message> MESSAGES = Collections.synchronizedList(new ArrayList<>());
    private final JFrame root;
    private int mark = 0;

    private MainScreen(EventBus eventBus, JFrame rootFrame) {
        this.root = rootFrame;
        root.addKeyListener(this);
        eventBus.addListener(DMessageRecievedEvent.class, this::MessageEvent);
    }

    public void MessageEvent(DMessageRecievedEvent wrappedEvent) {
        var event = wrappedEvent.get();
        var message = event.getMessage();
        MESSAGES.add(message);
    }

    /**
     * @param g the <code>Graphics</code> context in which to paint
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        AtomicInteger y = new AtomicInteger(50);
        int index = 0;

        for (Message m : MESSAGES) {
            g.drawString("%sMessage Author %s".formatted(index == mark ? "->" : "", m.getAuthor().getName()), 10, y.addAndGet(10));
            g.drawString("%sMessage Content %s".formatted(index == mark ? "->" : "", m.getContentRaw()), 10, y.addAndGet(10));
            y.addAndGet(20);
            index++;
        }

    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (mark > 0)
                mark--;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (mark < MESSAGES.size())
                mark++;
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            var m = MESSAGES.get(mark);
            if (mark > 0)
                mark--;

            m.delete().queue();
            MESSAGES.remove(m);
        }

        repaint();
    }

    /**
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {

    }
}
