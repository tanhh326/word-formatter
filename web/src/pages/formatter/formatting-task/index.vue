<script lang="tsx" setup>
import cloneDeep from 'lodash/cloneDeep';
import {FormItem, Input, Link, Space, Tag} from 'tdesign-vue-next';
import {reactive} from 'vue';

import CrudPage from '@/components/crud-page/index.vue';
import {usePage, useRemove} from '@/hooks';
import {formattingTaskApi} from "@/api/formatter";
import {handleAddUpdate} from './handler';

const TASK_STATUS = {
  "1": <Tag theme="success" variant="light-outline">成功</Tag>,
  "0": <Tag theme="danger" variant="light-outline">失败</Tag>,
  "2": <Tag theme="primary" variant="light-outline">执行中</Tag>
}
const defaultQueryForm = {code: '', deptId: '', name: ''};
const queryForm = reactive(cloneDeep(defaultQueryForm));

const pageHook = usePage<any>({
  api: formattingTaskApi.list,
  async query(reset: boolean) {
    if (reset) {
      Object.assign(queryForm, cloneDeep(defaultQueryForm));
    }
    return Promise.resolve(queryForm);
  },
});

const {removeSignal} = useRemove<string>(formattingTaskApi.remove, pageHook.loadData);

const columns = [
  {
    title: '编号',
    colKey: 'id',
    align: 'center',
    width: 200
  },
  {
    title: '原文',
    colKey: 'originDoc',
    align: 'center',
    cell: (_, {row}) => <Link theme="primary" underline>{row.originDoc}</Link>
  },
  {
    title: '总耗时(秒)',
    colKey: 'totalTimeSpent',
    align: 'center',
  },
  {
    title: '状态',
    colKey: 'status',
    align: 'center',
    cell: (_, {row}) => TASK_STATUS[row.status]
  },
  {
    title: '结果',
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
    cell: (_, {row}) => (
        <>
          <Space>
            <Link theme="primary" onClick={() => handleAddUpdate(row, pageHook.loadData)}>
              下载
            </Link>
            <Link theme="primary" onClick={() => handleAddUpdate(row, pageHook.loadData)}>
              编辑
            </Link>
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
  <CrudPage :columns="columns" :hook="pageHook"
            :on-add="() => handleAddUpdate(null, pageHook.loadData)">
    <template #query-form-item>
      <FormItem label="名称">
        <Input v-model="queryForm.name"/>
      </FormItem>
    </template>
  </CrudPage>
</template>

<style lang="less" scoped></style>
