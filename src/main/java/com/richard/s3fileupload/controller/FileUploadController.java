package com.richard.s3fileupload.controller;

import com.richard.s3fileupload.model.File;
import com.richard.s3fileupload.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileUploadController {

    private final IFileService iFileService;

    @PostMapping("/upload")
    public ResponseEntity<Object> saveFile(@RequestParam(required = false) MultipartFile file,
                                           @RequestParam(required = false) String name){
        if(file.isEmpty() || name.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File and Name Required");
        }
        File fileToSave = iFileService.saveFile(file, name);
        return new ResponseEntity<>(fileToSave,HttpStatus.CREATED);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<File>> getAllFiles (){
        List<File> files = iFileService.getAllFiles();
        return new ResponseEntity<>(files,HttpStatus.OK);
    }

}
