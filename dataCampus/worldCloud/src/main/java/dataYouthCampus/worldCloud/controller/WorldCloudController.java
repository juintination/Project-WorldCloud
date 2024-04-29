package dataYouthCampus.worldCloud.controller;

import dataYouthCampus.worldCloud.service.WorldCloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class WorldCloudController {

    private final WorldCloudService worldCloudService;

    private final Map<String, List<String>> detailKeywordsMap;
    private final Map<String, String> keywordAddressMap;
    private final Map<String, String> detailKeywordAddressMap;

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

    @PostMapping("/result")
    public String getResult(@RequestParam String keyword, @RequestParam String detailKeyword,
                                    @RequestParam String date, Model model) throws IOException {
        String getKeyword = keywordAddressMap.get(keyword);
        String getDetailKeyword = detailKeywordAddressMap.get(detailKeyword);
        String getDate = date;
        if (getDate.isEmpty()) {
            LocalDate today = LocalDate.now();
            getDate = today.toString();
        }

        Integer exitCode = worldCloudService.generateWordCloud(getKeyword, getDetailKeyword, getDate);
        if (exitCode == 0) {
            String imagePath = "../outputdata/" + getKeyword + "/" + getDetailKeyword + "/"
                    + getDate.replace("-", "_") + "/wordCloud.png";
            model.addAttribute("imagePath", imagePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=wordCloud.png");

            byte[] imageBytes = worldCloudService.getImageBytes(imagePath);
            if (imageBytes == null) {
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
    public ResponseEntity<byte[]> displayImage(@RequestParam String keyword, @RequestParam String detailKeyword, @RequestParam String date) {
        String imagePath = "/dataCampus/outputdata/" + keyword + "/" + detailKeyword + "/"
                + date.replace("-", "_") + "/wordCloud.png";

        byte[] imageBytes = worldCloudService.getImageBytes(imagePath);
        if (imageBytes == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/save")
    public ResponseEntity<Resource> downloadWordCloudImage(@RequestParam String imagePath) {
        return worldCloudService.getImageFile(imagePath);
    }

    @GetMapping("/getVisHTML")
    public ResponseEntity<Resource> getVisHTML(@RequestParam String keyword, @RequestParam String detailKeyword, @RequestParam String date) throws MalformedURLException {
        String htmlFilePath = "/dataCampus/outputdata/" + keyword + "/" + detailKeyword + "/"
                + date.replace("-", "_") + "/vis.html";
        return worldCloudService.getVisFile(htmlFilePath);
    }
}