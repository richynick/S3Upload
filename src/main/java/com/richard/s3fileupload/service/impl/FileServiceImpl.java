package com.richard.s3fileupload.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Builder;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.richard.s3fileupload.model.File;
import com.richard.s3fileupload.repository.FileRepository;
import com.richard.s3fileupload.service.IFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FileServiceImpl implements IFileService {

    private final FileRepository fileRepository;

    @Value("${aws.s3.access.key}")
    private String awsS3AccessKey;

    @Value("${aws.s3.secret.key}")
    private String awsS3SecretKey;

    @Override
    public File saveFile(MultipartFile file, String name) {

        String saveFileUrl = saveFileToAWSS3Bucket(file);
        File fileToSave = File.builder()
                .fileUrl(saveFileUrl)
                .fileName(name)
                .build();
        return fileRepository.save(fileToSave);
    }

    @Override
    public List<File> getAllFiles() {
        return fileRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    private String saveFileToAWSS3Bucket(MultipartFile file){
        try{
            String s3FileName = file.getOriginalFilename();
            BasicAWSCredentials credentials = new BasicAWSCredentials(awsS3AccessKey, awsS3SecretKey);
            AmazonS3 amazonS3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(Regions.US_EAST_1)
                    .build();
            InputStream inputStream =file.getInputStream();
            ObjectMetadata objectMetadata = new ObjectMetadata();

            objectMetadata.setContentType("*");
            String bucketName = "richard-file-upload";

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,s3FileName,inputStream,objectMetadata);
            amazonS3Client.putObject(putObjectRequest);
            return "http://" + bucketName + ".s3.amazonaws.com/" + s3FileName;

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
