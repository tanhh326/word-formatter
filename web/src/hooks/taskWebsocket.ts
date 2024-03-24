import {NotifyPlugin} from "tdesign-vue-next";

export default function taskWebsocket() {
    // todo 此处的时间需要改成用户id
    const ws = new WebSocket(`${import.meta.env.VITE_WS_URL}/websocket/${new Date().getTime()}`);

    ws.onmessage = (event) => {
        const {status, id, originDoc} = JSON.parse(event.data);
        if (status == 1) {
            NotifyPlugin.success({title: '任务通知', content: `排版任务【${id}】《${originDoc}》执行成功!`});
        } else if (status == 0) {
            NotifyPlugin.error({title: '任务通知', content: `排版任务【${id}】《${originDoc}》执行失败!`});
        }
    }

    ws.onerror = () => {
        console.error("websocket连接失败");
    }

    ws.onclose = () => {
        console.error("websocket连接关闭");
    }
}
