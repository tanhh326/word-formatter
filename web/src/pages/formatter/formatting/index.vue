<script lang="tsx" setup>
import {MessagePlugin, Space} from "tdesign-vue-next";
import {ref} from "vue";
import ConfigForm from "./components/ConfigForm.vue";
import {wordFormatApi} from "@/api/formatter"

const currTab = ref("1");

const columns = [
  {colKey: 'desc', title: 'x', width: '80',},
]

const data = ref([]);
const expandedRow = (h, {row}) => (
    <Space direction="vertical">
      {row.title && <ConfigForm type="标题" config={row.title}/>}
      {row.text && <ConfigForm type="正文" config={row.text}/>}
      {row.table && <ConfigForm type="表格" config={row.table}/>}
      {row.diagramTitle && <ConfigForm type="图题" config={row.diagramTitle}/>}
      {row.tableTitle && <ConfigForm type="表题" config={row.tableTitle}/>}
      {row.source && <ConfigForm type="资料来源" config={row.source}/>}
      {row.footnote && <ConfigForm type="脚注" config={row.footnote}/>}
    </Space>
);

const expandedRowKeys = ref([1, 2]);

const uploadFiles = ref<any>([]);

function requestMethod() {
  return Promise.resolve({
    status: 'success',
    response: {url: "success"},
  });
}

const loading = ref(false);

async function handleFormat() {
  if (uploadFiles.value.length > 0) {
    loading.value = true;
    const selectedFile = uploadFiles.value[0].raw;
    await wordFormatApi.formatting(selectedFile);
    await MessagePlugin.success("排版任务已创建");
    loading.value = false;
  } else {
    await MessagePlugin.error("请先选择文件！")
  }
}

function handleCopy() {
  navigator.clipboard.writeText(JSON.stringify(data.value, null, 2))
  MessagePlugin.success("复制成功！");
}

function handleExport() {
  const blob = new Blob([JSON.stringify(data.value, null, 2)], {type: 'application/json'});
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = "config.json";
  a.click();
  window.URL.revokeObjectURL(url);
}

const current = ref(0);
</script>

<template>
  <div style="width: 40%;margin:auto">
    <t-steps :current="current" class="steps-demos-extra" status="process">
      <t-step-item title="步骤1">
      </t-step-item>
      <t-step-item content="这里是提示文字" title="步骤2">
      </t-step-item>
      <t-step-item content="这里是提示文字" title="步骤3">
      </t-step-item>
      <t-step-item content="这里是提示文字" title="步骤4">
      </t-step-item>
    </t-steps>
    <div class="step-form">
      <t-upload
          v-if="current==0"
          v-model="uploadFiles" :abridge-name="[10, 8]"
          :data="{ extra_data: 123, file_name: 'certificate' }"
          :request-method="requestMethod"
          draggable
          theme="file"
      />
      <div v-if="current==1">封面</div>
      <div v-if="current==2">格式配置</div>
      <div v-if="current==3">选择封面</div>
    </div>
    <t-button size="small" variant="base" @click="current++"> 下一步</t-button>
    <div></div>
    <t-space style="margin: 14px 0">
      <t-button theme="default" @click="handleExport">导出配置</t-button>
      <t-button theme="default" @click="handleCopy">复制配置</t-button>
      <t-upload v-model="uploadFiles" :request-method="requestMethod"/>
      <t-button :loading="loading" @click="handleFormat">格式化</t-button>
    </t-space>
    <t-tabs v-if="false" v-model="currTab">
      <t-tab-panel label="章节" value="1">
        <t-table v-model:expandedRowKeys="expandedRowKeys" :columns="columns" :data="data"
                 :expanded-row="expandedRow"
                 :show-header="false"
                 expand-icon
                 row-key="desc"
                 stripe>
        </t-table>
      </t-tab-panel>
      <t-tab-panel label="页码页眉" value="2"></t-tab-panel>
      <t-tab-panel label="全局设置" value="3"></t-tab-panel>
    </t-tabs>
  </div>
</template>

<style scoped>
#cesiumContainer {
  width: 100vw;
  height: 100vh;
}

.step-form {
  padding: 14px 0;
  display: flex;
  justify-content: center;
  height: 150px;
}
</style>
