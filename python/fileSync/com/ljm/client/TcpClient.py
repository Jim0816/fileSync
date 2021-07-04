import json
import threading
import time
from socket import *

from com.ljm.api.api import get_sync_network_state, set_sync_network_state, create_sync_network, join_sync_network, \
    close_sync_network, get_sync_network_info
from com.ljm.tools.FileUtil import get_all_files_desc
from com.ljm.tools.HttpRequest import request_get
from com.ljm.tools.MessageUtil import MessageUtil, deal_message
from com.ljm.tools.NetWorkUtil import get_mac_address

buffer_size = 1024 * 1024
client = {
    'machine_id': '',  # 本机唯一标识
    'server_ip': '127.0.0.1',  # 服务器ip
    'sync_path': 'C:\\Users\\yhkj\\Desktop\\test',  # 本机设置同步路径
    'tcp_obj': None,  # 本机客户端连接
    'join_net_id': '',  # 本机连接的同步网络id
    'client_state': 0,  # 本机状态 -1:已经断开 0:空闲  1：native_client -> server  2:server -> native_client
}


# 建立连接，申请账号 (加入或者创建网络时程序即执行)
def start_client(server_ip, port):
    tcp_client = socket(AF_INET, SOCK_STREAM)
    server_msg = (server_ip, port)
    # 连接服务器
    try:
        tcp_client.connect(server_msg)
    except error:
        print("client: connection failed, host --" + server_ip + "--The service is not turned on")
        return None
    else:
        # 已经与服务器建立tcp链接，准备向服务器申请自己的机器账号
        mac_addr = get_mac_address()
        # 申请账号报文请求
        client['tcp_obj'] = tcp_client
        send_message('300', {'mac_addr': mac_addr})
        wait_message_thread = threading.Thread(target=wait_message, args=())
        wait_message_thread.start()


# 等候服务端消息(一直等待)
def wait_message():
    while True:
        if client['tcp_obj'] is not None:
            socket_client = client['tcp_obj']
            # 只要连接还在，就可以接收消息
            msg = socket_client.recv(buffer_size)
            code, data = deal_message(msg)
            if code == "400":
                # 账号申请成功
                client['machine_id'] = data
                client['client_state'] = 0
                create_sync_net(data, "000000")
                print("创建者开始销毁网络")
                destroy_sync_net(data)
            elif code == "401":
                # 账号申请失败
                client['client_state'] = -1
                client['tcp_obj'] = None
                socket_client.close()
                break;


# 发送消息给服务端（主动）
def send_message(code, data):
    tcp_client = client['tcp_obj']
    if tcp_client is not None:
        message_obj = MessageUtil(code, client['server_ip'], data)
        tcp_client.send(message_obj.get_message().encode())
    else:
        print("与服务端连接无效!")


# 监听本地同步文件夹是否发生变化
def listening_dir_change(dir_path):
    pre_hash = get_all_files_desc(dir_path)
    time.sleep(1)
    while True:
        if client['client_state'] != -1:
            # 客户端只要还与服务端保持连接，就需要监听本地文件变化情况
            now_hash = get_all_files_desc(dir_path)
            if pre_hash != now_hash:
                pre_hash = now_hash
                # 发生变化, 询问自己加入的网络是否空闲
                if get_sync_network_state("1364122359899938817") == 0:
                    # 同步网络空闲
                    print("网络空闲，准备同步文件")
                    # 1.先抢占忙碌状态
                    if set_sync_network_state("1364122359899938817", 1) is True:
                        print("")
                        # 2.成功占据资源，准备同步文件

                        # 3.查询服务器中当前网络文件目录

                        # 4.本地共享文件目录与网络文件目录比对 -> 决定谁向谁传输数据

                        # 4.1 本地传输文件给共享网络

                        # 4.2 本地需要拉取网络文件目录资源

                    else:
                        print("网络忙碌，请稍等片刻")


# 创建网络
def create_sync_net(machine_id, join_pwd):
    try:
        result = create_sync_network(machine_id, join_pwd)
        if result['status'] == 500:
            # 创建新网络成功
            print("创建网络成功")
            # 告知服务器去更新网络列表
            send_message('301', {})
        elif result['status'] == 501:
            # 已经创建过网络
            print("您已经创建了网络，不需要再创建")
    except error:
        print("客户端创建网络异常!")


def destroy_sync_net(machine_id):
    # 1.先查询本机是否创建了网络
    net_info = get_sync_network_info(machine_id)
    if net_info:
        # 2.判断该网络是否处于忙碌状态
        if net_info['workState'] == 1:
            # 忙碌
            print("网络忙碌，是否需要强制销毁?")
        # 通知服务器去断开该网络所有连接
        send_message('302', {'net_id': net_info['id']})
        #result = close_sync_network(net_info['id'], net_info['createMachineId'])
        #print(result)
    else:
        print("本机没有创建可用网络")

# 加入网络
def join_sync_net(net_id, machine_id, join_pwd):
    join_sync_network(net_id, machine_id, join_pwd)


if __name__ == '__main__':
    start_client("127.0.0.1", 9999)
