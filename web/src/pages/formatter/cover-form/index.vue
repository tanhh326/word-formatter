<script lang="tsx" setup>
import cloneDeep from 'lodash/cloneDeep';
import {DialogPlugin, FormItem, Input, Loading} from 'tdesign-vue-next';
import {reactive, ref} from 'vue';

import CrudPage from '@/components/crud-page/index.vue';
import {usePage, useRemove} from '@/hooks';
import {coverFormApi} from "@/api/formatter";
import {fileApi} from "@/api/system";
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

const previewLoading = ref(false);

async function previewPdf(row: any) {
  const url = ref("");
  previewLoading.value = true;
  DialogPlugin({
    header: '预览',
    body: () => <Loading loading={previewLoading.value}>
      <iframe width='100%' height="500px" style="border:0" src={url.value}></iframe>
    </Loading>,
    width: '50%',
    footer: false,
  })
  const data = await fileApi.doc2pdf(row.coverTemplateUrl);
  url.value = URL.createObjectURL(new Blob([data], {type: "application/pdf"}));
  previewLoading.value = false;
}

const previewImgArr = ref([]);

async function previewImg(row: any) {
  previewImgArr.value.forEach((url) => {
    window.URL.revokeObjectURL(url);
  })
  previewImgArr.value = [];
  Promise.all(row.coverPreviewUrl?.map((url: string) =>
      new Promise((resolve, reject) => resolve(fileApi.download(url)))
  )).then(data => {
    data.forEach((item) => {
      const url = window.URL.createObjectURL(item);
      previewImgArr.value.push(url);
    })
  })
}

const downloadLoading = ref([]);

async function handleDownload(row: any, rowIndex: number) {
  downloadLoading.value[rowIndex] = true;
  const data = await fileApi.download(row.coverTemplateUrl);
  const url = window.URL.createObjectURL(data);
  const a = document.createElement("a");
  a.href = url;
  a.download = "封面-" + row.name + ".doc";
  a.click();
  window.URL.revokeObjectURL(url);
  downloadLoading.value[rowIndex] = false;
}

const columns = [
  {
    title: '名称',
    colKey: 'name',
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
    cell: (_, {row, rowIndex}) => (
        <t-space>
          <t-image-viewer close-on-overlay images={previewImgArr.value}>
            {{
              trigger: ({open}) => <t-link theme="primary" onClick={() => {
                open();
                previewImg(row);
              }}>
                <t-link theme="primary">图片预览</t-link>
              </t-link>
            }}
          </t-image-viewer>
          <t-link theme="primary" onClick={() => previewPdf(row)}>
            pdf预览
          </t-link>
          {
            downloadLoading.value[rowIndex] ? <Loading size="small" text="下载中..."/> :
                <t-link theme="primary" onClick={() => handleDownload(row, rowIndex)}>
                  模板下载
                </t-link>}
          <t-link theme="primary" onClick={() => handleAddUpdate(row, pageHook.loadData)}>
            编辑
          </t-link>
          <t-link theme="danger" onClick={() => removeSignal(row.id)}>
            删除
          </t-link>
        </t-space>
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

<style lang="less" scoped>
</style>
