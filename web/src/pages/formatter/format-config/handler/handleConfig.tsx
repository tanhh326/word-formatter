import {ref} from "vue";
import {DialogPlugin, MessagePlugin, Space, Table, TabPanel, Tabs} from "tdesign-vue-next";
import ConfigForm from "../components/ConfigForm.vue";
import {formatConfigApi} from "@/api/formatter";

export function handleConfig(row: any) {
  const currTab = ref(1);

  const expandedRowKeys = ref([1, 2]);

  const columns = [
    {colKey: 'desc', title: 'x', width: '80',},
  ]

  const config = ref(row.config);

  //@ts-ignore
  const expandedRow = (h, {row: tRow}) => (
      <Space direction="vertical">
        {tRow.title && <ConfigForm type="标题" config={tRow.title}/>}
        {tRow.text && <ConfigForm type="正文" config={tRow.text}/>}
        {tRow.table && <ConfigForm type="表格" config={tRow.table}/>}
        {tRow.diagramTitle && <ConfigForm type="图题" config={tRow.diagramTitle}/>}
        {tRow.tableTitle && <ConfigForm type="表题" config={tRow.tableTitle}/>}
        {tRow.source && <ConfigForm type="资料来源" config={tRow.source}/>}
        {tRow.footnote && <ConfigForm type="脚注" config={tRow.footnote}/>}
      </Space>
  );

  const dialog = DialogPlugin({
    mode: "full-screen",
    header: "配置",
    confirmBtn: "保存",
    onClosed: () => {
      dialog.destroy();
    },
    onConfirm:  () => {
      dialog.update({confirmBtn: {content: '保存中', loading: true}});
      formatConfigApi.update({...row, ...{config: config.value}})
      .then(() => {
        MessagePlugin.success('已保存');
        dialog.hide();
      })
      .finally(() => {
        dialog.update({});
      });
    },
    body: () =>
        <Tabs v-if="false" v-model={currTab.value}>
          <TabPanel label="章节" value={1}>
            <Table v-model:expandedRowKeys={expandedRowKeys.value} columns={columns}
                   data={config.value}
                   expanded-row={expandedRow}
                   show-header={false}
                   expand-icon
                   row-key="desc"
                   stripe/>
          </TabPanel>
          <TabPanel label="页码页眉" value="2"></TabPanel>
          <TabPanel label="全局设置" value="3"></TabPanel>
        </Tabs>
  })
}
