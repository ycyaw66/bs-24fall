#codeing:utf-8
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
import time
from bs4 import BeautifulSoup
import json
import sys
import os

def get_value_in_html(text):
    soup = BeautifulSoup(text, 'html.parser')
    items = soup.find_all('li', class_='item-wrap')
    
    result = []
    for item in items:
        img_tag = item.find('div', class_='res-img').find('img')
        pic_img = f"{img_tag['src']}" if img_tag and 'src' in img_tag.attrs else ''
        
        a_tag = item.find('a', class_='sellPoint')
        product_link = f"{a_tag['href']}" if a_tag and 'href' in a_tag.attrs else ''
        
        price_tag = item.find('div', class_='price-box').find('span', class_='def-price')
        if price_tag:
            currency_tag = price_tag.find('i')
            currency = currency_tag.text if currency_tag else ''
            price = ''.join(price_tag.stripped_strings).replace(currency, '').strip()
            full_price = f"{currency}{price}"
            full_price = full_price.replace("到手价", "").strip()
        else:
            full_price = ''
        
        title_tag = item.find('div', class_='title-selling-point').find('a')
        title = title_tag.text.strip() if title_tag else ''
        
        result.append({
            "picImg": pic_img,
            "productLink": product_link,
            'productPrice': full_price,
            'productTitle': title
        })
    
    return result
 
 
if __name__ == '__main__':
    keyword = sys.argv[1]

    chrome_options = Options()
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--disable-gpu")
    driver = webdriver.Chrome(options=chrome_options)

    try:
        url = f'https://search.suning.com/{keyword}/'
        driver.get(url)

        time.sleep(3)

        for _ in range(1):
            driver.find_element(By.TAG_NAME, 'body').send_keys(Keys.END)
            time.sleep(5)

        html = driver.page_source

        result_list = get_value_in_html(html)
        script_dir = os.path.dirname(os.path.abspath(__file__))
        json_file_path = os.path.join(script_dir, "suning_search_result.json")
        with open(json_file_path, "w", encoding="utf-8") as file:
            json.dump(result_list, file, ensure_ascii=False, indent=4)

    except Exception as e:
        print(f"请求异常: {e}")

    finally:
        driver.quit()