import sys
from bs4 import BeautifulSoup
from selenium import webdriver
urls_file = open('paper.txt', 'r')      # details for each paper
author_keyword = open('keywords.txt', 'w', encoding='UTF-8')
papers = []

for i in urls_file.readlines():     # remove '\n'
    i = i.strip('\n')
    papers.append(i)

paper_num = len(papers)

driver = webdriver.Chrome()


for paper_id in range(paper_num):
    try:
        print(paper_id+1)
        driver.get(papers[paper_id])
        page = driver.page_source
        soup = BeautifulSoup(page)
        keyword_lists = soup.find_all('span', {'class': 'badges',})
        keyword_count = 1

        if keyword_lists:       # if author keywords exist
            for each_keyword in keyword_lists:
                author_keyword.write(str(paper_id+1) + '$')
                author_keyword.write(str(keyword_count) + '$')
                author_keyword.write(each_keyword.text)
                author_keyword.write('\n')
                keyword_count += 1
    except:
        print(sys.exc_info())

driver.close()
author_keyword.close()
urls_file.close()