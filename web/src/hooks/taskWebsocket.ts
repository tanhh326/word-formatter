import {NotifyPlugin} from "tdesign-vue-next";

export default function taskWebsocket() {
    NotifyPlugin.success({title: '标题', content: `编号 ${new Date().getTime()} 排版任务已完成`})
}
