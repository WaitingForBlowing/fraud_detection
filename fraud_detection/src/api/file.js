import service from "@/utils/request";

export function downloadFile(path) {
  return service(({
    url: `/file/download/${path}`,
    method: 'get',
    responseType: 'blob'
  }))
}
