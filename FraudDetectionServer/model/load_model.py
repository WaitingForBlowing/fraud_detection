from io import BytesIO
import joblib
from MinioClient import client


def load_model(path):
    # 从Minio中获取模型对象
    model_object = client.get_object('online', path)
    # 读取对象内容
    model_content = model_object.read()
    # 从内存中加载模型
    model = joblib.load(BytesIO(model_content))
    return model

load = load_model
