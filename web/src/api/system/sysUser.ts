import {generateBasicApi} from "@/utils/request/utils";
import {request} from "@/utils/request";

const path = '/sys-user';

export const {add, remove, update, page, list, getById} = generateBasicApi(path);

export function login(data: {}) {
    return request.post({
            url: path + "/login",
            data: data
        },
    );
}

export function captcha() {
    return request.get({
            url: path + "/captcha",
        },
    );
}
