import io
import pandas as pd
from MinioClient import client
from minio.error import S3Error
import uuid


def data_reader(data_path):
    # 从MinIO中获取对象并将其读入内存中
    try:
        file_object = client.get_object("online", data_path)
        file_content = file_object.read()
    except S3Error as err:
        print(err)
        return {}

    # 3. 将内存中的对象加载到Pandas DataFrame中并返回
    data = pd.read_csv(io.BytesIO(file_content))
    return data


def data_uploader(input_data):
    try:
        csv_data = input_data.to_csv(index=False)
        # 将CSV数据读入内存中
        csv_data_bytes = bytes(csv_data, 'utf-8')
        csv_data_bytes_io = io.BytesIO(csv_data_bytes)
        file_name = str(uuid.uuid4()) + '.csv'
        client.put_object(
                        bucket_name='online',
                        object_name=file_name,
                        data=csv_data_bytes_io,
                        length=len(csv_data_bytes_io.getvalue()))
    except Exception as e:
        print(e)
        return -1
    return file_name


reader = data_reader
uploader = data_uploader
