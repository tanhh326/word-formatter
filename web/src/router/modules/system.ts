import Layout from '@/layouts/index.vue';
import {nanoid} from "nanoid";

export default [
    {
        path: '/system',
        name: nanoid(),
        component: Layout,
        redirect: '/formatter/cover-form',
        meta: {
            title: {
                zh_CN: '系统设置',
                en_US: 'Result',
            },
            icon: 'check-circle',
        },
        children: [
            {
                path: 'user',
                name: nanoid(),
                component: () => import('@/pages/system/user/index.vue'),
                meta: {
                    title: {
                        zh_CN: '用户',
                        en_US: 'Success',
                    },
                },
            },
        ],
    },
];
