import time

from com.ljm.api.api import get_sync_network_state, set_sync_network_state
from com.ljm.tools.FileUtil import get_all_files_desc
from com.ljm.tools.HttpRequest import request_get


if __name__ == '__main__':
    """
    client['pre_file_info_hash'] = get_all_files_desc(client['sync_path'])
    # 1.加入网络  加入成功则建立连接
    if join_sync_network("net_id", "machine_id", "join_pwd") is True:
        print("加入成功")
        tcp_client, machine_id = start_client()
        client['machine_id'] = machine_id
        client['tcp_obj'] = tcp_client
    else:
        print("加入失败")

    """
    #listening_dir_change()
