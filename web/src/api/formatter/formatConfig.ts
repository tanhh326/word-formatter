import {generateBasicApi} from "@/utils/request/utils";

const path = '/format-config';

export const {add, remove, update, page, list, getById} = generateBasicApi(path);
