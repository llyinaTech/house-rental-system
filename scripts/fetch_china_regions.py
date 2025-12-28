import json
import urllib.request
import os

# 配置
JSON_URL = 'https://raw.githubusercontent.com/modood/Administrative-divisions-of-China/master/dist/pca-code.json'
OUTPUT_FILE = '../backend/house-rental-backend/src/main/resources/sql/region_data_full.sql'

def fetch_data():
    print(f"Downloading data from {JSON_URL}...")
    try:
        with urllib.request.urlopen(JSON_URL) as response:
            data = json.loads(response.read().decode('utf-8'))
            return data
    except Exception as e:
        print(f"Error downloading data: {e}")
        return None

def generate_sql(data):
    if not data:
        return

    print("Generating SQL...")
    sql_lines = []
    sql_lines.append("TRUNCATE TABLE sys_region;")
    
    # 递归处理
    # Level 1: Province
    for prov in data:
        p_code = prov['code']
        p_name = prov['name']
        sql_lines.append(f"INSERT INTO sys_region (id, parent_id, name, level_type, sort_order) VALUES ({p_code}, 0, '{p_name}', 1, 0);")
        
        children = prov.get('children', [])
        for city in children:
            c_code = city['code']
            c_name = city['name']
            sql_lines.append(f"INSERT INTO sys_region (id, parent_id, name, level_type, sort_order) VALUES ({c_code}, {p_code}, '{c_name}', 2, 0);")
            
            districts = city.get('children', [])
            for dist in districts:
                d_code = dist['code']
                d_name = dist['name']
                sql_lines.append(f"INSERT INTO sys_region (id, parent_id, name, level_type, sort_order) VALUES ({d_code}, {c_code}, '{d_name}', 3, 0);")

    # 写入文件
    # 确保目录存在
    os.makedirs(os.path.dirname(OUTPUT_FILE), exist_ok=True)
    
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        f.write('\n'.join(sql_lines))
    
    print(f"Success! SQL file generated at: {os.path.abspath(OUTPUT_FILE)}")
    print(f"Total lines: {len(sql_lines)}")

if __name__ == '__main__':
    data = fetch_data()
    generate_sql(data)
