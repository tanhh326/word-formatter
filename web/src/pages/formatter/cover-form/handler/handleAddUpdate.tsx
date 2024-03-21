import cloneDeep from 'lodash/cloneDeep';
import type {FormInstanceFunctions, FormRule} from 'tdesign-vue-next';
import {DialogPlugin, Form, FormItem, Input, MessagePlugin, Upload} from 'tdesign-vue-next';
import {reactive, ref} from 'vue';
import {coverFormApi} from "@/api/formatter";
import {fileApi} from "@/api/system";

const rules: Record<string, Array<FormRule>> = {
    name: [{required: true, message: '名称不能为空', trigger: 'change'}],
    coverTemplateUrl: [{required: true, message: '封面模板必须上传', trigger: 'change'}],
};


export function handleAddUpdate<R extends Record<string, any>>(row: R | null, callBack: Function) {
    const formRef = ref<FormInstanceFunctions>();
    const isAdd = !row;
    const form = reactive<any>(row ? cloneDeep<R>(row) : {});

    const uploadFile = ref([]);

    async function requestMethod(file) {
        const {path} = await fileApi.upload(file.raw, {prefix: `/cover-temp`});
        form.coverTemplateUrl = path;
        return Promise.resolve({
            status: 'success',
            response: {url: "success"},
        });
    }

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
                    {isAdd && <FormItem name="coverTemplateUrl" label="封面模板">
                        <Upload accept=".doc,.docx" v-model={uploadFile.value} requestMethod={requestMethod}/>
                    </FormItem>}
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
