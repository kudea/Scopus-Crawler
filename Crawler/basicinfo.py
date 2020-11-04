# -*- coding: utf-8 -*-
"""
Created on Mon Apr 15 09:33:59 2019

@author: Xuan Yeh
"""

from selenium import webdriver
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support.ui import WebDriverWait as wait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from bs4 import BeautifulSoup as bs

urls_file = open('paper.txt', 'r') 
basic_info = open('keywords.txt', 'w', encoding='UTF-8')
papers = []

for i in urls_file.readlines():     # remove '\n'
    i = i.strip('\n')
    papers.append(i)

paper_num = len(papers)
driver = webdriver.Chrome()

start = int(input('start from: '))
end = int(input('end: '))


for paper_id in range(start-1, end):
    print(paper_id+1)
    # print(papers[paper_id])
    driver.get(papers[paper_id])
    page = driver.page_source
    soup = bs(page, 'lxml')
    title = soup.find('h2', {'class': 'h3'})
    basic_info.write(str(paper_id + 1) + '$')
    basic_info.write(title.text() + '$')
    
    
    
    page1 = driver.page_source
    soup1 = bs(page, 'lxml')
    journal_lists = soup1.find_all('span', {'id': 'journalInfo'})    

    journal_order = 1
    basic_info.write(str(journal_order) + '$')
    basic_info.write(journal_lists.text + '$')

    journal_order += 1
    basic_info.write(('\n'))
        
basic_info.close()
urls_file.close()