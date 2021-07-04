# 创建同步网络(创建者本人默认加入网络)
# machine_id: 本机唯一标识
# join_pwd: 创建者设置的加入网络密码
from com.ljm.tools.HttpRequest import request_get

"""
@function 申请账号 (server申请)
@return 返回账号
"""


def apply_machine_id(mac_addr, open_ip):
    result = request_get("/apply", {"macAddr": mac_addr, "openIp": open_ip})
    return result['data']


"""
@function 创建网络 (client)
@desc 当前业务设置客户端只能创建一个网络
@return 返回创建结果
"""


def create_sync_network(machine_id, join_pwd):
    result = request_get("/create", {"machineId": machine_id, "joinPwd": join_pwd})
    return result['data']


"""
@function 更新网络列表 (server)
@desc 返回
@return 字典
"""


def update_sync_network_list():
    result = request_get("/net_list", {})
    return result['data']


"""
@function 加入网络 (client)
@desc 返回加入结果
@return True|False
"""


def join_sync_network(net_id, machine_id, join_pwd):
    result = request_get("/join", {"netId": net_id, "machineId": machine_id, "joinPwd": join_pwd})
    return result['success']


"""
@function 退出网络 (client)
@desc 返回退出结果
@return True|False
"""


def exit_sync_network(net_id, machine_id):
    result = request_get("/exit", {"netId": net_id, "machineId": machine_id})
    return result['success']


"""
@function 销毁当前网络 (仅创建者client)
@desc 
@return 
"""


def close_sync_network(net_id, machine_id):
    result = request_get("/destroy", {"netId": net_id, "machineId": machine_id})
    return result


"""
@function 查询网络状态
@desc 返回网络状态 0:空闲  1：忙碌
@return 
"""


def get_sync_network_info(machine_id):
    result = request_get("/net_info_2", {"machineId": machine_id})
    return result['data']


"""
@function 查询网络状态
@desc 返回网络状态 0:空闲  1：忙碌
@return 整型
"""


def get_sync_network_state(net_id):
    net_info = request_get("/net_info_1", {"netId": net_id})
    return net_info['data']['workState']


"""
@function 设置网络 (client)
@desc 返回设置结果
@return True|False
"""


def set_sync_network_state(net_id, state):
    result = request_get("/set", {"netId": net_id, "state": state})
    return result['success']


# 向同步网络传输文件
def push_file_data():
    print("向服务器传输文件")


# 拉取同步网络文件
def pull_file_data():
    print("同步服务器文件到本地")
