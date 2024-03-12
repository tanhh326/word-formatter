import {generateBasicApi} from "@/utils/request/utils";
import {request} from "@/utils/request";

const path = '/word-format';

export const {add, remove, update, list, getById} = generateBasicApi(path);


export function formatting(file: File) {
  return request.upload("file", file,
      {url: path + "/formatting", responseType: 'blob',},
      {isTransformResponse: false}
  );
}