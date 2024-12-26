package com.my.fl.startup.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class Base64MultipartFile implements MultipartFile {

    private final byte[] fileContent;
    private final String fileName;

    public Base64MultipartFile(byte[] fileContent, String fileName) {
        this.fileContent = fileContent;
        this.fileName = fileName;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        // Set to a default MIME type or detect it if possible
        return "application/octet-stream";
    }

    @Override
    public boolean isEmpty() {
        return fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return fileContent.length;
    }

    @Override
    public byte[] getBytes() {
        return fileContent;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException {
        throw new UnsupportedOperationException("This MultipartFile implementation does not support transferTo");
    }

    public static MultipartFile fromBase64(String base64, String fileName) {
        // Remove metadata if present in the Base64 string
        if (base64.contains(",")) {
            base64 = base64.split(",")[1];
        }

        // Decode the Base64 string
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        return new Base64MultipartFile(decodedBytes, fileName);
    }
}
