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

package org.mangorage.mangobotapi.core.modules.buttonactions;

import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

import java.util.HashMap;

public class ButtonActions {
    public enum Type {
        PERMANENT,
        TEMPORARY
    }

    private static final HashMap<String, PermanentButtonAction> PERMANENT_BUTTON_ACTIONS = new HashMap<>();

    public static class PermanentButtonAction<T> implements IPermanentButtonAction<T>, IDataResolver<T> {

        private final IPermanentButtonAction<T> action;
        private final IDataResolver<T> resolver;
        private final String id;


        private PermanentButtonAction(String id, IPermanentButtonAction<T> action, IDataResolver<T> resolver) {
            this.id = id;
            this.action = action;
            this.resolver = resolver;
        }


        @Override
        public boolean onAction(ComponentInteraction interaction, T data) {
            return action.onAction(interaction, data);
        }

        @Override
        public T resolve(String[] data) {
            return resolver.resolve(data);
        }

        public String getId(String... args) {
            var result = "%s:%s".formatted(Type.PERMANENT, id);
            if (args.length > 0)
                result += ":" + String.join(":", args);

            return result;
        }
    }

    public interface IPermanentButtonAction<T> {
        boolean onAction(ComponentInteraction interaction, T data);
    }

    public interface IDataResolver<T> {
        T resolve(String[] data);
    }

    public interface ITemporaryButtonAction {
        boolean onAction(ComponentInteraction interaction);
    }


    public static <T> PermanentButtonAction<T> registerPermanent(String id, Class<T> dataType, IPermanentButtonAction<T> action, IDataResolver<T> resolver) {
        var result = new PermanentButtonAction<>(id, action, resolver);
        PERMANENT_BUTTON_ACTIONS.put(id, result);
        return result;
    }

    // ComponentID -> Type:ID:DataA:DataB:DataC ... etc

    @SuppressWarnings("unchecked")
    public static boolean post(ComponentInteraction interaction) {
        var rawId = interaction.getComponentId();
        String[] raw = rawId.split(":");

        if (raw.length < 2)
            return false;

        var type = raw[0];
        var id = raw[1];
        String[] data;

        if (raw.length > 2) {
            data = new String[raw.length - 2];
            System.arraycopy(raw, 2, data, 0, data.length);
        } else {
            data = new String[0];
        }

        switch (Type.valueOf(type)) {
            case PERMANENT -> {
                var action = PERMANENT_BUTTON_ACTIONS.get(id);
                if (action == null) return false;

                var resolved = action.resolve(data);
                return action.onAction(interaction, resolved);
            }
            case TEMPORARY -> {

            }
        }

        return false;
    }
}
