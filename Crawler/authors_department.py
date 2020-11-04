import time
from bs4 import BeautifulSoup
from selenium import webdriver

urls_file = open('paper.txt', 'r')      # details for each paper
# author = open('authors.txt', 'a+')
# ori_author = open('department.txt', 'w')
author_and_department = open('final_authors.txt', 'w')

# ori_info = []
# for i in ori_author.readlines():
#     i = i.replace('\n', '')
#     ori_info.append(i)

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
    time.sleep(3)
    driver.get(papers[paper_id])
    page = driver.page_source
    soup = BeautifulSoup(page,'lxml')
    author_lists = soup.find_all('div', {'id': 'profileleftside'})

    author_order = 1

    department = []
    for i in author_lists:
        subsciprt = i.text.split('.')[-1]
        department.append(subsciprt)
    print(department)

    for i in department:
        i = i.split(',')
        for j in i:
            superscript = 'superscript_' + j
            department_lists = soup.find_all('li', {'id': superscript, })
            if department_lists:
                dep = department_lists[0].text
                if len(j) == 2:
                    dep = dep[3:]
                elif len(j) == 1:
                    dep = dep[2:]
                # print(dep)
            else:
                department_lists = soup.find_all('li', {'id': 'superscript_a', })
                dep = department_lists[0].text

            author_and_department.write(str(paper_id+1) + '$')
            author_and_department.write(str(author_order) + '$')
            author_and_department.write(dep + '\n')

        author_order += 1


driver.close()