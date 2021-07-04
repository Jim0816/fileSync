# 获取当前文件夹下所有文件描述信息
import hashlib
import json
from os import listdir
import os


def get_files_desc(dir_path, file_path_list):
    file_desc_dict = {}
    for file_path in file_path_list:
        if os.path.isdir(file_path):
            file_desc_value = {
                'file_type': 0,  # 0表示文件夹， 1表示文件
                'file_size': 0,  # 单位字节B
            }
            file_desc_dict[file_path] = file_desc_value
        else:
            file_size = os.path.getsize(file_path)
            file_update_time = os.path.getmtime(file_path)
            file_desc_value = {
                'file_type': 1,  # 0表示文件夹， 1表示文件
                'file_size': file_size,  # 单位字节B
                'file_update_time': file_update_time
            }
            file_desc_dict[file_path] = file_desc_value
    return file_desc_dict


# 获取文件名列表信息
def listdir(dir_path, file_path_list):
    nums = len(os.listdir(dir_path))
    if nums > 0:
        for file in os.listdir(dir_path):
            file_path = os.path.join(dir_path, file)
            if os.path.isdir(file_path):
                listdir(file_path, file_path_list)
            else:
                file_path_list.append(file_path)
    else:
        file_path_list.append(dir_path)


# 获取指定文件夹下文件描述信息的hash值
def get_all_files_desc(dir_path):
    file_path_list = []
    listdir(dir_path, file_path_list)
    # 这里已经获取文件夹下所有文件详细信息
    file_desc_dict = get_files_desc(dir_path, file_path_list)
    s = hashlib.sha256()
    s.update(json.dumps(file_desc_dict, ensure_ascii=False).encode("utf-8"))
    hash_value = s.hexdigest()
    return hash_value
