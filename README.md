# Project-WorldCloud
Implementation of Word cloud generator using keywords obtained based on the optimal number of topics for news crawled using the LDA model

---

## 프로젝트 개요

- 원하는 키워드에 관련된 하루의 뉴스들의 주요 키워드를 추출하여 하나의 워드 클라우드로 만들어주는 웹 서비스
- LDA 모델을 이용하여 크롤링한 뉴스에 대한 최적의 토픽 개수를 기반으로 얻은 키워드를 이용한 워드 클라우드 생성기

---

## 참여 인원

Back-end : 권덕재(B989003, 스프링 프레임워크를 이용한 웹 개발 및 프론트엔드, 크롤링 보조)

Front-end : 이승호(B989037, html5, css3, javascript를 이용한 UI 개발 및 백엔드, 크롤링 보조)

Crawling : 김기현(B989009, python을 이용한 크롤링을 비롯한 전처리 및 백엔드, 프론트엔드 보조)

---

## 목차

- [사용 방법](#사용-방법)
- [실행 화면과 기능 설명](#실행-화면과-기능-설명)
- [UI 변경 및 추가된 기능 설명](#ui-변경-및-추가된-기능-설명)
- [워드 클라우드 생성 관련 알고리즘 설명](#워드-클라우드-생성-관련-알고리즘-설명)

---

## 사용 방법

- 스프링부트 2.7.15 버전으로 작성하였으며, JAVA 11과 Python 3.11이 필요합니다.
  - ex) `openjdk11`, `Python 3.11.4`
- 웹 크롤링을 위한 추가적인 파이썬 라이브러리 설치가 필요합니다.
  - `$ pip install -r requirements.txt`
- DB를 사용하지 않고 로컬 환경에 각종 데이터들이 저장되는 방식입니다.
  - 현재 파이썬 코드 실행과 관련된 동시성 문제가 있는 것으로 확인되며, 추후에 해결할 예정입니다.

---

## 실행 화면과 기능 설명

![image](https://github.com/juintination/Project-WorldCloud/assets/89019601/60f95cf0-cd35-44ec-9b59-93a8bb153c85)

Welcome page에서 시작하기 버튼을 누르면 다음과 같이 대주제를 선택할 수 있는 창으로 넘어가게 됩니다.

![image](https://github.com/juintination/Project-WorldCloud/assets/89019601/b47bfb5e-294b-4c18-bd21-0171b1f1d466)

대주제를 선택하게 되면 예시로 IT를 선택한 다음과 같이 소주제와 원하는 날짜를 선택할 수 있는 창으로 넘어가게 됩니다.

![image](https://github.com/juintination/Project-WorldCloud/assets/89019601/0933525f-556b-4091-81f9-6cf69835a1e9)

이후 워드 클라우드 생성 버튼을 누르면 generateWordCloud.py에 의해 워드 클라우드 및 pyLDAvis가 html 파일로 특정 디렉터리에 생성된 후에 해당 워드 클라우드를 보여줍니다.

![image](https://github.com/juintination/Project-WorldCloud/assets/89019601/75f92815-bbf6-4ce9-8e49-804286243710)

키워드 분석 결과 확인하러 가기 버튼을 누르면 토픽 별 키워드의 빈도수를 확인할 수 있는 pyLDAvis 파일을 볼 수 있습니다.

![image](https://github.com/juintination/Project-WorldCloud/assets/89019601/9f71817a-57f0-49ba-b8e8-280bee90594b)

뉴스 크롤링은 [다음뉴스](https://news.daum.net/breakingnews)의 세부 페이지에서 진행되는데 이 때 선택한 날짜에 올라온 뉴스가 1개도 없거나 예상치 못한 오류가 발생하면 다음과 같이 Error page로 이동하게 됩니다.

![image](https://github.com/juintination/Project-WorldCloud/assets/89019601/36e73ae5-af73-464e-9319-afd797230977)

---

## UI 변경 및 추가된 기능 설명

무료 부트스트랩 템플릿을 참고하여 해당 프로젝트의 UI를 수정하였습니다.

<img width="1544" alt="스크린샷 2023-09-24 오후 9 50 19" src="https://github.com/juintination/Project-WorldCloud/assets/89019601/f7e66cc2-de6b-4a5e-ad3b-fc9f5cbdeb85">

Welcome page의 맨 위의 화면입니다. 시작하기 버튼을 누르면 다음과 같이 대주제를 선택할 수 있는 창으로 넘어가게 됩니다.

<img width="1544" alt="스크린샷 2023-09-24 오후 9 50 33" src="https://github.com/juintination/Project-WorldCloud/assets/89019601/e4ee31f6-3a36-45e0-b009-5f14fa324204">

Welcome page의 중간 화면입니다. 해당 프로젝트의 간단한 설명을 적어놓았습니다.

<img width="1544" alt="스크린샷 2023-09-24 오후 9 50 43" src="https://github.com/juintination/Project-WorldCloud/assets/89019601/66369f08-54ec-44b0-9b60-406571c9a486">

Welcome page의 맨 아래 화면입니다. 해당 기능은 아직 구현하지 않았으며 추후에 기회가 생긴다면 수정할 예정입니다.

<img width="1544" alt="스크린샷 2023-09-24 오후 9 50 54" src="https://github.com/juintination/Project-WorldCloud/assets/89019601/50174bdd-cb1a-4b86-8834-fed7db5161b1">

대주제 선택 화면입니다. 기존의 UI와 다르게 2줄로 나열하였으며 훨씬 깔끔해진 것을 확인할 수 있습니다.

<img width="1544" alt="스크린샷 2023-09-24 오후 9 51 00" src="https://github.com/juintination/Project-WorldCloud/assets/89019601/b6045714-21c2-4469-b53d-7e90bf8dc688">

소주제 선택 화면입니다. 워드 클라우드 생성 버튼을 누르게 되면 잠시만 기다려주세요 옆에 3개의 서로 다른 작은 원이 움직입니다.

<img width="1544" alt="스크린샷 2023-09-24 오후 9 51 22" src="https://github.com/juintination/Project-WorldCloud/assets/89019601/8baf94f4-59de-433b-b05c-dc89dd4f8832">

마지막 결과 화면입니다. 전체 뉴스 기사 보러가기 버튼을 누르게 된다면 워드 클라우드 제작에 활용한 뉴스 기사들의 원문들을 확인할 수 있는 기능과 더 알아보고 싶은 키워드 등을 아래 검색창에 입력하고 오른쪽의 버튼을 누르게 된다면 바로 네이버 검색 결과를 보여주도록 기능을 추가하였습니다.

---

## 워드 클라우드 생성 관련 알고리즘 설명

- generateWordCloud.py는 매개변수로 \<keyword> \<detail_keyword> \<date>를 받습니다.
- 이 때 해당 뉴스들을 크롤링한 후 txt 파일로 변환하여 inputdata 디렉터리에 필요한 새로운 디렉터리를 생성합니다.
  - \<data>가 오늘 날짜가 아니며 생성한 디렉터리가 이미 존재한 경우 크롤링한 뉴스의 개수와 해당 디렉터리에 존재하는 txt 파일의 개수가 같다면 이미 outputdata 디렉터리에 워드 클라우드가 존재하며 새로운 뉴스가 작성되지 않은 상태이므로 generateWordCloud.py를 종료한 후에 이미 존재하는 워드 클라우드를 result.html에 표시하게 됩니다.
  - 위의 조건을 만족하지 않는 경우 새로운 워드 클라우드를 생성하기 위해 다음 과정을 진행합니다.
    - \<data>가 오늘 날짜인 경우 새로운 뉴스가 언제 올라올지 알 수 없으므로 매번 요청할 때마다 새로운 워드 클라우드를 생성합니다.
- 모든 뉴스가 txt 파일로 저장되었다면 한글을 제외하여 모두 제거한 후에 konlpy 라이브러리를 사용하여 모든 한글 키워드를 추출합니다.
- 이후 for문을 통해 LDA 모델을 생성해가며 최적의 토픽 개수를 찾기 위해 Coherence Score를 비교하여 가장 높은 점수를 받은 토픽 개수를 기반으로 추출된 키워드들로 만든 워드 클라우드와 pyLDAvis 파일을 outputdata 디렉터리에 저장합니다.

---

## 기타 사항

- 사용언어 및 개발환경 : <img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=for-the-badge&logo=IntelliJ IDEA&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=OpenJDK&logoColor=white"> <img src="https://img.shields.io/badge/python-3776AB?style=for-the-badge&logo=python&logoColor=white"> <img src ="https://img.shields.io/badge/HTML5-E34F26.svg?&style=for-the-badge&logo=HTML5&logoColor=white"/> <img src ="https://img.shields.io/badge/CSS3-1572B6.svg?&style=for-the-badge&logo=CSS3&logoColor=white"/> <img src ="https://img.shields.io/badge/JavaScriipt-F7DF1E.svg?&style=for-the-badge&logo=JavaScript&logoColor=black"/>

## 참고

- [명령어 설명 규칙](https://technet.tmaxsoft.com/upload/download/online/jeus/pver-20170202-000001/reference-book/jeusadmin-conventions.html)
- [UI 수정을 위해 참고한 무료 부트스트랩 템플릿](https://startbootstrap.com/theme/freelancer)
