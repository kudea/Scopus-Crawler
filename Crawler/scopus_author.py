from bs4 import BeautifulSoup
from selenium import webdriver
import time
start = int(input('start from: '))
end = int(input('end: '))
urls_file = open('paper.txt', 'r')      # details for each paper
# author = open('authors.txt', 'a+')
# author = open('scp_author.txt', 'a')
author = open('scp_author1.txt', 'a', encoding='UTF-8')
papers = []

for i in urls_file.readlines():     # remove '\n'
    i = i.strip('\n')
    papers.append(i)

paper_num = len(papers)

driver = webdriver.Chrome()




for paper_id in range(start-1, end):
    #print(paper_id+1)
    # print(papers[paper_id])
    driver.get(papers[paper_id])
    page = driver.page_source
    soup = BeautifulSoup(page, 'lxml')
    author_lists = soup.find_all('span', {'title': '顯示作者詳情:需要訂閱'})
    corresponder = soup.find_all('p', {'class': 'corrAuthSect'})        # find corresponding author
    corresponder = corresponder[0].text.split(';')
    corresponder = corresponder[0].strip().replace('\n', '')

    author_order = 1

    for each_author in author_lists:
        author.write(str(paper_id + 1) + '$')
        author.write(str(author_order) + '$')
        author.write(each_author.text + '$')
        if each_author.text == corresponder:
            author.write(str(1))
        else:
            author.write(str(0))

        author_order += 1
        author.write(('\n'))



driver.close()
author.close()