import service from "@/utils/request";

export function singleDetect(param) {
  return service(({
    url: '/detect/raw',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }))
}

export function multiDetect(param) {
  return service(({
    url: '/detect/rate',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }))
}
