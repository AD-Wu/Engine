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

package com.x.loader.classloader.resources.resource.factory;

import com.x.loader.classloader.resources.IResourceByteGetter;
import com.x.loader.classloader.resources.resource.core.IResource;
import com.x.loader.utils.Assert;
import com.x.loader.utils.IOUtils;
import com.x.loader.utils.ObjectUtils;
import com.x.loader.utils.ResourceUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的资源存储者
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultResourceManager extends BaseResourceManager {

    protected final Map<String, IResource> resourceStorage = new ConcurrentHashMap<>();

    public DefaultResourceManager(URL baseUrl) {
        super(baseUrl);
    }

    @Override
    public void add(String name, URL url, IResourceByteGetter byteGetter) throws Exception{
        Assert.isNotEmpty(name, "name 不能为空");
        Assert.isNotNull(url, "url 不能为空");
        // 将name统一格式化为a/b/c格式
        name = formatResourceName(name);
        if(resourceStorage.containsKey(name)){
            return;
        }
        DefaultResource defaultResource = new DefaultResource(name, baseUrl, url);
        resourceStorage.put(name, defaultResource);
    }

    @Override
    public void add(String name, URL url) throws Exception{
        this.add(name, url, null);
    }

    @Override
    public boolean exist(String name) {
        if(ObjectUtils.isEmpty(name)){
            return false;
        }
        name = formatResourceName(name);
        return resourceStorage.containsKey(name);
    }

    protected void addResource(String name, IResource resource){
        Assert.isNotEmpty(name, "name 不能为空");
        Assert.isNotNull(resource, "resource 不能为空");
        resourceStorage.put(name, resource);
    }

    @Override
    public IResource get(String name) {
        if(ObjectUtils.isEmpty(name)){
            return null;
        }
        name = formatResourceName(name);
        return resourceStorage.get(name);
    }

    @Override
    public InputStream getInputStream(String name) {
        if(ObjectUtils.isEmpty(name)){
            return null;
        }
        name = formatResourceName(name);
        IResource resourceInfo = resourceStorage.get(name);
        if (resourceInfo != null) {
            try (InputStream inputStream = resourceInfo.getUrl().openStream();
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
                IOUtils.copy(inputStream, byteArrayOutputStream);
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public List<IResource> getAll() {
        return new ArrayList<>(resourceStorage.values());
    }

    @Override
    public boolean isEmpty() {
        return resourceStorage.isEmpty();
    }

    @Override
    public void close() throws Exception {
        for (IResource resource : resourceStorage.values()) {
            IOUtils.closeQuietly(resource);
        }
        resourceStorage.clear();
    }

    /**
     * 将name统一格式化为a/b/c的格式
     * @param name
     * @return
     */
    protected final String formatResourceName(String name) {
        return ResourceUtils.formatStandardName(name);
    }

}
