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

package com.x.loader.classloader.resources.loader.core;

import com.x.loader.classloader.resources.resource.core.IResource;
import com.x.loader.classloader.resources.loader.ClassPathLoader;
import com.x.loader.classloader.resources.loader.JarResourceLoader;
import com.x.loader.classloader.resources.resource.factory.BaseResourceManager;
import com.x.loader.classloader.resources.resource.core.IResourceManager;
import com.x.loader.launcher.ResourceLoaderFactoryGetter;
import com.x.loader.utils.IOUtils;
import com.x.loader.utils.ResourceUtils;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的资源加载工厂
 */
public class DefaultResourceLoaderFactory implements IResourceLoaderFactory {

    private final Map<URL, BaseResourceManager> resourceLoaderMap = new ConcurrentHashMap<>();

    private final String classLoaderName;

    public DefaultResourceLoaderFactory(String classLoaderName) {
        this.classLoaderName = classLoaderName;
    }


    @Override
    public void addResource(String path) throws Exception{
        if(path == null || "".equals(path)){
            return;
        }
        addResource(Paths.get(path));
    }

    @Override
    public void addResource(File file) throws Exception{
        if(file == null){
            return;
        }
        addResource(file.toPath());
    }

    @Override
    public void addResource(Path path) throws Exception{
        if(path == null){
            return;
        }
        if(!Files.exists(path)){
            return;
        }
        addResource(path.toUri().toURL());
    }

    @Override
    public void addResource(URL url) throws Exception{
        BaseResourceLoader resourceLoader = null;
        if(ResourceUtils.isJarFileUrl(url)) {
            if(ResourceUtils.isJarProtocolUrl(url)){
                resourceLoader = new JarResourceLoader(url);
            } else {
                resourceLoader = new JarResourceLoader(Paths.get(url.toURI()).toFile());
            }
        } else if(ResourceUtils.isZipFileUrl(url)){
            resourceLoader = new JarResourceLoader(Paths.get(url.toURI()).toFile());
        } else if(ResourceUtils.isFileUrl(url)){
            resourceLoader = new ClassPathLoader(url);
        }
        if(resourceLoader != null){
            addResource(resourceLoader);
        }
    }

    @Override
    public void addResource(IResourceLoader resourceLoader) throws Exception {
        if(resourceLoader == null){
            return;
        }
        if (resourceLoaderMap.containsKey(resourceLoader.getBaseUrl())) {
            return;
        }
        BaseResourceManager resourceManager = ResourceLoaderFactoryGetter.getResourceManager(
                classLoaderName,
                resourceLoader.getBaseUrl());
        resourceLoader.load(resourceManager);
        if(!resourceManager.isEmpty()){
            resourceLoaderMap.put(resourceLoader.getBaseUrl(), resourceManager);
        }
    }

    @Override
    public IResource findResource(String name) {
        for (Map.Entry<URL, BaseResourceManager> entry : resourceLoaderMap.entrySet()) {
            IResourceManager resourceManager = entry.getValue();
            IResource resource = resourceManager.get(name);
            if(resource != null){
                return resource;
            }
        }
        return null;
    }

    @Override
    public Enumeration<IResource> findResources(String name) {
        return new Enumeration<IResource>() {
            private final List<BaseResourceManager> list  = new ArrayList<>(resourceLoaderMap.values());
            private int index = 0;
            private IResource resource = null;

            @Override
            public boolean hasMoreElements() {
                return next();
            }

            @Override
            public IResource nextElement() {
                if (!next()) {
                    throw new NoSuchElementException();
                }
                IResource r = resource;
                resource = null;
                return r;
            }

            private boolean next() {
                if (resource != null) {
                    return true;
                } else {
                    BaseResourceManager resourceManager;
                    while (index < list.size()){
                        resourceManager = list.get(index++);
                        resource = getResource(resourceManager);
                        if(resource != null){
                            return true;
                        }
                    }
                    return false;
                }
            }

            private IResource getResource(BaseResourceManager resourceStorage){
                resource = resourceStorage.get(name);
                if(resource != null){
                    return resource;
                }
                return null;
            }
        };
    }

    @Override
    public InputStream getInputStream(String name) {
        for (Map.Entry<URL, BaseResourceManager> entry : resourceLoaderMap.entrySet()) {
            IResourceManager resourceStorage = entry.getValue();
            InputStream inputStream = resourceStorage.getInputStream(name);
            if(inputStream != null){
                return inputStream;
            }
        }
        return null;
    }

    @Override
    public List<URL> getUrls() {
        return new ArrayList<>(resourceLoaderMap.keySet());
    }

    @Override
    public void close() throws Exception {
        for (IResourceManager resourceStorage : resourceLoaderMap.values()) {
            IOUtils.closeQuietly(resourceStorage);
        }
        resourceLoaderMap.clear();
    }

}
