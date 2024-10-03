package com.richard.s3fileupload.service;

import com.richard.s3fileupload.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {
    File saveFile(MultipartFile file, String name);
    List<File> getAllFiles();
}
