/*
  *  Copyright (C) 2022 github.com/REAndroid
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package com.reandroid.apk;

import com.reandroid.archive.BlockInputSource;
import com.reandroid.archive.ZipEntryMap;
import com.reandroid.archive.block.ApkSignatureBlock;
import com.reandroid.arsc.chunk.TableBlock;
import com.reandroid.arsc.pool.TableStringPool;
import com.reandroid.arsc.pool.builder.StringPoolMerger;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApkBundle implements Closeable {
    private final Map<String, ApkModule> mModulesMap;

    public ApkBundle(){
        this.mModulesMap=new HashMap<>();
    }

    public ApkModule mergeModules() throws IOException {
        List<ApkModule> moduleList=getApkModuleList();
        if(moduleList.isEmpty()){
            throw new FileNotFoundException("Nothing to merge, empty modules");
        }
        ApkModule result = new ApkModule(generateMergedModuleName(), new ZipEntryMap());
        result.setLoadDefaultFramework(false);

        mergeStringPools(result);

        ApkModule base=getBaseModule();
        if(base==null){
            base=getLargestTableModule();
        }
        result.merge(base);
        ApkSignatureBlock signatureBlock = null;
        for(ApkModule module:moduleList){
            ApkSignatureBlock asb = module.getApkSignatureBlock();
            if(module==base){
                if(asb != null){
                    signatureBlock = asb;
                }
                continue;
            }
            if(signatureBlock == null){
                signatureBlock = asb;
            }
            result.merge(module);
        }

        result.setApkSignatureBlock(signatureBlock);

        if(result.hasTableBlock()){
            TableBlock tableBlock=result.getTableBlock();
            tableBlock.sortPackages();
            tableBlock.refresh();
        }
        result.getZipEntryMap().autoSortApkFiles();
        return result;
    }
    private void mergeStringPools(ApkModule mergedModule) throws IOException {
        if(!hasOneTableBlock() || mergedModule.hasTableBlock()){
            return;
        }
        //LogUtil.logMessage("Merging string pools ... ");
        TableBlock createdTable = new TableBlock();
        BlockInputSource<TableBlock> inputSource=
                new BlockInputSource<>(TableBlock.FILE_NAME, createdTable);
        mergedModule.getZipEntryMap().add(inputSource);

        StringPoolMerger poolMerger = new StringPoolMerger();

        for(ApkModule apkModule:getModules()){
            if(!apkModule.hasTableBlock()){
                continue;
            }
            TableStringPool stringPool = apkModule.getVolatileTableStringPool();
            poolMerger.add(stringPool);
        }

        poolMerger.mergeTo(createdTable.getTableStringPool());

        //LogUtil.logMessage("Merged string pools="+poolMerger.getMergedPools() +", style="+poolMerger.getMergedStyleStrings()+", strings="+poolMerger.getMergedStrings());
    }
    private String generateMergedModuleName(){
        Set<String> moduleNames=mModulesMap.keySet();
        String merged="merged";
        int i=1;
        String name=merged;
        while (moduleNames.contains(name)){
            name=merged+"_"+i;
            i++;
        }
        return name;
    }
    private ApkModule getLargestTableModule(){
        ApkModule apkModule=null;
        int chunkSize=0;
        for(ApkModule module:getApkModuleList()){
            if(!module.hasTableBlock()){
                continue;
            }
            TableBlock tableBlock=module.getTableBlock();
            int size=tableBlock.getHeaderBlock().getChunkSize();
            if(apkModule==null || size>chunkSize){
                chunkSize=size;
                apkModule=module;
            }
        }
        return apkModule;
    }
    public ApkModule getBaseModule(){
        for(ApkModule module:getApkModuleList()){
            if(module.isBaseModule()){
                return module;
            }
        }
        return null;
    }
    public List<ApkModule> getApkModuleList(){
        return new ArrayList<>(mModulesMap.values());
    }

    public void loadApkDirectory(File dir, boolean recursive) throws IOException {
        if(!dir.isDirectory()) throw new FileNotFoundException("No such directory: " + dir);
        List<File> apkList = recursive ? ApkUtil.recursiveFiles(dir, ".apk") : ApkUtil.listFiles(dir, ".apk");
        if(apkList.isEmpty()) throw new FileNotFoundException("No '*.apk' files in directory: " + dir);
        //LogUtil.logMessage("Found apk files: "+apkList.size());
        load(apkList);
    }

    private void load(List<File> apkList) throws IOException {
        for(File file : apkList) {
            //LogUtil.logMessage("Loading: "+file.getName());
            addModule(ApkModule.loadApkFile(file, ApkUtil.toModuleName(file)));
        }
    }

    public void addModule(ApkModule apkModule){
        apkModule.setLoadDefaultFramework(false);
        String name = apkModule.getModuleName();
        mModulesMap.remove(name);
        mModulesMap.put(name, apkModule);
    }
    public Collection<ApkModule> getModules(){
        return mModulesMap.values();
    }
    private boolean hasOneTableBlock(){
        for(ApkModule apkModule:getModules()){
            if(apkModule.hasTableBlock()){
                return true;
            }
        }
        return false;
    }
    @Override
    public void close() throws IOException {
        for(ApkModule module : mModulesMap.values()) {
            module.close();
        }
        mModulesMap.clear();
    }
}
