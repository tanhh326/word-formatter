import {generateBasicApi} from "@/utils/request/utils";

const path = '/format-config';

export const {add, remove, update, list, getById} = generateBasicApi(path);
