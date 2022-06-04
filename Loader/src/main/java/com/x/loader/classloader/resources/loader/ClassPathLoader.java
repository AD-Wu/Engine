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

package com.x.loader.classloader.resources.loader;
import com.x.loader.classloader.resources.resource.core.IResource;
import com.x.loader.classloader.resources.loader.core.BaseResourceLoader;
import com.x.loader.classloader.resources.resource.core.IResourceManager;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

/**
 * classpath 资源加载者
 * @author starBlues
 * @version 3.0.0
 */
public class ClassPathLoader extends BaseResourceLoader {

    private final URL url;

    public ClassPathLoader(URL url) {
        super(url);
        this.url = Objects.requireNonNull(url, "url 不能为空");
    }

    public ClassPathLoader(File file) throws MalformedURLException {
        this(file.toPath());
    }

    public ClassPathLoader(Path path) throws MalformedURLException {
        super(path.toUri().toURL());
        this.url = super.baseUrl;
    }

    @Override
    protected void loadOfChild(IResourceManager resourceManager) throws Exception {
        File file = new File(url.toURI());
        load(resourceManager, file, null);
    }

    private void load(IResourceManager resourceManager, File file, String currentPackageName) throws Exception {
        if(currentPackageName == null){
            // 根目录
            currentPackageName = "";
        } else {
            if("".equals(currentPackageName)){
                currentPackageName = file.getName();
            } else {
                currentPackageName = currentPackageName + IResource.PACKAGE_SPLIT + file.getName();
            }
            loadResource(resourceManager, file, currentPackageName);
        }
        if(file.isDirectory()){
            File[] listFiles = file.listFiles();
            if(listFiles == null || listFiles.length == 0){
                return;
            }
            for (File subFile : listFiles) {
                load(resourceManager, subFile, currentPackageName);
            }
        }
    }

    private void loadResource(IResourceManager resourceManager, File file, String packageName) throws Exception{
        if(file.isDirectory()){
            addResource(resourceManager, file, packageName + IResource.PACKAGE_SPLIT);
        } else {
            addResource(resourceManager, file, packageName);
        }
    }

    private void addResource(IResourceManager resourceManager, File file, String packageName) throws Exception {
        resourceManager.add(packageName, new URL(url.toString() + packageName), ()->{
            if(file.exists() && file.isFile()){
                return getClassBytes(file.getPath(), new FileInputStream(file), true);
            } else {
                return null;
            }
        });
    }


}
