import {generateBasicApi} from "@/utils/request/utils";

const path = '/formatting-task';

export const {add, remove, update, list, getById} = generateBasicApi(path);
