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
