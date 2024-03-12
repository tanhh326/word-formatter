// 通用声明

// Vue
declare module '*.vue' {
  import {DefineComponent} from 'vue';

  const component: DefineComponent<{}, {}, any>;
  export default component;
}

declare type ClassName = { [className: string]: any } | ClassName[] | string;

declare module '*.svg' {
  const CONTENT: string;
  export default CONTENT;
}

declare type Recordable<T = any> = Record<string, T>;


declare interface PageResponse<T> {
  // 当前页
  current: number;
  // 分页大小
  size: number;
  // 数据总数
  total: number;
  // 总页数
  pages: number;
  // 当前页数据
  records: T[];
}

declare interface PageRequest {
  current: number;
  size: number;
  orders?: { column: string; asc: boolean }[];
}
