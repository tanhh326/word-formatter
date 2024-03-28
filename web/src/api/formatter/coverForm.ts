import {generateBasicApi} from "@/utils/request/utils";
import {request} from "@/utils/request";

const path = '/cover-form';

export const {add, remove, update, page, list, getById} = generateBasicApi(path);

export function tree() {
    return request.get({
            url: path + "/tree",
        },
    );
}
