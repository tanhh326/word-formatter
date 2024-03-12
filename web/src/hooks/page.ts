import type { FilterValue, PaginationProps, TableChangeContext, TableChangeData } from 'tdesign-vue-next';
import { SortInfo } from 'tdesign-vue-next';
import { reactive, ref } from 'vue';

interface Options {
  /**
   * 获取数据的接口
   * @param query
   */
  api: (query: any) => Promise<any>;
  /**
   * 生成查询参数
   */
  query: (reset: boolean, filter: FilterValue) => Promise<any>;
  /**
   * 表格数据转换器
   * @param data
   */
  dataConvert?: (data: Array<any>) => Array<any>;
  /**
   * 开启首次加载(默认:true)
   */
  firstLoadEnable?: boolean;
  /**
   * 默认排序规则
   */
  defaultSort?: SortInfo[];
  /**
   * 默认筛选
   */
  defaultFilter?: FilterValue;
}

// 排序字段转成mybatis-plus要的下划线命名方式
function convertCamelToUnderscore(camelCase: string) {
  return camelCase.replace(/([A-Z])/g, '_$1').toLowerCase();
}

export default function page<T>(options: Options) {
  const { api, query, firstLoadEnable = true, defaultSort, defaultFilter, dataConvert } = options;

  const loading = ref<boolean>(false);

  const data = ref<T[]>([]);

  let orders = defaultSort || [];

  const filterValue = ref(defaultFilter);

  const selectedRowKeys = ref<string[]>([]);

  const pagination = reactive<PaginationProps>({
    current: 1,
    pageSize: 10,
    total: 0,
    showJumper: true,
  });

  async function loadData(reset?: 'reset') {
    loading.value = true;
    data.value = [];
    const isReset = reset === 'reset';
    if (isReset) {
      pagination.current = 1;
    }
    const queryParam = await query(isReset, filterValue.value);
    const params = {
      ...queryParam,
      current: pagination.current,
      size: pagination.pageSize,
      orders: orders.map(({ sortBy, descending }) => ({ column: convertCamelToUnderscore(sortBy), asc: !descending })),
    };

    const { total, records, current, pages } = (await api(params)) || {};
    const [$total, $current, $pages] = [Number(total), Number(current), Number(pages)];
    // 当前页码大于总页码，重发请求
    if ($pages > 0 && $current > $pages) {
      pagination.current = $pages;
      await loadData();
      return;
    }
    pagination.total = $total;
    data.value = dataConvert ? dataConvert(records) : records;
    loading.value = false;
  }

  async function onChange(
    { sorter, pagination: _pagination, filter }: TableChangeData,
    { trigger }: TableChangeContext<T>,
  ) {
    switch (trigger) {
      case 'filter': {
        filterValue.value = filter;
        break;
      }
      case 'sorter': {
        orders = sorter as SortInfo[];
        break;
      }
      case 'pagination': {
        const { current, pageSize } = _pagination;
        pagination.current = current;
        pagination.pageSize = pageSize;
        break;
      }
      default:
    }
    await loadData();
  }

  if (firstLoadEnable) {
    loadData('reset').then();
  }

  return {
    loading,
    data,
    pagination,
    loadData,
    onChange,
    selectedRowKeys,
  };
}
