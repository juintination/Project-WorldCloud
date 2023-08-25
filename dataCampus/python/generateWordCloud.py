import warnings, sys, os, re, chardet, gensim
import datetime
import urllib.request as ur
from bs4 import BeautifulSoup as bs
from gensim.models import LdaModel, CoherenceModel
from konlpy.tag import Okt
import pyLDAvis.gensim
from wordcloud import WordCloud
# import numpy as np
# from PIL import Image

warnings.filterwarnings('ignore')


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
    if date == "":
        date = str(datetime.date.today())

    is_today, is_new_directory_exist = False, False
    if date == str(re.sub('-', '_', str(datetime.date.today()))):
        is_today = True

    new_input_directory = f'C:\\dataCampus\\inputdata\\{keyword}\\{detail_keyword}\\' + str(re.sub('-', '_', date))
    try:
        os.mkdir(new_input_directory)
        print(f"디렉터리 '{new_input_directory}'를 생성했습니다.")
    except FileExistsError:
        is_new_directory_exist = True
        print(f"'{new_input_directory}'는 이미 존재합니다.")
    except Exception as e:
        print(f"디렉터리 생성 중 오류 발생: {e}")

    if detail_keyword == "":
        detail_keyword = 'all'
        url = f'https://news.daum.net/breakingnews/{keyword}?regDate=' + str(re.sub('-', '', date))
    else:
        url = f'https://news.daum.net/breakingnews/{keyword}/{detail_keyword}?regDate=' + str(re.sub('-', '', date))

    newspapers = getNewsPapers(url)

    if not is_today and is_new_directory_exist:
        if len(os.listdir(r'C:\\dataCampus\\inputdata\\' + keyword + '\\' + detail_keyword + '\\' + str(re.sub('-', '_', date)))) == len(newspapers):
            print('종료 조건에 의해 프로그램을 종료합니다.')
            return
        
    os.chdir(r'C:\\dataCampus\\inputdata\\' + keyword + '\\' + detail_keyword + '\\' + str(re.sub('-', '_', date)))
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

    file_list = os.listdir(
        r'C:\\dataCampus\\inputdata\\' + keyword + '\\' + detail_keyword + '\\' + str(re.sub('-', '_', date)))

    datas = []
    for file in file_list:
        file_path = os.path.join(f'C:\\dataCampus\\inputdata\\{keyword}\\{detail_keyword}\\',
                                 str(re.sub('-', '_', date)), file)

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
    vis1 = pyLDAvis.gensim.prepare(model, corpus, id2word)

    new_output_directory = f'C:\\dataCampus\\outputdata\\{keyword}\\{detail_keyword}\\' + str(re.sub('-', '_', date))
    try:
        os.mkdir(new_output_directory)
        print(f"디렉터리 '{new_output_directory}'를 생성했습니다.")
    except FileExistsError:
        is_new_directory_exist = True
        print(f"'{new_output_directory}'는 이미 존재합니다.")
    except Exception as e:
        print(f"디렉터리 생성 중 오류 발생: {e}")

    pyLDAvis.save_html(vis1, f'C:\\dataCampus\\outputdata\\{keyword}\\{detail_keyword}\\' + str(re.sub('-', '_', date)) + '\\vis.html')

    topics = model.show_topics(num_topics=max_num, num_words=10, formatted=False)
    topic_keywords = [[word[0] for word in topic[1]] for topic in topics]

    text = ""
    for keywords in topic_keywords:
        text += " ".join(keywords)

    print('word cloud 생성중')
    wordcloud = WordCloud(
        font_path=r'C:\\dataCampus\\font\\NanumGothicEco.ttf',
        background_color='white',
        width=1000,
        height=1000,
        max_words=50,
        max_font_size=300
    ).generate(text)

    os.chdir(r'C:\\dataCampus\\outputdata\\' + keyword + '\\' + detail_keyword + '\\' + str(re.sub('-', '_', date)))
    wordcloud.to_file('wordCloud.png')

    print('word cloud 생성 완료')


if __name__ == '__main__':
    if len(sys.argv) != 4:
        print("Usage: python wordCloud.py <keyword> <detail_keyword> <date>")
        sys.exit(1)
    keyword = sys.argv[1]
    detail_keyword = sys.argv[2]
    date = sys.argv[3]
    getWordCloud(keyword, detail_keyword, date)
