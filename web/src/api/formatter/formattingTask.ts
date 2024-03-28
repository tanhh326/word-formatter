import {generateBasicApi} from "@/utils/request/utils";

const path = '/formatting-task';

export const {add, remove, update, page, list, getById} = generateBasicApi(path);
