from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from model import model_map
from model.data_io import reader
from model.data_io import uploader
from model.load_model import load

@csrf_exempt
def raw_predict(request):
    try:
        model_id = int(request.POST.get('model_id'))
        model_url = request.POST.get('model_url')
        if model_id in model_map:
            model = model_map[model_id]
        else:
            model = load(model_url)
            if model is None:
                return JsonResponse({'code': 1, 'message': '模型不存在'})
            model_map[model_id] = model
        data = reader(request.POST.get('data_url'))
        x = data.values[:, 2:]
        predict = model.predict(x)
        data['result'] = predict.astype(int)
        result = uploader(data)
        if result == -1:
            return JsonResponse({'code': 1, 'message': '文件上传异常'})
    except Exception as e:
        print(e)
        return JsonResponse({'code': 1, 'message': '发生错误，请重试'})
        # 返回预测结果
    return JsonResponse({'code': 0, 'message': '预测结果成功', 'data': result})









