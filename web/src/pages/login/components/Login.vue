<template>
  <t-form
      ref="form"
      :class="['item-container', `login-${type}`]"
      :data="formData"
      :rules="FORM_RULES"
      label-width="0"
      @submit="onSubmit"
  >
    <template v-if="type == 'password'">
      <t-form-item name="username">
        <t-input v-model="formData.username" :placeholder="`${$t('pages.login.input.account')}：admin`" size="large">
          <template #prefix-icon>
            <t-icon name="user"/>
          </template>
        </t-input>
      </t-form-item>

      <t-form-item name="password">
        <t-input
            v-model="formData.password"
            :placeholder="`${$t('pages.login.input.password')}：admin`"
            :type="showPsw ? 'text' : 'password'"
            clearable
            size="large"
        >
          <template #prefix-icon>
            <t-icon name="lock-on"/>
          </template>
          <template #suffix-icon>
            <t-icon :name="showPsw ? 'browse' : 'browse-off'" @click="showPsw = !showPsw"/>
          </template>
        </t-input>
      </t-form-item>

      <t-form-item name="verifyCode">
        <t-row :gutter="24">
          <t-col :span="8">
            <t-input v-model="formData.verifyCode" placeholder="验证码" size="large">
              <template #prefix-icon>
                <t-icon name="secured"/>
              </template>
            </t-input>
          </t-col>
          <t-col :span="4">
            <img :src="formData.img" alt="加载失败"
                 style="border: 1px solid var(--td-border-level-1-color)" @click="refreshCaptcha"/>
          </t-col>
        </t-row>
      </t-form-item>

      <div class="check-container remember-pwd">
        <t-checkbox>{{ $t('pages.login.remember') }}</t-checkbox>
        <span class="tip">{{ $t('pages.login.forget') }}</span>
      </div>
    </template>

    <!-- 扫码登录 -->
    <template v-else-if="type == 'qrcode'">
      <div class="tip-container">
        <span class="tip">{{ $t('pages.login.wechatLogin') }}</span>
        <span class="refresh">{{ $t('pages.login.refresh') }} <t-icon name="refresh"/> </span>
      </div>
      <qrcode-vue :size="160" level="H" value=""/>
    </template>

    <!-- 手机号登录 -->
    <template v-else>
      <t-form-item name="phone">
        <t-input v-model="formData.phone" :placeholder="$t('pages.login.input.phone')" size="large">
          <template #prefix-icon>
            <t-icon name="mobile"/>
          </template>
        </t-input>
      </t-form-item>

      <t-form-item class="verification-code" name="verifyCode">
        <t-input v-model="formData.verifyCode" :placeholder="$t('pages.login.input.verification')" size="large"/>
        <t-button :disabled="countDown > 0" size="large" variant="outline" @click="sendCode">
          {{ countDown == 0 ? $t('pages.login.sendVerification') : `${countDown}秒后可重发` }}
        </t-button>
      </t-form-item>
    </template>

    <t-form-item v-if="type !== 'qrcode'" class="btn-container">
      <t-button block size="large" type="submit"> {{ $t('pages.login.signIn') }}</t-button>
    </t-form-item>

    <div class="switch-container">
      <span v-if="type !== 'password'" class="tip" @click="switchType('password')">{{
          $t('pages.login.accountLogin')
        }}</span>
      <span v-if="type !== 'qrcode'" class="tip" @click="switchType('qrcode')">{{
          $t('pages.login.wechatLogin')
        }}</span>
      <span v-if="type !== 'phone'" class="tip" @click="switchType('phone')">{{ $t('pages.login.phoneLogin') }}</span>
    </div>
  </t-form>
</template>

<script lang="ts" setup>
import QrcodeVue from 'qrcode.vue';
import type {FormInstanceFunctions, FormRule, SubmitContext} from 'tdesign-vue-next';
import {MessagePlugin} from 'tdesign-vue-next';
import {ref} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {sysUserApi} from '@/api/system';

import {useCounter} from '@/hooks';
import {t} from '@/locales';
import {useUserStore} from '@/store';

const userStore = useUserStore();


const INITIAL_DATA = {
  phone: '',
  username: 'admin',
  password: 'admin',
  verifyCode: '8888',
  checked: false,
  uuid: '',
  img: ''
};

const FORM_RULES: Record<string, FormRule[]> = {
  phone: [{required: true, message: t('pages.login.required.phone'), type: 'error'}],
  account: [{required: true, message: t('pages.login.required.account'), type: 'error'}],
  password: [{required: true, message: t('pages.login.required.password'), type: 'error'}],
  verifyCode: [{required: true, message: t('pages.login.required.verification'), type: 'error'}],
};

const type = ref('password');

const form = ref<FormInstanceFunctions>();
const formData = ref({...INITIAL_DATA});
const showPsw = ref(false);

const [countDown, handleCounter] = useCounter();

const switchType = (val: string) => {
  type.value = val;
};

const router = useRouter();
const route = useRoute();

/**
 * 发送验证码
 */
const sendCode = () => {
  form.value.validate({fields: ['phone']}).then((e) => {
    if (e === true) {
      handleCounter();
    }
  });
};

const onSubmit = async (ctx: SubmitContext) => {
  if (ctx.validateResult === true) {
    try {
      await userStore.login(formData.value);

      MessagePlugin.success('登录成功');
      const redirect = route.query.redirect as string;
      const redirectUrl = redirect ? decodeURIComponent(redirect) : '/dashboard';
      router.push(redirectUrl);
    } catch (e) {
      console.log(e);
      MessagePlugin.error(e.message);
    }
  }
};

async function refreshCaptcha() {
  const data = await sysUserApi.captcha();
  Object.assign(formData.value, data);
}

refreshCaptcha();
</script>

<style lang="less" scoped>
@import '../index.less';
</style>
