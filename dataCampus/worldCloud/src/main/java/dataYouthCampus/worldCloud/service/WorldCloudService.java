package dataYouthCampus.worldCloud.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.MalformedURLException;

public interface WorldCloudService {

    Integer generateWordCloud(String keyword, String detailKeyword, String date) throws IOException;

    byte[] getImageBytes(String imagePath);

    ResponseEntity<Resource> getImageFile(String imagePath);

    ResponseEntity<Resource> getVisFile(String htmlFilePath) throws MalformedURLException;

}
