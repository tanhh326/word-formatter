<script lang="tsx" setup>
import cloneDeep from 'lodash/cloneDeep';
import {FormItem, Input, Link, Space} from 'tdesign-vue-next';
import {reactive} from 'vue';

import CrudPage from '@/components/crud-page/index.vue';
import {usePage, useRemove} from '@/hooks';
import {coverFormApi} from "@/api/formatter";
import {handleAddUpdate} from './handler';

const defaultQueryForm = {code: '', deptId: '', name: ''};
const queryForm = reactive(cloneDeep(defaultQueryForm));

const pageHook = usePage<any>({
  api: coverFormApi.list,
  async query(reset: boolean) {
    if (reset) {
      Object.assign(queryForm, cloneDeep(defaultQueryForm));
    }
    return Promise.resolve(queryForm);
  },
});

const {removeSignal} = useRemove<string>(coverFormApi.remove, pageHook.loadData);

const columns = [
  {
    title: '名称',
    colKey: 'name',
    align: 'center',
  },
  {
    title: '封面',
    colKey: 'coverPreviewUrl',
    align: 'center',
  },
  {
    title: '封面模板',
    colKey: 'coverTemplateUrl',
    align: 'center',
  },
  {
    title: '创建时间',
    colKey: 'createdTime',
    sorter: true,
    align: 'center',
  },
  {
    title: '修改时间',
    colKey: 'updateTime',
    sorter: true,
    align: 'center',
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
