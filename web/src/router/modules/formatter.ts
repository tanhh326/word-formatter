import Layout from '@/layouts/index.vue';
import {nanoid} from "nanoid";

export default [
    {
        path: '/formatter',
        name: nanoid(),
        component: Layout,
        redirect: '/formatter/cover-form',
        meta: {
            title: {
                zh_CN: '排版',
                en_US: 'Result',
            },
            icon: 'check-circle',
        },
        children: [
            {
                path: 'cover-form',
                name: nanoid(),
                component: () => import('@/pages/formatter/cover-form/index.vue'),
                meta: {
                    title: {
                        zh_CN: '封面',
                        en_US: 'Success',
                    },
                },
            },
            {
                path: 'format-config',
                name: nanoid(),
                component: () => import('@/pages/formatter/format-config/index.vue'),
                meta: {
                    title: {
                        zh_CN: '配置',
                        en_US: 'Success',
                    },
                },
            },
            {
                path: 'formatting',
                name: nanoid(),
                component: () => import('@/pages/formatter/formatting/index.vue'),
                meta: {
                    title: {
                        zh_CN: '执行',
                        en_US: 'Success',
                    },
                },
            },
        ],
    },
];
