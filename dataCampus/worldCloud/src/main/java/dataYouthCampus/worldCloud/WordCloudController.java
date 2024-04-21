package dataYouthCampus.worldCloud;

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
import java.util.Arrays;
import java.util.HashMap;
import java.time.LocalDate;

@Controller
public class WordCloudController {

    private final Map<String, List<String>> detailKeywordsMap = new HashMap<>();
    private final Map<String, String> keywordAddressMap = new HashMap<>();
    private final Map<String, String> detailKeywordAddressMap = new HashMap<>();
    private String getKeyword, getDetailKeyword, getDate;

    public WordCloudController() {
        detailKeywordsMap.put("Social", Arrays.asList("전체기사", "사건/사고", "인물", "교육", "미디어", "여성", "복지", "사회일반", "노동", "환경", "전국",
                "서울", "수도권", "강원", "충청", "경상", "전라", "제주", "지역일반"));
        detailKeywordsMap.put("Political", Arrays.asList("전체기사", "행정/지자체", "국회/정당", "북한", "정치일반", "외교/국방", "청와대"));
        detailKeywordsMap.put("Economic", Arrays.asList("전체기사", "금융", "기업산업", "취업직장인", "경제일반", "자동차", "주식", "시황분석", "공시", "해외증시",
                "채권선물", "외환", "주식일반", "부동산", "생활경제", "국제경제"));
        detailKeywordsMap.put("International", Arrays.asList("전체기사", "아시아대양주", "미국/아메리카", "유럽", "중동/아프리카", "국제일반", "해외화제", "일본", "중국"));
        detailKeywordsMap.put("Cultural", Arrays.asList("전체기사", "건강", "생활정보", "공연/전시", "책", "여행레저", "문화생활일반", "날씨", "뷰티/패션", "가정/육아",
                "음식/맛집", "종교"));
        detailKeywordsMap.put("Entertainment", Arrays.asList("전체기사", "방송", "가요음악", "영화", "해외연예", "드라마", "예능", "연예가화제", "연예일반"));
        detailKeywordsMap.put("Sports", Arrays.asList("전체기사", "해외야구", "축구", "야구", "농구", "스포츠일반", "e스포츠", "골프", "해외축구", "배구"));
        detailKeywordsMap.put("IT", Arrays.asList("전체기사", "인터넷", "과학", "게임", "휴대폰통신", "IT기기", "통신모바일", "소프트웨어", "Tech일반"));

        keywordAddressMap.put("Social", "society");
        keywordAddressMap.put("Political", "politics");
        keywordAddressMap.put("Economic", "economic");
        keywordAddressMap.put("International", "foreign");
        keywordAddressMap.put("Cultural", "culture");
        keywordAddressMap.put("Entertainment", "entertain");
        keywordAddressMap.put("Sports", "sports");
        keywordAddressMap.put("IT", "digital");

        detailKeywordAddressMap.put("전체기사", "all");

        // society
        detailKeywordAddressMap.put("사건/사고", "affair");
        detailKeywordAddressMap.put("인물", "people");
        detailKeywordAddressMap.put("교육", "education");
        detailKeywordAddressMap.put("미디어", "media");
        detailKeywordAddressMap.put("여성", "women");
        detailKeywordAddressMap.put("복지", "welfard");
        detailKeywordAddressMap.put("사회일반", "others");
        detailKeywordAddressMap.put("노동", "labor");
        detailKeywordAddressMap.put("환경", "environment");
        detailKeywordAddressMap.put("전국", "nation");
        detailKeywordAddressMap.put("서울", "nation\\seoul");
        detailKeywordAddressMap.put("수도권", "nation\\metro");
        detailKeywordAddressMap.put("강원", "nation\\gangwon");
        detailKeywordAddressMap.put("충청", "nation\\chuncheong");
        detailKeywordAddressMap.put("경상", "nation\\gyeongsang");
        detailKeywordAddressMap.put("전라", "nation\\jeolla");
        detailKeywordAddressMap.put("제주", "nation\\jeju");
        detailKeywordAddressMap.put("지역일반", "nation\\others");

        // politics
        detailKeywordAddressMap.put("행정/지자체", "administration");
        detailKeywordAddressMap.put("국회/정당", "assembly");
        detailKeywordAddressMap.put("북한", "north");
        detailKeywordAddressMap.put("정치일반", "others");
        detailKeywordAddressMap.put("외교/국방", "dipdefen");
        detailKeywordAddressMap.put("청와대", "president");

        // economic
        detailKeywordAddressMap.put("금융", "finance");
        detailKeywordAddressMap.put("기업산업", "industry");
        detailKeywordAddressMap.put("취업직장인", "employ");
        detailKeywordAddressMap.put("경제일반", "others");
        detailKeywordAddressMap.put("자동차", "autos");
        detailKeywordAddressMap.put("주식", "stock");
        detailKeywordAddressMap.put("시황분석", "stock\\market");
        detailKeywordAddressMap.put("공시", "stock\\publicnotice");
        detailKeywordAddressMap.put("해외증시", "stock\\world");
        detailKeywordAddressMap.put("채권선물", "stock\\bondsfutures");
        detailKeywordAddressMap.put("외환", "stock\\fx");
        detailKeywordAddressMap.put("주식일반", "stock\\others");
        detailKeywordAddressMap.put("부동산", "estate");
        detailKeywordAddressMap.put("생활경제", "consumer");
        detailKeywordAddressMap.put("국제경제", "world");

        // foreign
        detailKeywordAddressMap.put("아시아/대양주", "asia");
        detailKeywordAddressMap.put("미국/아메리카", "america");
        detailKeywordAddressMap.put("유럽", "europe");
        detailKeywordAddressMap.put("중동/아프리카", "africa");
        detailKeywordAddressMap.put("국제일반", "others");
        detailKeywordAddressMap.put("영어뉴스", "englishnews");
        detailKeywordAddressMap.put("해외화제", "topic");
        detailKeywordAddressMap.put("일반", "japan");
        detailKeywordAddressMap.put("중국", "china");

        // culture
        detailKeywordAddressMap.put("건강", "health");
        detailKeywordAddressMap.put("생활정보", "life");
        detailKeywordAddressMap.put("공연/전시", "art");
        detailKeywordAddressMap.put("책", "book");
        detailKeywordAddressMap.put("여행레져", "leisure");
        detailKeywordAddressMap.put("문화생활일반", "others");
        detailKeywordAddressMap.put("날씨", "weather");
        detailKeywordAddressMap.put("뷰티/패션", "fashion");
        detailKeywordAddressMap.put("가정/육아", "home");
        detailKeywordAddressMap.put("음식/맛집", "food");
        detailKeywordAddressMap.put("종교", "religion");

        // entertain
        detailKeywordAddressMap.put("방송", "broadcast");
        detailKeywordAddressMap.put("가요음악", "music");
        detailKeywordAddressMap.put("영화", "movie");
        detailKeywordAddressMap.put("해외연애", "abroad");
        detailKeywordAddressMap.put("드라마", "drama");
        detailKeywordAddressMap.put("예능", "variety");
        detailKeywordAddressMap.put("연예가화제", "topic");
        detailKeywordAddressMap.put("연예일반", "others");

        // sports
        detailKeywordAddressMap.put("해외야구", "worldbaseball");
        detailKeywordAddressMap.put("축구", "soccer");
        detailKeywordAddressMap.put("야구", "baseball");
        detailKeywordAddressMap.put("농구", "basketball");
        detailKeywordAddressMap.put("스포츠일반", "others");
        detailKeywordAddressMap.put("e스포츠", "esports");
        detailKeywordAddressMap.put("골프", "golf");
        detailKeywordAddressMap.put("해외축구", "worldsoccer");
        detailKeywordAddressMap.put("배구", "volleyball");

        // digital
        detailKeywordAddressMap.put("인터넷", "internet");
        detailKeywordAddressMap.put("과학", "science");
        detailKeywordAddressMap.put("게임", "game");
        detailKeywordAddressMap.put("휴대폰통신", "it");
        detailKeywordAddressMap.put("IT기기", "device");
        detailKeywordAddressMap.put("통신모바일", "moblie");
        detailKeywordAddressMap.put("소프트웨어", "software");
        detailKeywordAddressMap.put("Tech일반", "others");
    }

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
        String command = "python ..\\\\python\\\\generateWordCloud.py " + getKeyword + " " + getDetailKeyword + " " + getDate;
        Process process = Runtime.getRuntime().exec(command);
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