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
package com.reandroid.apkeditor.merge;

import static com.reandroid.apkeditor.merge.LogUtil.logMessage;

import android.content.Context;
import android.net.Uri;

import io.github.abdurazaaqmohammed.ApkExtractor.R;
import io.github.abdurazaaqmohammed.ApkExtractor.MainActivity;
import io.github.abdurazaaqmohammed.ApkExtractor.SignUtil;

import com.reandroid.apk.ApkBundle;
import com.reandroid.apk.ApkModule;
import com.reandroid.apkeditor.common.AndroidManifestHelper;
import com.reandroid.app.AndroidManifest;
import com.reandroid.archive.ZipEntryMap;
import com.reandroid.arsc.chunk.TableBlock;
import com.reandroid.arsc.chunk.xml.AndroidManifestBlock;
import com.reandroid.arsc.chunk.xml.ResXmlAttribute;
import com.reandroid.arsc.chunk.xml.ResXmlElement;
import com.reandroid.arsc.container.SpecTypePair;
import com.reandroid.arsc.model.ResourceEntry;
import com.reandroid.arsc.value.Entry;
import com.reandroid.arsc.value.ResValue;
import com.reandroid.arsc.value.ValueType;
import com.starry.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Merger {

    public interface LogListener {
        void onLog(String log);

        void onLog(int resID);
    }

    public static void run(ApkBundle bundle, File cacheDir, Uri out, Context context, boolean signApk) throws IOException {
        logMessage("Found modules: " + bundle.getApkModuleList().size());

        try (ApkModule mergedModule = bundle.mergeModules()) {
            if (mergedModule.hasAndroidManifest()) {
                AndroidManifestBlock manifest = mergedModule.getAndroidManifest();
                logMessage(MainActivity.rss.getString(R.string.sanitizing_manifest));
                int ID_requiredSplitTypes = 0x0101064e;
                int ID_splitTypes = 0x0101064f;

                AndroidManifestHelper.removeAttributeFromManifestById(manifest,
                        ID_requiredSplitTypes);
                AndroidManifestHelper.removeAttributeFromManifestById(manifest,
                        ID_splitTypes);
                AndroidManifestHelper.removeAttributeFromManifestByName(manifest,
                        AndroidManifest.NAME_splitTypes);

                AndroidManifestHelper.removeAttributeFromManifestByName(manifest,
                        AndroidManifest.NAME_requiredSplitTypes);
                AndroidManifestHelper.removeAttributeFromManifestByName(manifest,
                        AndroidManifest.NAME_splitTypes);
                AndroidManifestHelper.removeAttributeFromManifestAndApplication(manifest,
                        AndroidManifest.ID_extractNativeLibs
                );
                AndroidManifestHelper.removeAttributeFromManifestAndApplication(manifest,
                        AndroidManifest.ID_isSplitRequired
                );

                ResXmlElement application = manifest.getApplicationElement();
                List<ResXmlElement> splitMetaDataElements =
                        AndroidManifestHelper.listSplitRequired(application);
                boolean splits_removed = false;
                for (ResXmlElement meta : splitMetaDataElements) {
                    if (!splits_removed) {
                        boolean result = false;
                        ResXmlAttribute nameAttribute = meta.searchAttributeByResourceId(AndroidManifest.ID_name);
                        if (nameAttribute != null) {
                            if ("com.android.vending.splits".equals(nameAttribute.getValueAsString())) {
                                ResXmlAttribute valueAttribute = meta.searchAttributeByResourceId(
                                        AndroidManifest.ID_value);
                                if (valueAttribute == null) {
                                    valueAttribute = meta.searchAttributeByResourceId(
                                            AndroidManifest.ID_resource);
                                }
                                if (valueAttribute != null
                                        && valueAttribute.getValueType() == ValueType.REFERENCE) {
                                    if (mergedModule.hasTableBlock()) {
                                        TableBlock tableBlock = mergedModule.getTableBlock();
                                        ResourceEntry resourceEntry = tableBlock.getResource(valueAttribute.getData());
                                        if (resourceEntry != null) {
                                            ZipEntryMap zipEntryMap = mergedModule.getZipEntryMap();
                                            for (Entry entry : resourceEntry) {
                                                if (entry == null) {
                                                    continue;
                                                }
                                                ResValue resValue = entry.getResValue();
                                                if (resValue == null) {
                                                    continue;
                                                }
                                                String path = resValue.getValueAsString();
                                                logMessage(MainActivity.rss.getString(R.string.removed_table_entry) + " " + path);
                                                //Remove file entry
                                                zipEntryMap.remove(path);
                                                // It's not safe to destroy entry, resource id might be used in dex code.
                                                // Better replace it with boolean value.
                                                entry.setNull(true);
                                                SpecTypePair specTypePair = entry.getTypeBlock()
                                                        .getParentSpecTypePair();
                                                specTypePair.removeNullEntries(entry.getId());
                                            }
                                            result = true;
                                        }
                                    }
                                }
                            }
                        }
                        splits_removed = result;
                    }
                    logMessage("Removed-element : <" + meta.getName() + "> name=\""
                            + AndroidManifestHelper.getNamedValue(meta) + "\"");
                    application.remove(meta);
                }
                manifest.refresh();
            }
            logMessage(MainActivity.rss.getString(R.string.saving));

            File temp;
            if (signApk) {
                mergedModule.writeApk(temp = new File(cacheDir, "temp.apk"));
                logMessage(MainActivity.rss.getString(R.string.signing));
                boolean noPerm = MainActivity.doesNotHaveStoragePerm(context);
                File stupid = signedApk = new File(noPerm ? (cacheDir + File.separator + "stupid.apk") : FileUtils.getPath(out, context));
                try {
                    SignUtil.signDebugKey(context, temp, stupid);
                        if (noPerm) try(OutputStream os = FileUtils.getOutputStream(out, context)) {
                            FileUtils.copyFile(stupid, os);
                    }
                } catch (Exception e) {
                    SignUtil.signPseudoApkSigner(temp, context, out, e);
                }
                // Below no longer necessary
                    /*if (revanced) {
                       // The apk does not need to be signed to patch with ReVanced and it will make this already long crap take even more time
                    // but someone is probably going to try to install it before patching and complain
                    // and to avoid confusion/mistakes the sign apk option in the app should not be toggled off when revanced option is on
                     logMessage(MainActivity.rss.getString(R.string.fixing));
                        // Copying the contents of the zip to a new one works on most JRE implementations of java.util.zip but not on Android,
                        // the exact same problem happens in ReVanced.
                        try (ZipFileInput zfi = new ZipFileInput(temp);
                             com.j256.simplezip.ZipFileOutput zfo = new com.j256.simplezip.ZipFileOutput(signApk ?
                                     FileUtils.getOutputStream(temp = new File(cacheDir, "toSign.apk")) :
                                     FileUtils.getOutputStream(out, context))) {
                            ZipFileHeader header;
                            while ((header = zfi.readFileHeader()) != null) {
                                ZipFileHeader.Builder b = ZipFileHeader.builder();
                                b.setCompressedSize(header.getCompressedSize());
                                b.setCrc32(header.getCrc32());
                                b.setCompressionMethod(header.getCompressionMethod());
                                b.setFileName(header.getFileName());
                                b.setGeneralPurposeFlags(header.getGeneralPurposeFlags());
                                b.clearGeneralPurposeFlag(GeneralPurposeFlag.DATA_DESCRIPTOR);
                                b.setExtraFieldBytes(header.getExtraFieldBytes());
                                b.setLastModifiedDate(header.getLastModifiedDate());
                                b.setVersionNeeded(header.getVersionNeeded());
                                b.setUncompressedSize(header.getUncompressedSize());
                                zfo.writeFileHeader(b.build());
                                zfo.writeRawFileData(zfi.openFileDataInputStream(true));
                            }
                        }
                    }*/
            } else {
                mergedModule.writeApk(FileUtils.getOutputStream(out, context));
            }
        }
    }

    public static File signedApk;

}