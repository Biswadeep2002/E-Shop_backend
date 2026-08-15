//package com.example.EmbarkXProject.Service.File;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@Service
//public class FileServiceImpl implements FileService{
//
//    @Override
//    public String uploadImage(String path, MultipartFile file) throws IOException {
//
//        //File names of current / original file
//        String originalFileName = file.getOriginalFilename();
//
//        //Generate an unique file name
//        String randomId = UUID.randomUUID().toString();
//        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
//        String filePath = path + File.separator + fileName;
//
//        //Check if path exist and create
//        File folder = new File(path);
//
//        if(!folder.exists())
//            folder.mkdir();
//
//        //Upload to the server
//        Files.copy(file.getInputStream(), Paths.get(filePath));
//
//        //Return file name
//        return fileName;
//    }
//}






package com.example.EmbarkXProject.Service.File;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Autowired
    private Cloudinary cloudinary;


//    @Override
//    public String uploadImage(String path, MultipartFile file) throws IOException {
//
//        //File names of current / original file
//        String originalFileName = file.getOriginalFilename();
//
//        //Generate an unique file name
//        String randomId = UUID.randomUUID().toString();
//        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
//        String filePath = path + File.separator + fileName;
//
//        //Check if path exist and create
//        File folder = new File(path);
//
//        if(!folder.exists())
//            folder.mkdir();
//
//        //Upload to the server
//        Files.copy(file.getInputStream(), Paths.get(filePath));
//
//        //Return file name
//        return fileName;
//    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename();

        String publicId = UUID.randomUUID().toString();

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", "products",
                "public_id", publicId
        );

        Map<?, ?> uploadResult =
                cloudinary.uploader().upload(file.getBytes(), options);

        return uploadResult.get("secure_url").toString();
    }
}

