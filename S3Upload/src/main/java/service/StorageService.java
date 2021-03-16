package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;



@Service
@ComponentScan("com.amazonaws.services.s3")
public class StorageService {

	
	@Value("${application.bucket.name}")
	private String bucketName;
	
	@Autowired
	private AmazonS3 s3Client;
	
	public String uploadFile(MultipartFile file) {
		
		File convertedFile = convertMultiPartFileTofile(file);
		String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
		
		s3Client.putObject(bucketName, fileName, convertedFile);
		convertedFile.delete();
		
		return "File uploaded : "+fileName;
		
	}
	
	public byte[] downloadFile(String fileName) {
		
		S3Object s3obj = s3Client.getObject(bucketName,fileName);
		S3ObjectInputStream inputStream = s3obj.getObjectContent();
		
		try {
			byte[] content = IOUtils.toByteArray(inputStream);
			return content;
		} catch(IOException e) {
			System.out.println("Caught in download File: "+e.getMessage());
		}
		
		return null;
	}
	
	
	public String deleteFile(String fileName) {
		s3Client.deleteObject(bucketName, fileName);
		
		return fileName+" removed...";
	}
	private File convertMultiPartFileTofile(MultipartFile file) {
		
		File convertedFile = new File(file.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convertedFile)){
			
			fos.write(file.getBytes());
			
		} catch(IOException e) {
			System.out.println("Error converting multipart to File "+e.getMessage());
		}
		
		return convertedFile;
		
	}
	
}
