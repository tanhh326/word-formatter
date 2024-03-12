import { DialogPlugin, MessagePlugin } from 'tdesign-vue-next';
import { computed, ref } from 'vue';

/**
 * 通用删除hook
 * @param api 删除数据的接口i
 * @param callBack 删除后的回调方法，例如重新加载数据
 */
export default function remove<T = string>(api: (ids: T[]) => Promise<any>, callBack: Function) {
  const selectedRowKeys = ref([]);

  const selectColumn = {
    colKey: 'row-select',
    type: 'multiple',
    width: 50,
  };

  const batchRemoveAble = computed(() => selectedRowKeys.value.length);

  function removeSignal(id: T) {
    const dialog = DialogPlugin.confirm({
      theme: 'danger',
      header: '提示',
      body: () => '确认删除此数据？',
      onClosed: () => {
        dialog.destroy();
      },
      onConfirm: async () => {
        dialog.setConfirmLoading(true);
        await api([id]);
        dialog.setConfirmLoading(false);
        callBack();
        dialog.hide();
        await MessagePlugin.success('已删除');
      },
    });
  }

  function removeBatch() {
    const dialog = DialogPlugin.confirm({
      theme: 'danger',
      header: '提示',
      body: () => `确认删除当前选中的${selectedRowKeys.value.length}条数据？`,
      onClosed: () => {
        dialog.destroy();
      },
      onConfirm: async () => {
        dialog.setConfirmLoading(true);
        await api(selectedRowKeys.value);
        dialog.setConfirmLoading(false);
        callBack();
        dialog.hide();
        await MessagePlugin.success('已批量删除');
      },
    });
  }

  return {
    selectedRowKeys,
    batchRemoveAble,
    removeSignal,
    removeBatch,
    selectColumn,
  };
}
