import cloneDeep from 'lodash/cloneDeep';
import type {FormInstanceFunctions, FormRule} from 'tdesign-vue-next';
import {DialogPlugin, Form, FormItem, Input, MessagePlugin} from 'tdesign-vue-next';
import {reactive, ref} from 'vue';
import {coverFormApi} from "@/api/formatter";

const rules: Record<string, Array<FormRule>> = {
  name: [{required: true, message: '名称不能为空', trigger: 'change'}],
};

export function handleAddUpdate<R extends Record<string, any>>(row: R | null, callBack: Function) {
  const formRef = ref<FormInstanceFunctions>();
  const isAdd = !row;
  const form = reactive<any>(row ? cloneDeep<R>(row) : {});

  const dialog = DialogPlugin({
    header: isAdd ? '添加' : '编辑',
    width: '30%',
    destroyOnClose: true,
    confirmBtn: {
      content: '保存',
      theme: 'primary',
      loading: false,
    },
    body: () => {
      return (
          <Form ref={formRef} data={form} rules={rules}>
            <FormItem name="name" label="名称">
              <Input v-model={form.name}/>
            </FormItem>
          </Form>
      );
    },
    onClosed: () => {
      dialog.destroy();
    },
    onConfirm: async () => {
      if ((await formRef.value.validate()) === true) {
        dialog.update({confirmBtn: {content: '保存中', loading: true}});
        (isAdd ? coverFormApi.add(form) : coverFormApi.update(form))
        .then(() => {
          MessagePlugin.success('已保存');
          dialog.hide();
          callBack?.();
        })
        .finally(() => {
          dialog.update({});
        });
      }
    },
  });
}
