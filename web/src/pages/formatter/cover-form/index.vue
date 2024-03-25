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
  images.length = 0;
  visible.value = true;
  const urls = await fileApi.getPictureURL(row.coverPreviewUrl.toString());
  for (let i = 0; i < urls.length; i++) {
    images.push(urls[i]);
    // images.push("https://tdesign.gtimg.com/demo/demo-image-1.png");
  }
  console.log(images);
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

  <t-image-viewer v-model:visible="visible" :images="images" >
  </t-image-viewer>
</template>

<style lang="less"  scoped>
.tdesign-demo-image-viewer__ui-image {
  width: 100%;
  height: 100%;
  display: inline-flex;
  position: relative;
  justify-content: center;
  align-items: center;
  border-radius: var(--td-radius-small);
  overflow: hidden;
}

.tdesign-demo-image-viewer__ui-image--hover {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  left: 0;
  top: 0;
  opacity: 0;
  background-color: rgba(0, 0, 0, 0.6);
  color: var(--td-text-color-anti);
  line-height: 22px;
  transition: 0.2s;
}

.tdesign-demo-image-viewer__ui-image:hover .tdesign-demo-image-viewer__ui-image--hover {
  opacity: 1;
  cursor: pointer;
}

.tdesign-demo-image-viewer__ui-image--img {
  width: auto;
  height: auto;
  max-width: 100%;
  max-height: 100%;
  cursor: pointer;
  position: absolute;
}

.tdesign-demo-image-viewer__ui-image--footer {
  padding: 0 16px;
  height: 56px;
  width: 100%;
  line-height: 56px;
  font-size: 16px;
  position: absolute;
  bottom: 0;
  color: var(--td-text-color-anti);
  background-image: linear-gradient(0deg, rgba(0, 0, 0, 0.4) 0%, rgba(0, 0, 0, 0) 100%);
  display: flex;
  box-sizing: border-box;
}

.tdesign-demo-image-viewer__ui-image--title {
  flex: 1;
}

.tdesign-demo-popup__reference {
  margin-left: 16px;
}

.tdesign-demo-image-viewer__ui-image--icons .tdesign-demo-icon {
  cursor: pointer;
}

.tdesign-demo-image-viewer__base {
  width: 160px;
  height: 160px;
  margin: 10px;
  border: 4px solid var(--td-bg-color-secondarycontainer);
  border-radius: var(--td-radius-medium);
}
</style>
