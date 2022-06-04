/**
 * Copyright [2019-2022] [starBlues]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.x.loader.launcher;

import com.x.loader.archive.IArchive;
import com.x.loader.archive.ExplodedArchive;
import com.x.loader.archive.JarFileArchive;
import com.x.loader.classloader.GenericClassLoader;
import com.x.loader.classloader.resources.loader.JarResourceLoader;
import com.x.loader.launcher.runner.MethodRunner;

import com.x.loader.archive.IArchive.EntryFilter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Objects;

/**
 * 主程序jar in jar 模式启动者
 * @author starBlues
 * @version 3.0.0
 */
public class MainJarProgramLauncher extends MainProgramLauncher {

    private static final String PROD_CLASSES_PATH = "classes/";
    private static final String PROD_CLASSES_URL_SIGN = "/classes!/";

    private static final String PROD_LIB_PATH = "lib/";

    private final static EntryFilter ENTRY_FILTER = (entry) -> {
        String name = entry.getName();
        return name.startsWith(PROD_CLASSES_PATH) || name.startsWith(PROD_LIB_PATH);
    };

    private final static EntryFilter INCLUDE_FILTER = (entry) -> {
        if (entry.isDirectory()) {
            return entry.getName().equals(PROD_CLASSES_PATH);
        }
        return entry.getName().startsWith(PROD_LIB_PATH);
    };

    private final File rootJarFile;

    public MainJarProgramLauncher(MethodRunner methodRunner, File rootJarFile) {
        super(methodRunner);
        this.rootJarFile = Objects.requireNonNull(rootJarFile, "参数 rootJarFile 不能为空");
    }

    @Override
    protected void addResource(GenericClassLoader classLoader) throws Exception {
        super.addResource(classLoader);
        IArchive archive = getArchive();
        Iterator<IArchive> archiveIterator = archive.getNestedArchives(ENTRY_FILTER, INCLUDE_FILTER);
        addLibResource(archiveIterator, classLoader);
    }

    private IArchive getArchive() throws IOException {
        if (rootJarFile.isDirectory()) {
            return new ExplodedArchive(rootJarFile);
        }
        return new JarFileArchive(rootJarFile);
    }

    private void addLibResource(Iterator<IArchive> archives, GenericClassLoader classLoader) throws Exception {
        while (archives.hasNext()) {
            IArchive archive = archives.next();
            URL url = archive.getUrl();
            String path = url.getPath();
            if (path.contains(PROD_CLASSES_URL_SIGN)) {
                classLoader.addResource(new MainJarResourceLoader(url));
            } else {
                classLoader.addResource(new JarResourceLoader(url));
            }
        }
    }

    private static class MainJarResourceLoader extends JarResourceLoader {

        public MainJarResourceLoader(URL url) throws Exception {
            super(url);
        }

        @Override
        protected String resolveName(String name) {
            return name.replace(PROD_CLASSES_PATH, "");
        }
    }


}
