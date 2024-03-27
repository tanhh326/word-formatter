<script lang="tsx" setup>
import cloneDeep from 'lodash/cloneDeep';
import {FormItem, Input, Link, Loading, Space} from 'tdesign-vue-next';
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

// async function preview(row: any) {
//   const url = ref("");
//   previewLoading.value = true;
//   DialogPlugin({
//     header: '预览',
//     body: () => <Loading loading={previewLoading.value}>
//       <iframe width='100%' height="500px" style="border:0" src={url.value}></iframe>
//     </Loading>,
//     width: '50%',
//     footer: false,
//   })
//   const data = await fileApi.doc2pdf(row.coverTemplateUrl);
//   url.value = URL.createObjectURL(new Blob([data], {type: "application/pdf"}));
//   previewLoading.value = false;
// }

// 改为图片显示
const visible = ref(false);
let images: string[] = [];
async function preview(row: any) {
  visible.value = false;
  images = [];
  const urls = await fileApi.getPictureURL(row.coverPreviewUrl.toString());
  for (let i = 0; i < urls.length; i++) {
    images.push(urls[i]);
  }
  console.log(images)
  visible.value = true;
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
        cell: (_, {row,rowIndex}) => (
        <>
        <Space>
        <Link theme="primary" onClick={() => preview(row)}>
      预览
    </Link>
{
  downloadLoading.value[rowIndex] ? <Loading size="small" text="下载中..."/> :
                  <Link theme="primary" onClick={() => handleDownload(row, rowIndex)}>
                    下载
                  </Link>}
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

  <div>
    <t-image-viewer v-model:visible="visible" :images="images">
    </t-image-viewer>
  </div>

</template>

<style lang="less"  scoped>
</style>
