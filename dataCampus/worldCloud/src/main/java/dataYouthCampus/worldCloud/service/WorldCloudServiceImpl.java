package dataYouthCampus.worldCloud.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class WorldCloudServiceImpl implements WorldCloudService {

    @Override
    public Integer generateWordCloud(String keyword, String detailKeyword, String date) throws IOException {
        String pythonCommand = "python";
        String pythonScript = "..\\\\python\\\\generateWordCloud.py";
        String[] commandArgs = { pythonCommand, pythonScript, keyword, detailKeyword, date };
        Process process = new ProcessBuilder(commandArgs).start();
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            exitCode = -1;
        }
        return exitCode;
    }

    @Override
    public byte[] getImageBytes(String imagePath) {
        byte[] imageBytes;
        try {
            imageBytes = Files.readAllBytes(Paths.get(imagePath));
        } catch (IOException e) {
            return null;
        }
        return imageBytes;
    }

    @Override
    public ResponseEntity<Resource> getImageFile(String imagePath) {
        File file = new File(imagePath);
        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wordCloud.png");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @Override
    public ResponseEntity<Resource> getVisFile(String htmlFilePath) throws MalformedURLException {
        File localHTMLFile = new File(htmlFilePath);
        String absolutePath = localHTMLFile.getAbsolutePath();
        Resource resource = new UrlResource("file:" + absolutePath);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=file.html");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

}
