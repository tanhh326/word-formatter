<script lang="ts" setup>
import {AddIcon, CloudUploadIcon, RefreshIcon, Setting1Icon} from 'tdesign-icons-vue-next';
import {
  Button,
  Card,
  Col,
  Form,
  FormItem,
  PaginationProps,
  PrimaryTable,
  PrimaryTableCol,
  Row,
  Space,
  TableChangeContext,
  TableChangeData
} from 'tdesign-vue-next';
import {Ref, ref, watchEffect} from 'vue';

type YcTableCol<T = any> = (PrimaryTableCol<T> & { display?: Boolean; configurable?: Boolean })[];

interface Hook {
  loading?: Ref<boolean>;
  data?: Ref<any[]>;
  pagination?: PaginationProps;
  onChange?: (tcd: TableChangeData, tcc: TableChangeContext<any>) => Promise<void>;
  loadData: (reset?: 'reset') => Promise<void>;
  selectedRowKeys?: Ref<string[]>;
}

const props = defineProps<{
  columns: YcTableCol;
  hook?: Hook;
  bordered?: Boolean;
  onAdd?: Function;
  onImport?: Function;
  border?: Boolean;
}>();

const {loading, data, pagination, onChange, loadData, selectedRowKeys} = props.hook;

const colConfig = ref(false);

// 默认显示的列
const defaultDisplayCol = ref([]);

// 可以进行列配置的列
const configurableCol = ref([]);

watchEffect(() => {
  configurableCol.value = props.columns.reduce((acc, {configurable, colKey}) => {
    if (configurable !== false) {
      acc.push(colKey);
    }
    return acc;
  }, []);

  defaultDisplayCol.value = props.columns.reduce((acc, {configurable, display, colKey}) => {
    if (configurable !== false && display !== false) {
      acc.push(colKey);
    }
    return acc;
  }, []);
});
</script>

<template>
  <Card :bordered="border !== false">
    <!--  查询表单  -->
    <div v-if="$slots['query-form-item']" class="query-form">
      <Form auto-label-width layout="inline" @reset="loadData('reset')" @submit="loadData()">
        <slot name="query-form-item"></slot>
        <FormItem>
          <Space>
            <Button theme="primary" type="submit">查询</Button>
            <Button theme="default" type="reset">重置</Button>
          </Space>
        </FormItem>
      </Form>
    </div>
    <!--  操作按钮  -->
    <Row :style="{ '--padding-top': `${$slots['query-form-item'] ? 14 : 0}px` }" class="action"
         justify="space-between">
      <Col>
        <Space>
          <Button theme="default" variant="outline" @click="loadData()">
            <RefreshIcon/>
          </Button>
          <Button v-if="onAdd" @click="() => onAdd()">
            <template #icon>
              <AddIcon/>
            </template>
            新增
          </Button>
          <Button v-if="onImport" variant="outline" @click="() => onImport()">
            <template #icon>
              <CloudUploadIcon/>
            </template>
            导入
          </Button>
          <!--  额外操作按钮使用此插槽  -->
          <slot name="extra-action"></slot>
        </Space>
      </Col>
      <Col v-if="!$slots.default">
        <Button shape="circle" variant="outline" @click="colConfig = true">
          <Setting1Icon/>
        </Button>
      </Col>
    </Row>
    {{ bordered }}
    <PrimaryTable
        v-if="!$slots.default"
        v-model:column-controller-visible="colConfig"
        v-model:display-columns="defaultDisplayCol"
        v-model:selected-row-keys="selectedRowKeys"
        :bordered="bordered !== false"
        :column-controller="{ hideTriggerButton: true, fields: configurableCol }"
        :columns="columns"
        :data="data"
        :loading="loading"
        :pagination="pagination"
        ellipsis
        hover
        multiple-sort
        resizable
        row-key="id"
        size="large"
        @change="onChange"
    />
  </Card>
</template>

<style lang="less" scoped>
.query-form {
  border-bottom: 1px solid var(--td-border-level-1-color);
  padding-bottom: 24px;
}

.t-row.action {
  padding-top: var(--padding-top);
  padding-bottom: 14px;
}
</style>
