# 本工具用于封装报文
import datetime
import json

from com.ljm.tools.NetWorkUtil import get_open_ip

# 3开头表示client请求   4开头表示server请求
MESSAGE_DICT = {
    '300': '客户端向服务端申请账号',
    '301': '客户端通知服务器销毁它创建的同步网络',
    '302': '客户端通知服务器断开该网络下所有客户端连接',
    '303': '',
    '304': '',
    '400': '服务端告知客户端申请账号成功',
    '401': '服务端告知客户端申请账号失败',
}

# 传入socket直接接收的msg
def deal_message(msg):
    msg_obj = json.loads(msg)
    if msg_obj['code'] == "300":
        print(msg_obj)
        return "300",msg_obj['data']

    elif msg_obj['code'] == "301":
        print(msg_obj)
        return "301", msg_obj

    elif msg_obj['code'] == "302":
        print(msg_obj)
        return "302", msg_obj['data']['net_id']

    elif msg_obj['code'] == "400":
        print(msg_obj)
        return "400", msg_obj['data']['machine_id']

    elif msg_obj['code'] == "401":
        print(msg_obj)
        return "401", msg_obj['data']['machine_id']

    else:
        print("当前数据报不合法!!!")


class MessageUtil(object):
    # 报文格式
    message = {
        'code': '',
        'sender': '',
        'receiver': '',
        'date': '',
        'desc': '',
        'data': ''
    }

    # code 传字符串类型
    # data ： 字典类型
    def __init__(self, code, receiver_ip, data):
        self.message['code'] = code
        self.message['sender'] = get_open_ip()
        self.message['receiver'] = receiver_ip
        self.message['date'] = str(datetime.datetime.now()).split(".")[0]
        self.message['desc'] = MESSAGE_DICT[code]
        self.message['data'] = data

    # 返回json串
    def get_message(self):
        return json.dumps(self.message, ensure_ascii=False)
