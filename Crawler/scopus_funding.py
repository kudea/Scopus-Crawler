import sys
from bs4 import BeautifulSoup
from selenium import webdriver

urls_file = open('paper.txt', 'r')      # details for each paper
funding = open('funding.txt', 'w')
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
    except:
           print(sys.exc_info())


driver.close()
funding.close()