<script lang="tsx" setup>
import {FormItem, Input, Link, Space} from 'tdesign-vue-next';
import {reactive} from 'vue';

import CrudPage from '@/components/crud-page/index.vue';
import {usePage, useRemove} from '@/hooks';
import {sysUserApi} from "@/api/system";

const defaultQueryForm = () => ({username: ''});

const queryForm = reactive(defaultQueryForm());

const pageHook = usePage<any>({
  api: sysUserApi.page,
  defaultSort: [
    {
      sortBy: "createdTime",
      descending: true
    }
  ],
  async query(reset: boolean) {
    if (reset) {
      Object.assign(queryForm, defaultQueryForm());
    }
    return Promise.resolve(queryForm);
  },
});

const {removeSignal} = useRemove<string>(sysUserApi.remove, pageHook.loadData);


const columns = [
  {
    title: '用户名',
    colKey: 'username',
    align: 'center',
    ellipsis: true,
    width: 200,
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
      <FormItem label="用户名">
        <Input v-model="queryForm.username"/>
      </FormItem>
    </template>
  </CrudPage>
</template>

<style lang="less" scoped></style>
