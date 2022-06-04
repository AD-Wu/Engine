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


import com.x.loader.classloader.resources.resource.core.IResourceManager;
import com.x.loader.utils.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * 抽象的资源加载者
 */
public abstract class BaseResourceLoader implements IResourceLoader {

    private boolean loaded = false;

    protected final URL baseUrl;

    protected BaseResourceLoader(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public URL getBaseUrl() {
        return baseUrl;
    }

    /**
     * 初始化 resource
     * @throws Exception 初始异常
     */
    @Override
    public final synchronized void load(IResourceManager resourceManager) throws Exception{
        if(loaded){
            throw new Exception(this.getClass().getName()+": 已经初始化了, 不能再初始化!");
        }
        try {
            // 添加root 路径
            resourceManager.add("/", baseUrl);
            loadOfChild(resourceManager);
        } finally {
            loaded = true;
        }
    }

    /**
     * 子类初始化实现
     * @throws Exception 初始异常
     */
    protected abstract void loadOfChild(IResourceManager resourceManager) throws Exception;

    protected byte[] getClassBytes(String path, InputStream in, boolean isClose) throws Exception{
        if(!isClass(path)){
            return null;
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            IOUtils.copy(in, out);
            return out.toByteArray();
        } finally {
            if(isClose){
                IOUtils.closeQuietly(in);
            }
            IOUtils.closeQuietly(out);
        }
    }

    @Override
    public void close() throws Exception {

    }

    private static boolean isClass(String path){
        if(path == null || "".equals(path)){
            return false;
        }
        return path.toLowerCase().endsWith(".class");
    }

}
