# -*- coding: utf-8 -*-
import json
import threading
from socket import *
import time
from com.ljm.api.api import apply_machine_id, update_sync_network_list
from com.ljm.tools.MessageUtil import MessageUtil, deal_message
from com.ljm.tools.NetWorkUtil import stop_thread

buffer_size = 1024 * 1024
client_pool = []
network_list = []


# 为客户端申请唯一标识
def apply_id(socket_client, mac_addr, open_ip):
    try:
        client_id = apply_machine_id(mac_addr, open_ip)
        send_message(socket_client, '400', open_ip, {'machine_id': client_id})
        return True, client_id
    except error:
        # 账号没有申请成功 -> 放弃当前连接
        send_message(socket_client, '401', open_ip, {'machine_id': ''})
        time.sleep(5)
        socket_client.close()
        return False, ""


# 等候客户端端消息(一直等待)
def wait_message(socket_client, client_ip, client_id):
    global network_list
    try:
        while True:
            print("========================  与客户端：" + client_ip + "通信  ========================")
            msg = socket_client.recv(buffer_size)
            code, data = deal_message(msg)
            if code == "300":
                # 申请账号
                result = apply_id(socket_client, data, client_ip)
                if result is False:
                    break;  # 自然退出当前子线程
            elif code == "301":
                # 请求更新网络列表
                network_list = update_sync_network_list()
            elif code == "302":
                # 请求关闭该网络下所有连接
                close_net_all_thread(data)
            else:
                print("当前接收数据不是信息报文")
    except error:
        print("========================  客户端：" + client_ip + "已经断开连接  ========================")
        # 从连接池中清理当前客户端
        socket_client.close()
        remove_client(client_id)


# 从连接池中清理当前客户端
def remove_client(client_id):
    global client_pool
    for index, client_obj in enumerate(client_pool):
        if client_obj['client_id'] == client_id:
            del client_pool[index]
            return True
    return False

def close_net_all_thread(net_id):
    global client_pool
    for index, client_obj in enumerate(client_pool):
        if client_obj['net_id'] == net_id:
            stop_thread(client_obj['client_session_thread'])
            del client_pool[index]
            return True
    return False

# 发送消息给客户端（主动）
def send_message(socket_client, code, receiver_ip, data):
    message_obj = MessageUtil(code, receiver_ip, data)
    socket_client.send(message_obj.get_message().encode())


def start_server():
    global network_list
    # 建立一个套接字， AF_INET 表示遵从IPv4协议，SOCK_STREAM（流） 表示使用的是tcp协议传输
    # 若使用UDP协议传输， 则使用SOCK_DGRAM(数据报）
    server = socket(AF_INET, SOCK_STREAM)
    # 重复使用绑定的信息
    # 此处若服务端成为tcp四次挥手的第一次，那么服务端最后将等待 2MSL 的时间来接受客户端可能发来的ack
    # 在这段时间内，若服务器想重复执行，之前被占用的端口等服务不被释放，导致服务器不能被执行
    # 此处加上这句话则解决了这个问题
    server.setsockopt(SOL_SOCKET, SO_REUSEADDR, 1)
    # 绑定本地端口
    msg_server = ("", 9999)
    server.bind(msg_server)
    # 开始监听
    # 此处的listen中的值表示处于半连接和以连接状态的client总和
    server.listen(12)
    print("server: The service is ready and waiting for the customer to connect")
    # 服务器开启时需要装入所有网络信息
    network_list = update_sync_network_list()
    try:
        while True:
            # 与客户端建立连接
            # socket_client表示为这个客户端创建出了包含tcp三次握手信息的新的套接字
            # msg_addr 包含这个客户端的信息
            socket_client, msg_address = server.accept()
            # 先申请账号，申请成功才能保持连接
            msg = socket_client.recv(buffer_size)
            code, data = deal_message(msg)
            if code == "300":
                # 申请账号
                result, client_id = apply_id(socket_client, data, msg_address[0])
                if result:
                    # 申请成功
                    client_session_thread = threading.Thread(target=wait_message,args=(socket_client, msg_address[0], client_id))
                    client_session_thread.start()
                    # 申请账号成功,与该客户端建立长连接 -> 开启会话
                    # 保存会话对象
                    client_session = {
                        'client_id': client_id,
                        'client_tcp': socket_client,
                        'client_session_thread': client_session_thread,
                        'net_id': '1364495953763098625',  # 此刻刚打开客户端，默认加入网络号为-1，表示当前没有加入任何网络
                    }
                    client_pool.append(client_session)
                else:
                    print("========================  客户端：" + msg_address[0] + "申请账号失败  ========================")
                    socket_client.close()
    except error:
        print("服务端: 客户端建立连接异常!")


# 服务端监听各个网络同步文件夹是否发生变化
def listening_dir_change(dir_path):
    pre_hash = get_all_files_desc(dir_path)
    time.sleep(1)
    while True:
        if client['client_state'] != -1:
            # 客户端只要还与服务端保持连接，就需要监听本地文件变化情况
            now_hash = get_all_files_desc(dir_path)
            if pre_hash != now_hash:
                pre_hash = now_hash


if __name__ == '__main__':
    start_server()
    """
    if sys.getdefaultencoding() != 'utf-8':
        reload(sys)
    sys.setdefaultencoding('utf-8')
    server_thread = threading.Thread(target=start_server, args=())
    server_thread.start()
    """

    # reform_thread = threading.Thread(target=reform_machine, args=())
    # reform_thread.start()
