import service from "@/utils/request";

export function getModelList(param) {
  return service(({
    url: '/models/page',
    method: 'get',
    params: param
  }))
}

export function getAllModel() {
  return service(({
    url: '/models',
    method: 'get',
  }))
}

export function downloadModel(id) {
  return service(({
    url: `/download/${id}`,
    method: 'get',
    responseType: 'blob'
  }))
}

export function importModel(param) {
  return service(({
    url: '/upload',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }))
}
