import service from "@/utils/request";

export function getResultList(param) {
  return service(({
    url: '/results',
    method: 'get',
    params: param
  }))
}

export function updateResult(param) {
  return service(({
    url: `/result/${param}`,
    method: 'put',
  }))
}
