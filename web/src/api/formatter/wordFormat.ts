import {generateBasicApi} from "@/utils/request/utils";
import {request} from "@/utils/request";

const path = '/word-format';

export const {add, remove, update, page, list, getById} = generateBasicApi(path);


export function formatting(file: File, data: {}) {
    return request.upload("file", file,
        {url: path + "/formatting", responseType: 'blob', timeout: 10000 * 1000, data},
        {isTransformResponse: false}
    );
}

export function analyseCover(file: File, data: {
    zh: any[],
    en: any[]
}) {
    return request.upload("file", file,
        {url: path + "/analyse-cover", timeout: 10000 * 1000, data},
    );
}


export function retry(data: {}) {
    return request.post({
            url: path + "/retry",
            data: data
        },
        {
            isTransformResponse: false
        }
    );
}
