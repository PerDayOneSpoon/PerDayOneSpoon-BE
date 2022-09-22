package com.sparta.perdayonespoon.util;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.perdayonespoon.domain.dto.S3Dto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class Scalr_Resize_S3Uploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;


    public S3Dto uploadImage(MultipartFile multipartFile) throws IOException {
        String fileName = UUID.randomUUID() + multipartFile.getOriginalFilename();
        String fileFormatName = Objects.requireNonNull(multipartFile.getContentType()).substring(multipartFile.getContentType().lastIndexOf("/") + 1);
        String directory = "spoon/" + fileName;   // spoon/ 은 버킷 내 디렉토리 이름

        MultipartFile newFile = resizeImage(multipartFile, fileName, fileFormatName);
        return uploadToS3(newFile,directory);
    }

    @Transactional
    public S3Dto uploadToS3(MultipartFile uploadFile,String fileName) throws IOException {

//        String fileName = UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름 , 중복저장을 피하기 위해 UUID로 랜덤이름 추가
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드

        removeNewFile(uploadFile);

        S3Dto s3Dto = S3Dto.builder()
                .fileName(fileName)
                .uploadImageUrl(uploadImageUrl)
                .build();

        return s3Dto;
    }

    // S3 에 업로드
    private String putS3(MultipartFile newFile, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(newFile.getSize());
        metadata.setContentType(newFile.getContentType());
        InputStream inputStream = newFile.getInputStream();
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream,metadata).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 생성된 로컬 파일 삭제 메소드
    private void removeNewFile(MultipartFile targetFile) throws IOException {
        File file = new File(targetFile.getOriginalFilename());
//        Runtime.getRuntime().exec("rm -r " + file);
        if (file.delete()) {
            log.info("파일이 삭제되었습니다.");
        } else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }

//    Scalr 라이브러리로 Cropping 및 Resizing
    private MultipartFile resizeImage(MultipartFile originalImage, String fileName, String fileFormatName) throws IOException {
//
//        Runtime.getRuntime().exec("chmod 777 " + fileName);
//        Runtime.getRuntime().exec("chmod 777 " + originalImage.getOriginalFilename());
        // 요청 받은 파일로 부터 BufferedImage 객체를 생성합니다.
        BufferedImage srcImg = ImageIO.read(originalImage.getInputStream());

        // 썸네일의 너비와 높이 입니다.
        int demandWidth = 550, demandHeight = 550;

//        // 원본 이미지의 너비와 높이 입니다.
//        int originWidth = srcImg.getWidth();
//        int originHeight = srcImg.getHeight();
//
//        // 원본 너비를 기준으로 하여 썸네일의 비율로 높이를 계산합니다.
//        int newWidth = originWidth;
//        int newHeight = (originWidth * demandHeight) / demandWidth;
//
//        // 계산된 높이가 원본보다 높다면 crop 이 안되므로
//        // 원본 높이를 기준으로 썸네일의 비율로 너비를 계산합니다.
//        if (newHeight > originHeight) {
//            newWidth = (originHeight * demandWidth) / demandHeight;
//            newHeight = originHeight;
//        }
//
//        // 계산된 크기로 원본이미지를 가운데에서 crop 합니다.
//        BufferedImage cropImg = Scalr.crop(srcImg, (originWidth - newWidth) / 2, (originHeight - newHeight) / 2, newWidth, newHeight);


        // crop 된 이미지로 썸네일을 생성합니다.
        BufferedImage destImg = Scalr.resize(srcImg, demandWidth, demandHeight);

        // 썸네일을 저장합니다.
        File resizedImage = new File("/tmp/"+fileName);
        resizedImage.setExecutable(true, false);
        resizedImage.setReadable(true, false);
        resizedImage.setWritable(true, false);
        Runtime.getRuntime().exec("chmod 777 " + "/tmp/"+fileName);
        ImageIO.write(destImg, fileFormatName.toUpperCase(), resizedImage);
        originalImage.transferTo(new File("/tmp/" + fileName));
//        originalImage.transferTo(resizedImage);
        return originalImage;
    }

    public void remove(String filename) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, filename);
        amazonS3Client.deleteObject(request);
    }
}
