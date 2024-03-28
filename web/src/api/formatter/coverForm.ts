import {generateBasicApi} from "@/utils/request/utils";

const path = '/cover-form';

export const {add, remove, update, page, list, getById} = generateBasicApi(path);
