import {request} from "@/utils/request";

const path = "/file";

export function upload(file: File, params: any) {
    return request.upload("file", file, {params, url: path + "/upload", timeout: 10000 * 1000});
}

export function download(filePath: string) {
    return request.get({
            url: path + "/download",
            responseType: 'blob',
            timeout: 10000 * 1000,
            params: {path: filePath}
        },
        {
            isTransformResponse: false
        }
    );
}
