/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.x.loader.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.jar.Manifest;

/**
 * copy from spring-boot-loader
 * @author starBlues
 * @version 3.0.0
 */
public class ExplodedArchive implements IArchive {

    private static final Set<String> SKIPPED_NAMES = new HashSet<>(Arrays.asList(".", ".."));

    /**
     * 当前文件(war)的路径
     */
    private final File root;

    /**
     * 是否递归(默认true)
     */
    private final boolean recursive;

    /**
     * 声明文件路径
     */
    private final File manifestFile;

    /**
     * 声明文件
     */
    private Manifest manifest;

    /**
     * Create a new {@link ExplodedArchive} instance.
     * @param root the root directory
     */
    public ExplodedArchive(File root) {
        this(root, true);
    }

    /**
     * Create a new {@link ExplodedArchive} instance.
     * @param root the root directory
     * @param recursive if recursive searching should be used to locate the manifest.
     * Defaults to {@code true}, directories with a large tree might want to set this to
     * {@code false}.
     */
    public ExplodedArchive(File root, boolean recursive) {
        /**
         * 判断文件是否存在&必须是目录
         */
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException("Invalid source directory " + root);
        }
        this.root = root;
        this.recursive = recursive;
        this.manifestFile = getManifestFile(root);
    }

    /**
     * 获取声明文件路径
     * @param root
     * @return
     */
    private File getManifestFile(File root) {
        // 在主文件里的META-INF里找文件:MANIFEST.MF
        File metaInf = new File(root, "META-INF");
        return new File(metaInf, "MANIFEST.MF");
    }

    @Override
    public URL getUrl() throws MalformedURLException {
        return this.root.toURI().toURL();
    }

    @Override
    public Manifest getManifest() throws IOException {
        if (this.manifest == null && this.manifestFile.exists()) {
            try (FileInputStream in = new FileInputStream(this.manifestFile)) {
                this.manifest = new Manifest(in);
            }
        }
        return this.manifest;
    }

    @Override
    public Iterator<IArchive> getNestedArchives(EntryFilter searchFilter, EntryFilter includeFilter) throws IOException {
        return new ArchiveIterator(this.root, this.recursive, searchFilter, includeFilter);
    }

    @Override
    public Iterator<Entry> iterator() {
        return new EntryIterator(this.root, this.recursive, null, null);
    }

    protected IArchive getNestedArchive(Entry entry) throws IOException {
        File file = ((FileEntry) entry).getFile();
        return (file.isDirectory() ? new ExplodedArchive(file) : new SimpleJarFileArchive((FileEntry) entry));
    }

    @Override
    public boolean isExploded() {
        return true;
    }

    @Override
    public String toString() {
        try {
            return getUrl().toString();
        }
        catch (Exception ex) {
            return "exploded archive";
        }
    }

    /**
     * File based {@link Entry} {@link Iterator}.
     */
    private abstract static class AbstractIterator<T> implements Iterator<T> {

        private static final Comparator<File> ENTRY_COMPARATOR = Comparator.comparing(File::getAbsolutePath);

        private final File root;

        private final boolean recursive;

        private final EntryFilter searchFilter;

        private final EntryFilter includeFilter;

        private final Deque<Iterator<File>> stack = new LinkedList<>();

        private FileEntry current;

        private final String rootUrl;

        AbstractIterator(File root, boolean recursive, EntryFilter searchFilter, EntryFilter includeFilter) {
            this.root = root;
            this.rootUrl = this.root.toURI().getPath();
            this.recursive = recursive;
            this.searchFilter = searchFilter;
            this.includeFilter = includeFilter;
            this.stack.add(listFiles(root));
            this.current = poll();
        }

        @Override
        public boolean hasNext() {
            return this.current != null;
        }

        @Override
        public T next() {
            FileEntry entry = this.current;
            if (entry == null) {
                throw new NoSuchElementException();
            }
            this.current = poll();
            return adapt(entry);
        }

        private FileEntry poll() {
            while (!this.stack.isEmpty()) {
                Iterator<File> peek = this.stack.peek();
                if(peek == null){
                    continue;
                }
                while (peek.hasNext()) {
                    File file = peek.next();
                    if (SKIPPED_NAMES.contains(file.getName())) {
                        continue;
                    }
                    // 将file进行细化封装
                    FileEntry entry = getFileEntry(file);
                    // 判断文件是否还有子文件,可遍历
                    if (isListable(entry)) {
                        this.stack.addFirst(listFiles(file));
                    }
                    if (this.includeFilter == null || this.includeFilter.matches(entry)) {
                        return entry;
                    }
                }
                this.stack.poll();
            }
            return null;
        }

        private FileEntry getFileEntry(File file) {
            URI uri = file.toURI();
            // 除去根路径的名字,即自身文件名
            String name = uri.getPath().substring(this.rootUrl.length());
            try {
                return new FileEntry(name, file, uri.toURL());
            }
            catch (MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
        }

        private boolean isListable(FileEntry entry) {
            return entry.isDirectory() && (this.recursive || entry.getFile().getParentFile().equals(this.root))
                    && (this.searchFilter == null || this.searchFilter.matches(entry))
                    && (this.includeFilter == null || !this.includeFilter.matches(entry));
        }

        private Iterator<File> listFiles(File file) {
            // 返回根文件的所有子文件和文件夹
            File[] files = file.listFiles();
            if (files == null) {
                return Collections.emptyIterator();
            }
            // 将路径排序
            Arrays.sort(files, ENTRY_COMPARATOR);
            // 将所有的文件封装到迭代器里
            return Arrays.asList(files).iterator();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        protected abstract T adapt(FileEntry entry);

    }

    private static class EntryIterator extends AbstractIterator<Entry> {

        EntryIterator(File root, boolean recursive, EntryFilter searchFilter, EntryFilter includeFilter) {
            super(root, recursive, searchFilter, includeFilter);
        }

        @Override
        protected Entry adapt(FileEntry entry) {
            return entry;
        }

    }

    private static class ArchiveIterator extends AbstractIterator<IArchive> {

        ArchiveIterator(File root, boolean recursive, EntryFilter searchFilter, EntryFilter includeFilter) {
            super(root, recursive, searchFilter, includeFilter);
        }

        @Override
        protected IArchive adapt(FileEntry entry) {
            File file = entry.getFile();
            return (file.isDirectory() ? new ExplodedArchive(file) : new SimpleJarFileArchive(entry));
        }

    }

    /**
     * {@link Entry} backed by a File.
     */
    private static class FileEntry implements Entry {

        /**
         * 自身文件名
         */
        private final String name;

        /**
         * 文件路径(包括父级)
         */
        private final File file;

        private final URL url;

        FileEntry(String name, File file, URL url) {
            this.name = name;
            this.file = file;
            this.url = url;
        }

        File getFile() {
            return this.file;
        }

        @Override
        public boolean isDirectory() {
            return this.file.isDirectory();
        }

        @Override
        public String getName() {
            return this.name;
        }

        URL getUrl() {
            return this.url;
        }

    }

    /**
     * {@link IArchive} implementation backed by a simple JAR file that doesn't itself
     * contain nested archives.
     */
    private static class SimpleJarFileArchive implements IArchive {

        private final URL url;

        SimpleJarFileArchive(FileEntry file) {
            this.url = file.getUrl();
        }

        @Override
        public URL getUrl() throws MalformedURLException {
            return this.url;
        }

        @Override
        public Manifest getManifest() throws IOException {
            return null;
        }

        @Override
        public Iterator<IArchive> getNestedArchives(EntryFilter searchFilter, EntryFilter includeFilter)
                throws IOException {
            return Collections.emptyIterator();
        }

        @Override
        @Deprecated
        public Iterator<Entry> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public String toString() {
            try {
                return getUrl().toString();
            }
            catch (Exception ex) {
                return "jar archive";
            }
        }

    }

}
