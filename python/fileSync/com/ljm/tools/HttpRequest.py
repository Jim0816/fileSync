import json

import requests

base_url = "http://localhost:8888/file-sync"


# 返回字典对象
def request_get(api_path, params):
    url = base_url + api_path
    res = requests.get(url=url, params=params)
    return json.loads(res.text)


#params = {"macAddr": "c8:5b:76:44:66:22", "openIp": "117.43.162.233"}
#request_get("/file-sync/apply", params)
