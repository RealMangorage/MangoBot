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

package org.mangorage.mangobotapi.core.config;

public class ConfigSetting<T> implements ISetting<T> {
    public static ConfigSetting<String> create(Config config, String ID, String defaultValue) {
        return new ConfigSetting<>(config, ID, Transformers.STRING, defaultValue);
    }

    public static <T> ConfigSetting<T> create(Config config, String ID, Transformer<T, String> transformer, T defaultValue) {
        return new ConfigSetting<>(config, ID, transformer, defaultValue);
    }

    private final Config config;
    private final String id;
    private final T defaultvalue;
    private final ITransformer<T, String> transformer;
    private final ITransformer<String, T> transformerReversed;

    private ConfigSetting(Config config, String ID, Transformer<T, String> transformer, T defaultvalue) {
        this.config = config;
        this.id = ID;
        this.transformer = transformer.getTransformer();
        this.transformerReversed = transformer.getTransformerReversed();
        this.defaultvalue = defaultvalue;
    }

    private ConfigSetting(Config config, String ID, Transformer<T, String> transformer) {
        this(config, ID, transformer, null);
    }

    protected String getRaw() {
        return config.get(id);
    }

    @Override
    public T get() {
        var result = transformer.transform(getRaw());
        return result != null ? result : defaultvalue;
    }

    @Override
    public void set(T value) {
        config.set(id, transformerReversed.transform(value));
        config.save();
    }
}
