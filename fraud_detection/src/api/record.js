import service from "@/utils/request";

export function getRecordList(param) {
  return service(({
    url: '/records',
    method: 'get',
    params: param
  }))
}
