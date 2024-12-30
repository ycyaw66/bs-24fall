#codeing=utf-8
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time
from bs4 import BeautifulSoup
import json
import sys
import os

def get_value_in_html(text):
    soup = BeautifulSoup(text, 'html.parser')
    items = soup.find_all('li', {'clstag': lambda x: x and 'ri_same_recommend' in x})
 
    result = []
    for item in items:
        pic_img = item.find('div', class_='pic').find('img', class_='img_k')['src']
        pic_img = f"https:{pic_img}"
        a_tag = item.find('div', class_='li_cen_bot').find('a')
        if a_tag is None:
            continue
        product_link = a_tag['href']
        price = a_tag.find('div', class_='commodity_info').find('span', class_='price')
        if price is not None:
            price = price.text.strip()
            price = price.replace('￥', '¥').strip()
        else:
            price = ''
        title = a_tag.find('div', class_='commodity_tit')
        if title is not None:
            title = title.text.strip()
        else:
            title = ''
 
        result.append({
            "picImg": pic_img,
            "productLink": product_link,
            'productPrice': price,
            'productTitle': title
        })
 
    return result

if __name__ == '__main__':
    keyword = sys.argv[1]
    sys.stdout = open(sys.stdout.fileno(), mode='w', encoding='utf-8')
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-dev-shm-usage')
    driver = webdriver.Chrome(options=chrome_options)

    try:
        url = f'https://re.jd.com/search?keyword={keyword}&enc=utf-8'
        driver.get(url)

        time.sleep(3)

        html = driver.page_source

        result_list = get_value_in_html(html)
        script_dir = os.path.dirname(os.path.abspath(__file__))
        print(json.dumps(result_list, ensure_ascii=False, indent=4))

    except Exception as e:
        print(f"请求异常: {e}")

    finally:
        driver.quit()
