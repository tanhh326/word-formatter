<script lang="tsx" setup>
import {MessagePlugin, Space} from "tdesign-vue-next";
import {ref} from "vue";
import ConfigForm from "@/pages/formatter/format-config/components/ConfigForm.vue";
import {coverFormApi, formatConfigApi, wordFormatApi} from "@/api/formatter"
import {DEGREE_OPTIONS} from "./constant";

const currTab = ref("1");

const columns = [
  {colKey: 'desc', title: 'x', width: '80',},
]

const data = ref([]);

const optionContainer = ref({
  coverForm: [],
  formatConfig: [],
})

coverFormApi.list({}).then(({records}) => {
  optionContainer.value.coverForm = records.map((it: any) => ({label: it.name, value: it.id}));
});
formatConfigApi.list({}).then(({records}) => {
  optionContainer.value.formatConfig = records.map((it: any) => ({label: it.name, value: it.id}));
});
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


const submitForm = ref({
  degree: "",
  formatConfigId: "",
  zhCover: {
    id: "",
    form: {}
  },
  enCover: {
    id: "",
    form: {}
  },
  referencesOrderly: false
});

async function handleFormat() {
  if (uploadFiles.value.length > 0) {
    loading.value = true;
    const selectedFile = uploadFiles.value[0].raw;
    await wordFormatApi.formatting(selectedFile, submitForm.value);
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

const setup2 = ref({
  loading: false,
  currTab: "1",
  coverForm: {
    zh: {},
    en: {}
  },
  dynamicCoverForm: {
    zh: [],
    en: []
  },
  formRule: {
    degree: [
      {required: true, message: '学位必须选择', trigger: 'blur'},
      {required: true, message: "学位必须选择", trigger: "change"},
    ],
    formatConfigId: [
      {required: true, message: '配置必须选择', trigger: 'blur'},
      {required: true, message: "配置必须选择", trigger: "change"},
    ],
    "zhCover.id": [
      {required: true, message: '中文封面必须选择', trigger: 'blur'},
      {required: true, message: "中文封面必须选择", trigger: "change"},
    ],
    "enCover.id": [
      {required: true, message: '英文封面必须选择', trigger: 'blur'},
      {required: true, message: "英文封面必须选择", trigger: "change"},
    ],
  }
});

const setup2FormRef = ref();

async function changeSetup(type: "next" | "pre") {
  let able = true;
  if (type === "next") {
    switch (current.value) {
      case 0:
        break;
      case 1:
        if ((await setup2FormRef.value.validate()) === true) {
          setup2.value.dynamicCoverForm.zh = (await coverFormApi.getById(submitForm.value.zhCover.id)).form;
          setup2.value.dynamicCoverForm.en = (await coverFormApi.getById(submitForm.value.enCover.id)).form;
        } else {
          able = false;
        }
        break;
      case 2:
        break;
    }
  }
  if (able) {
    if (type === "next") {
      current.value++;
    } else {
      current.value--;
    }
  }
}
</script>

<template>
  <t-card>
    <div style="width: 40%;margin:auto">
      <t-steps :current="current" class="steps-demos-extra" status="process">
        <t-step-item content="上传文件" title="步骤1">
        </t-step-item>
        <t-step-item content="选择配置" title="步骤2">
        </t-step-item>
        <t-step-item content="设置封面" title="步骤3">
        </t-step-item>
      </t-steps>
      <div class="step-form">
        <div v-show="current==0">
          <t-upload
              v-model="uploadFiles" :abridge-name="[10, 8]"
              :data="{ extra_data: 123, file_name: 'certificate' }"
              :request-method="requestMethod"
              draggable
              theme="file"
          />
        </div>
        <div v-show="current==1">
          <t-form ref="setup2FormRef" :data="submitForm" :rules="setup2.formRule" label-align="left"
                  required-mark>
            <t-form-item label="学位" name="degree">
              <t-select v-model="submitForm.degree" :options="DEGREE_OPTIONS"></t-select>
            </t-form-item>
            <t-form-item label="配置" name="formatConfigId">
              <t-select v-model="submitForm.formatConfigId"
                        :options="optionContainer.formatConfig"></t-select>
            </t-form-item>
            <t-form-item label="中文封面" name="zhCover.id">
              <t-select v-model="submitForm.zhCover.id"
                        :options="optionContainer.coverForm"></t-select>
            </t-form-item>
            <t-form-item label="英文封面" name="enCover.id">
              <t-select v-model="submitForm.enCover.id"
                        :options="optionContainer.coverForm"></t-select>
            </t-form-item>
            <t-form-item label="参考文献顺序">
              <t-switch v-model="submitForm.referencesOrderly"></t-switch>
            </t-form-item>
          </t-form>
        </div>
        <div v-show="current===2" style="width: 100%">
          <t-tabs v-model="setup2.currTab">
            <t-tab-panel label="中文" value="1">
              <t-form style="margin-top: 14px">
                <t-form-item v-for="(item,index) in setup2.dynamicCoverForm.zh"
                             :key="index" :label="item.label">
                  <t-textarea v-model="submitForm.zhCover.form[item.key]"></t-textarea>
                </t-form-item>
              </t-form>
            </t-tab-panel>
            <t-tab-panel label="英文" value="2">
              <t-form style="margin-top: 14px">
                <t-form-item v-for="(item,index) in setup2.dynamicCoverForm.en"
                             :key="index" :label="item.label">
                  <t-textarea v-model="submitForm.enCover.form[item.key]"></t-textarea>
                </t-form-item>
              </t-form>
            </t-tab-panel>
          </t-tabs>
        </div>
      </div>
      <t-button v-if="current" size="small" theme="default" variant="base"
                @click="changeSetup('pre')">上一步
      </t-button>
      <t-button v-if="current === 2" :loading="loading" size="small" variant="base" @click="handleFormat">执行
      </t-button>
      <t-button v-else size="small" variant="base" @click="changeSetup('next')">下一步
      </t-button>
      <div></div>
      <t-space v-if="false" style="margin: 14px 0">
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
  </t-card>
</template>

<style scoped>
.step-form {
  padding: 14px 0;
  display: flex;
  justify-content: center;
  height: 400px;
  overflow-y: auto;
  margin-bottom: 14px;
}
</style>

