import {generateBasicApi} from "@/utils/request/utils";

const path = '/cover-form';

export const {add, remove, update, list, getById} = generateBasicApi(path);
