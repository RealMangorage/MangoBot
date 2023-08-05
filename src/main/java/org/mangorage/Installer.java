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

package org.mangorage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

// TODO: Remove Installer and Instead have it just list out dependenices which get generated at dev time.
@Deprecated
public class Installer extends SimpleFileVisitor<Path> {

    public static void installResources(Path dst, URL location, String root) throws URISyntaxException, IOException {
        if (location.getProtocol().equals("file")) {
            Path path = Paths.get(location.toURI());
            if (location.getPath().endsWith(".jar")) {
                try (FileSystem fs = FileSystems.newFileSystem(path, (ClassLoader) null)) {
                    installResources(dst, fs.getPath("/" + root));
                }
            } else {
                installResources(dst, path.resolve(root));
            }
        } else {
            throw new IllegalArgumentException("Not supported: " + location);
        }
    }

    private static void installResources(Path dst, Path src) throws IOException {
        Files.walkFileTree(src, new Installer(dst, src));
    }

    private final Path target, source;

    private Installer(Path dst, Path src) {
        target = dst;
        source = src;
    }

    private Path resolve(Path path) {
        return target.resolve(source.relativize(path).toString());
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path dst = resolve(dir);
        Files.createDirectories(dst);
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path dst = resolve(file);
        Files.copy(Files.newInputStream(file), dst, StandardCopyOption.REPLACE_EXISTING);
        return super.visitFile(file, attrs);
    }
}