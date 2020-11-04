# -*- coding: utf-8 -*-
"""
Created on Sun Mar 17 22:27:58 2019

@author: Xuan Yeh
"""
import sys
from bs4 import BeautifulSoup
from selenium import webdriver
import time

urls_file = open('paper.txt', 'r')      # details for each paper
# author = open('authors.txt', 'a+')
# author = open('scp_author.txt', 'a')
author = open('scp_author1.txt', 'a', encoding='UTF-8')
funding = open('funding.txt', 'w', encoding='UTF-8')
author_keyword = open('keywords.txt', 'w', encoding='UTF-8')
papers = []

for i in urls_file.readlines():     # remove '\n'
    i = i.strip('\n')
    papers.append(i)

paper_num = len(papers)

driver = webdriver.Chrome()




for paper_id in range(paper_num):
    print(paper_id+1)
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
        
    author.close()
    sponsor_lists = soup.find('section', {'id': 'fundingDetails',})
    if sponsor_lists:       # if sponsors exist
        sponsor_lists = sponsor_lists.find('tbody')
        if sponsor_lists:
            sponsor_lists = sponsor_lists.find_all('tr')
            if sponsor_lists:
                sponsor_order = 1
                    # print(sponsor_lists)
                for each_sponsor in sponsor_lists:
                    each_sponsor_detail = each_sponsor.find_all('td')
                    funding.write(str(paper_id + 1) + '$')
                    funding.write(str(sponsor_order) + '$')
                        # print(each_sponsor_detail)
                    for details in range(len(each_sponsor_detail)):
                        if details == len(each_sponsor_detail) - 1:     # funding opportunities column
                            continue
                        if each_sponsor_detail[details].text:
                            funding.write(each_sponsor_detail[details].text + '$')
                        else:
                            funding.write('NULL' + '$')
                    sponsor_order += 1
                    funding.write('\n')
    funding.close()                
    keyword_lists = soup.find_all('span', {'class': 'badges',})
    keyword_count = 1
    if keyword_lists:       # if author keywords exist
        for each_keyword in keyword_lists:
            author_keyword.write(str(paper_id+1) + '$')
            author_keyword.write(str(keyword_count) + '$')
            author_keyword.write(each_keyword.text)
            author_keyword.write('\n')
            keyword_count += 1



driver.close()


author_keyword.close()
urls_file.close()