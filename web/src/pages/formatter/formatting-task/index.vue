<script lang="tsx" setup>
import cloneDeep from 'lodash/cloneDeep';
import {DialogPlugin, FormItem, Input, Link, Loading, MessagePlugin, Select, Space, Tag} from 'tdesign-vue-next';
import {reactive, ref} from 'vue';

import CrudPage from '@/components/crud-page/index.vue';
import {usePage, useRemove} from '@/hooks';
import {formattingTaskApi} from "@/api/formatter";
import {fileApi} from "@/api/system";
import {wordFormatApi} from "@/api/formatter";
import {toFixed} from "ol/math";

function viewError(row: any) {
  DialogPlugin({
    header: '错误日志',
    body: () => row.errorMsg,
    width: '50%',
    footer: false,
  })
}

const TASK_STATUS = {
  "1": (row: any) => <Tag theme="success" variant="light-outline">成功</Tag>,
  "0": (row: any) => <Link underline theme="danger" onClick={() => viewError(row)}>失败</Link>,
  "2": (row: any) => <Tag theme="primary" variant="light-outline">执行中</Tag>
}

const STATUS_OPTIONS = [
  {label: '成功', value: '1'},
  {label: '失败', value: '0'},
  {label: '执行中', value: '2'},
]
const defaultQueryForm = {originDoc: '', id: '', status: ''};

const queryForm = reactive(cloneDeep(defaultQueryForm));

const pageHook = usePage<any>({
  api: formattingTaskApi.list,
  defaultSort: [
    {
      sortBy: "createdTime",
      descending: true
    }
  ],
  async query(reset: boolean) {
    if (reset) {
      Object.assign(queryForm, cloneDeep(defaultQueryForm));
    }
    return Promise.resolve(queryForm);
  },
});

const {removeSignal} = useRemove<string>(formattingTaskApi.remove, pageHook.loadData);

const downloadLoading = ref([]);

const retryLoading = ref([]);

async function handleDownload(row: any, rowIndex: number) {
  downloadLoading.value[rowIndex] = true;
  const data = await fileApi.download(row.resultDoc);
  const url = window.URL.createObjectURL(data);
  const a = document.createElement("a");
  a.href = url;
  a.download = "已排版-" + row.originDoc;
  a.click();
  window.URL.revokeObjectURL(url);
  downloadLoading.value[rowIndex] = false;
}

async function handleRetry(row: any, rowIndex: number) {
  const data = await wordFormatApi.retry(row.requestParams);
  console.log(data)
  if(data.code == 0){
    MessagePlugin.success(data.msg)
  }else {
    MessagePlugin.error(data.msg)
  }
}

const columns = [
  {
    title: '编号',
    colKey: 'id',
    align: 'center',
    width: 200,
    ellipsis: true,
    fixed: 'left'
  },
  {
    title: '原文',
    colKey: 'originDoc',
    align: 'center',
    ellipsis: true,
    width: 200,
    fixed: 'left',
    cell: (_, {row}) => <Link theme="primary" underline>{row.originDoc}</Link>
  },
  {
    title: '总耗时(秒)',
    colKey: 'totalTimeSpent',
    align: 'center',
    sorter: true,
  },
  {
    title: '状态',
    colKey: 'status',
    align: 'center',
    cell: (_, {row}) => TASK_STATUS[row.status]?.(row)
  },
  {
    title: '结果大小(兆)',
    colKey: 'resultDocSize',
    align: 'center',
    sorter: true,
    cell: (_, {row}) => toFixed(row.resultDocSize, 2)
  },
  {
    title: '结果文件',
    colKey: 'resultDoc',
    align: 'center',
    display: false,
  },
  {
    title: '创建时间',
    colKey: 'createdTime',
    align: 'center',
    sorter: true,
  },
  {
    title: '修改时间',
    colKey: 'updateTime',
    align: 'center',
    sorter: true,
    display: false,
  },
  {
    title: '操作',
    colKey: 'operate',
    align: 'center',
    cell: (_, {row, rowIndex}) => (
        <>
          <Space>
            {
            <Link theme="primary" onClick={() => handleRetry(row, rowIndex)}>
              重试
            </Link>}
            {
              downloadLoading.value[rowIndex] ? <Loading size="small" text="下载中..."/> :
                  <Link theme="primary" disabled={row.status != 1} onClick={() => handleDownload(row, rowIndex)}>
                    下载结果
                  </Link>}
            <Link theme="danger" onClick={() => removeSignal(row.id)}>
              删除
            </Link>
          </Space>
        </>
    ),
  },
];

</script>

<template>
  <CrudPage :columns="columns" :hook="pageHook">
    <template #query-form-item>
      <FormItem label="编号">
        <Input v-model="queryForm.id"/>
      </FormItem>
      <FormItem label="原文">
        <Input v-model="queryForm.originDoc"/>
      </FormItem>
      <FormItem label="状态">
        <Select v-model="queryForm.status" :options="STATUS_OPTIONS"/>
      </FormItem>
    </template>
  </CrudPage>
</template>

<style lang="less" scoped></style>
