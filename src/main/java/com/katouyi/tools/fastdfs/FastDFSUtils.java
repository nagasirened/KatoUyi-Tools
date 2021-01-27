package com.katouyi.tools.fastdfs;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * author: ZGF
 * context : FastDFS 工具类
 */

@Slf4j
@Component
public class FastDFSUtils {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 上传附件到
     * @param file
     * @return
     * @throws IOException
     */
    public String upload(MultipartFile file) throws IOException {
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(),
                file.getSize(),
                file.getOriginalFilename(),
                null);
        return storePath.getFullPath();
    }

    public byte[] download(String groupName, String remoteFileName) throws IOException {
        return fastFileStorageClient.downloadFile(groupName, remoteFileName, new DownloadByteArray());
    }

    public void delete(String groupName, String remoteFileName) throws IOException {
        fastFileStorageClient.deleteFile(groupName, remoteFileName);
    }
}
