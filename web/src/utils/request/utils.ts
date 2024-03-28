import isObject from 'lodash/isObject';
import isString from 'lodash/isString';
import type {PageRequest, PageResponse} from '@/types/globals';
import {request} from '@/utils/request';
import qs from "qs";

const DATE_TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';

export function joinTimestamp<T extends boolean>(join: boolean, restful: T): T extends true ? string : object;

export function joinTimestamp(join: boolean, restful = false): string | object {
    if (!join) {
        return restful ? '' : {};
    }
    const now = new Date().getTime();
    if (restful) {
        return `?_t=${now}`;
    }
    return {_t: now};
}

// 格式化提交参数时间
export function formatRequestDate(params: Recordable) {
    if (Object.prototype.toString.call(params) !== '[object Object]') {
        return;
    }

    for (const key in params) {
        // eslint-disable-next-line no-underscore-dangle
        if (params[key] && params[key]._isAMomentObject) {
            params[key] = params[key].format(DATE_TIME_FORMAT);
        }
        if (isString(key)) {
            const value = params[key];
            if (value) {
                try {
                    params[key] = isString(value) ? value.trim() : value;
                } catch (error: any) {
                    throw new Error(error);
                }
            }
        }
        if (isObject(params[key])) {
            formatRequestDate(params[key]);
        }
    }
}

// 将对象转为Url参数
export function setObjToUrlParams(baseUrl: string, obj: { [index: string]: any }): string {
    let parameters = '';
    for (const key in obj) {
        parameters += `${key}=${encodeURIComponent(obj[key])}&`;
    }
    parameters = parameters.replace(/&$/, '');
    return /\?$/.test(baseUrl) ? baseUrl + parameters : baseUrl.replace(/\/?$/, '?') + parameters;
}


/**
 * 生成基础api
 * @param path 需要带 `/` 前缀
 */
export function generateBasicApi<
    Request extends { id?: string } = Record<string, any>,
    Response = Record<string, any>,
    Query = Record<string, any>,
>(path: string) {
    return {
        add: function (data: Request) {
            return request.post({
                url: `${path}`,
                data,
            });
        },
        remove: function (ids: string[]) {
            return request.delete({
                url: `${path}`,
                data: ids,
            });
        },
        update: function (data: Request) {
            return request.put({
                url: `${path}`,
                data,
            });
        },
        page: function (params?: Query) {
            return request.get<Response[]>({
                url: `${path}`,
                params,
                paramsSerializer: (params) => {
                    return qs.stringify(params, {
                        allowDots: true
                    })
                }
            });
        },
        list: function (params?: Query) {
            return request.get<Response[]>({
                url: `${path}/list`,
                params,
            });
        },
        getById: function (id: string) {
            return request.get<Response>({
                url: `${path}/${id}`,
            });
        },
    };
}
