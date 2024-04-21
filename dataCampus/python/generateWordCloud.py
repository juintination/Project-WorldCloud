import warnings, sys, os, re, chardet, gensim
import datetime
import urllib.request as ur
from bs4 import BeautifulSoup as bs
from gensim.models import LdaModel, CoherenceModel
from konlpy.tag import Okt
import pyLDAvis.gensim
from wordcloud import WordCloud

warnings.filterwarnings('ignore')


def make_directory(path: str) -> bool:
    is_new_directory_exist = False
    try:
        os.mkdir(path)
        print(f"디렉터리 '{path}'를 생성했습니다.")
    except FileExistsError:
        is_new_directory_exist = True
        print(f"'{path}'는 이미 존재합니다.")
    except Exception as e:
        print(f"{path} 디렉터리 생성 중 오류 발생: {e}")
    return is_new_directory_exist


def init_dictionary():
    directories = ['..\\inputdata\\', '..\\outputdata\\']
    [make_directory(directory) for directory in directories]


def getNewsPapers(url):
    html = ur.urlopen(url)
    soup = bs(html.read(), 'html.parser')
    page_news = [url]

    for a in soup.find('div', {'class': 'paging_news'}).find_all('a'):
        page_news.append(str(re.sub('®', '&reg', 'https://news.daum.net' + a.get('href'))))

    soups = []
    for url in page_news:
        html = ur.urlopen(url)
        soups.append(bs(html.read(), 'html.parser'))

    aside_g_aside_series = []
    for soup in soups:
        for strong in soup.find('div', {"class": "aside_g aside_series"}).find_all('strong', {"class": "tit_thumb"}):
            aside_g_aside_series.append(strong.find_all('a')[0].get('href'))
    aside_g_aside_series = list(set(aside_g_aside_series))

    newspapers = []
    for soup in soups:
        for newspaper in soup.find_all('strong', {"class": "tit_thumb"}):
            if newspaper.find('a').get('href') not in aside_g_aside_series:
                newspapers.append(newspaper)
    return newspapers


def getWordCloud(keyword, detail_keyword, date):
    is_today = False
    if date == str(datetime.date.today()):
        is_today = True

    input_keyword_directory = f'..\\inputdata\\{keyword}\\'
    make_directory(input_keyword_directory)

    input_detail_keyword_directory = input_keyword_directory + detail_keyword + '\\'
    make_directory(input_detail_keyword_directory)

    new_input_directory = input_detail_keyword_directory + str(re.sub('-', '_', date))
    is_new_directory_exist = make_directory(new_input_directory)

    if detail_keyword == 'all':
        url = f'https://news.daum.net/breakingnews/{keyword}?regDate=' + str(re.sub('-', '', date))
    else:
        url = f'https://news.daum.net/breakingnews/{keyword}/{detail_keyword}?regDate=' + str(re.sub('-', '', date))

    newspapers = getNewsPapers(url)

    if not is_today and is_new_directory_exist:
        if len(os.listdir(new_input_directory)) == len(newspapers):
            print('종료 조건에 의해 프로그램을 종료합니다.')
            return
        
    os.chdir(new_input_directory)
    for idx, newspaper in enumerate(newspapers):
        f = open(str(re.sub('-', '_', date)) + f'_{idx}_###.txt', 'w')
        try:
            title_of_news = newspaper.text.lstrip('\n').rstrip('\n').split('\n')[0]
            f.write('###\n\n' + title_of_news + '\n\n')
            f.write(newspaper.find('a').get('href') + '\n')
            soup2 = bs(ur.urlopen(newspaper.find('a').get('href')).read(), 'html.parser')
            for article_view in soup2.find('div', {"class": "article_view"}).find_all('p'):
                f.write(article_view.text + '\n\n')
        except:
            pass
        f.close()

    os.chdir(base_directory)
    file_list = os.listdir(new_input_directory)

    datas = []
    for file in file_list:
        file_path = os.path.join(new_input_directory, file)

        with open(file_path, 'rb') as f:
            raw_data = f.read()
            detected_encoding = chardet.detect(raw_data)['encoding']

            try:
                decoded_data = raw_data.decode(detected_encoding)
            except UnicodeDecodeError:
                print(f"Error decoding file {file} with encoding {detected_encoding}")
                continue

            lines = decoded_data.splitlines()
            if '###' in file:
                tmp = '\n'.join(lines).split('###')
            else:
                tmp = '\n'.join(lines)
            tmp = [t for t in tmp if len(t) > 30]
            datas.extend(tmp)

    regex = []
    for data in datas:
        text = re.sub('[^0-9ㄱ-힣]', ' ', str(data))
        text = re.sub(' +', ' ', text)
        regex.append(text)

    okt = Okt()
    nouns = []
    for i, noun in enumerate(regex):
        nouns.append(okt.nouns(noun))

    fil_nouns = nouns.copy()
    for idx, fil_noun in enumerate(fil_nouns):
        for i, fn in enumerate(fil_noun):
            if len(fn) < 2:
                fil_nouns[idx].pop(i)

    texts = fil_nouns.copy()
    id2word = gensim.corpora.Dictionary(texts)
    corpus = [id2word.doc2bow(text) for text in texts]

    start, end = 15, 20
    coherence_values = []
    for i in range(start, end + 1):
        try:
            model = LdaModel(
                corpus=corpus,
                id2word=id2word,
                num_topics=i,
                random_state=5000
            )
            coherence_model = CoherenceModel(model=model, texts=texts, dictionary=id2word, coherence='c_v')
            coherence_score = coherence_model.get_coherence()
            coherence_values.append(coherence_score)
            print(f"Number of topics: {i}, Coherence Score: {coherence_score}")
        except Exception as e:
            print(f"Error creating model for {i} topics: {e}")
    max_coherence_value, max_num = max(coherence_values), coherence_values.index(max(coherence_values)) + start

    model = LdaModel(
        corpus=corpus,
        id2word=id2word,
        num_topics=max_num,
        random_state=5000
    )

    vis1 = pyLDAvis.gensim.prepare(model, corpus, id2word, mds='mmds')

    output_keyword_directory = f'..\\outputdata\\{keyword}\\'
    make_directory(output_keyword_directory)

    output_detail_keyword_directory = output_keyword_directory + detail_keyword + '\\'
    make_directory(output_detail_keyword_directory)

    new_output_directory = output_detail_keyword_directory + str(re.sub('-', '_', date))
    make_directory(new_output_directory)

    pyLDAvis.save_html(vis1, new_output_directory + '\\vis.html')

    topics = model.show_topics(num_topics=max_num, num_words=10, formatted=False)
    topic_keywords = [[word[0] for word in topic[1]] for topic in topics]

    text = ""
    for keywords in topic_keywords:
        text += " ".join(keywords)

    print('워드클라우드 생성중...')
    os.chdir(base_directory)
    font_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..\\font\\NanumGothicEco.ttf')
    wordcloud = WordCloud(
        font_path=font_path,
        background_color='white',
        width=1000,
        height=1000,
        max_words=50,
        max_font_size=300
    ).generate(text)

    os.chdir(new_output_directory)
    wordcloud.to_file('wordCloud.png')

    print('워드클라우드 생성 완료!')


if __name__ == '__main__':
    if len(sys.argv) != 4:
        print("Usage: python generateWorldCloud.py <keyword> <detail_keyword> <date>")
        sys.exit(1)
    init_dictionary()
    base_directory = os.getcwd()
    keyword = sys.argv[1]
    detail_keyword = sys.argv[2]
    date = sys.argv[3]
    getWordCloud(keyword, detail_keyword, date)
