package dataYouthCampus.worldCloud.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class WordCloudController {

    private final Map<String, List<String>> detailKeywordsMap;
    private final Map<String, String> keywordAddressMap;
    private final Map<String, String> detailKeywordAddressMap;
    private String getKeyword, getDetailKeyword, getDate;

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/details")
    public String detailsPage(@RequestParam String keyword, Model model) {
        model.addAttribute("selectedKeyword", keyword);
        model.addAttribute("detailKeywords", detailKeywordsMap.get(keyword));
        return "details";
    }

    @PostMapping("/generate")
    public String generateWordCloud(@RequestParam String keyword, @RequestParam String detailKeyword,
                                    @RequestParam String date, Model model) throws IOException {
        getKeyword = keywordAddressMap.get(keyword);
        getDetailKeyword = detailKeywordAddressMap.get(detailKeyword);
        getDate = date;
        if (getDate.isEmpty()) {
            LocalDate today = LocalDate.now();
            getDate = today.toString();
        }
        String pythonCommand = "python";
        String pythonScript = "..\\\\python\\\\generateWordCloud.py";
        String[] commandArgs = { pythonCommand, pythonScript, getKeyword, getDetailKeyword, getDate };
        Process process = new ProcessBuilder(commandArgs).start();
        int exitCode;
        try {
            exitCode = process.waitFor();
        } catch (InterruptedException e) {
            exitCode = -1;
        }
        if (exitCode == 0) {
            String imagePath = "../outputdata/" + getKeyword + "/" + getDetailKeyword + "/"
                    + getDate.replace("-", "_") + "/wordCloud.png";
            model.addAttribute("imagePath", imagePath);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wordCloud.png");
            byte[] imageBytes;
            try {
                imageBytes = Files.readAllBytes(Paths.get(imagePath));
            } catch (IOException e) {
                return "error";
            }
            ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
            model.addAttribute("imageResponseEntity", responseEntity);
            model.addAttribute("selectedKeyword", getKeyword);
            model.addAttribute("selectedDetailKeyword", getDetailKeyword);
            model.addAttribute("selectedDate", getDate);
        } else {
            return "error";
        }
        return "result";
    }

    @GetMapping("/displayImage")
    public ResponseEntity<byte[]> displayImage() {
        String imagePath = "/dataCampus/outputdata/" + getKeyword + "/" + getDetailKeyword + "/"
                + getDate.replace("-", "_") + "/wordCloud.png";
        byte[] imageBytes;
        try {
            imageBytes = Files.readAllBytes(Paths.get(imagePath));
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/save")
    public ResponseEntity<Resource> downloadWordCloudImage(@RequestParam String imagePath) {
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

    @GetMapping("/getVisHTML")
    public ResponseEntity<Resource> getVisHTML() throws MalformedURLException {
        String localHTMLFilePath = "/dataCampus/outputdata/" + getKeyword + "/" + getDetailKeyword + "/"
                + getDate.replace("-", "_") + "/vis.html";
        File localHTMLFile = new File(localHTMLFilePath);
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