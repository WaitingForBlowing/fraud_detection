import os
from minio import Minio

# 从环境变量中获取连接信息
endpoint = 'localhost:9000'
access_key = 'MRdb4ScHOYmW5plW'
secret_key = 'aBspcY7yScx29DYq3Zhs0qWljR5io0bI'
use_ssl = False

# 创建MinIO客户端对象
client = Minio(endpoint=endpoint,
               access_key=access_key,
               secret_key=secret_key,
               secure=use_ssl)
