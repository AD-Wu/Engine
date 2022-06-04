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

package com.x.loader.jar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * copy from spring-boot-loader
 * @author starBlues
 * @version 3.0.0
 */
public class CentralDirectoryParser {

    private static final int CENTRAL_DIRECTORY_HEADER_BASE_SIZE = 46;

    private final List<ICentralDirectoryVisitor> visitors = new ArrayList<>();

    <T extends ICentralDirectoryVisitor> T addVisitor(T visitor) {
        this.visitors.add(visitor);
        return visitor;
    }

    /**
     * Parse the source data, triggering {@link ICentralDirectoryVisitor visitors}.
     * @param data the source data
     * @param skipPrefixBytes if prefix bytes should be skipped
     * @return the actual archive data without any prefix bytes
     * @throws IOException on error
     */
    IRandomAccessData parse(IRandomAccessData data, boolean skipPrefixBytes) throws IOException {
        CentralDirectoryEndRecord endRecord = new CentralDirectoryEndRecord(data);
        if (skipPrefixBytes) {
            data = getArchiveData(endRecord, data);
        }
        IRandomAccessData centralDirectoryData = endRecord.getCentralDirectory(data);
        visitStart(endRecord, centralDirectoryData);
        parseEntries(endRecord, centralDirectoryData);
        visitEnd();
        return data;
    }

    private void parseEntries(CentralDirectoryEndRecord endRecord, IRandomAccessData centralDirectoryData)
            throws IOException {
        byte[] bytes = centralDirectoryData.read(0, centralDirectoryData.getSize());
        CentralDirectoryFileHeader fileHeader = new CentralDirectoryFileHeader();
        int dataOffset = 0;
        for (int i = 0; i < endRecord.getNumberOfRecords(); i++) {
            fileHeader.load(bytes, dataOffset, null, 0, null);
            visitFileHeader(dataOffset, fileHeader);
            dataOffset += CENTRAL_DIRECTORY_HEADER_BASE_SIZE + fileHeader.getName().length()
                    + fileHeader.getComment().length() + fileHeader.getExtra().length;
        }
    }

    private IRandomAccessData getArchiveData(CentralDirectoryEndRecord endRecord, IRandomAccessData data) {
        long offset = endRecord.getStartOfArchive(data);
        if (offset == 0) {
            return data;
        }
        return data.getSubsection(offset, data.getSize() - offset);
    }

    private void visitStart(CentralDirectoryEndRecord endRecord, IRandomAccessData centralDirectoryData) {
        for (ICentralDirectoryVisitor visitor : this.visitors) {
            visitor.visitStart(endRecord, centralDirectoryData);
        }
    }

    private void visitFileHeader(int dataOffset, CentralDirectoryFileHeader fileHeader) {
        for (ICentralDirectoryVisitor visitor : this.visitors) {
            visitor.visitFileHeader(fileHeader, dataOffset);
        }
    }

    private void visitEnd() {
        for (ICentralDirectoryVisitor visitor : this.visitors) {
            visitor.visitEnd();
        }
    }

}
